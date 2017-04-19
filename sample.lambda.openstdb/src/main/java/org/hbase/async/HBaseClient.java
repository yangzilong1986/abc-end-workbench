package org.hbase.async;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.google.common.cache.LoadingCache;
import com.google.protobuf.InvalidProtocolBufferException;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.SocketChannel;
import org.jboss.netty.channel.socket.SocketChannelConfig;
import org.jboss.netty.channel.socket.nio.NioChannelConfig;
import org.jboss.netty.channel.socket.nio.NioClientBossPool;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stumbleupon.async.Callback;
import com.stumbleupon.async.Deferred;

import org.hbase.async.generated.ZooKeeperPB;

public final class HBaseClient {


  private static final Logger LOG = LoggerFactory.getLogger(HBaseClient.class);

  /**
   * An empty byte array you can use.  This can be useful for instance with
   * {@link Scanner#setStartKey} and {@link Scanner#setStopKey}.
   */
  public static final byte[] EMPTY_ARRAY = new byte[0];

  /** A byte array containing a single zero byte.  */
  private static final byte[] ZERO_ARRAY = new byte[] { 0 };

  protected static final byte[] ROOT = new byte[] { '-', 'R', 'O', 'O', 'T', '-' };
  protected static final byte[] ROOT_REGION = new byte[] { '-', 'R', 'O', 'O', 'T', '-', ',', ',', '0' };
  protected static final byte[] META = new byte[] { '.', 'M', 'E', 'T', 'A', '.' };
  protected static final byte[] INFO = new byte[] { 'i', 'n', 'f', 'o' };
  protected static final byte[] REGIONINFO = new byte[] { 'r', 'e', 'g', 'i', 'o', 'n', 'i', 'n', 'f', 'o' };
  protected static final byte[] SERVER = new byte[] { 's', 'e', 'r', 'v', 'e', 'r' };
  /** HBase 0.95 and up: .META. is now hbase:meta */
  protected static final byte[] HBASE96_META =
    new byte[] { 'h', 'b', 'a', 's', 'e', ':', 'm', 'e', 't', 'a' };
  /** New for HBase 0.95 and up: the name of META is fixed.  */
  protected static final byte[] META_REGION_NAME =
    new byte[] { 'h', 'b', 'a', 's', 'e', ':', 'm', 'e', 't', 'a', ',', ',', '1' };
  /** New for HBase 0.95 and up: the region info for META is fixed.  */
  protected static final RegionInfo META_REGION =
    new RegionInfo(HBASE96_META, META_REGION_NAME, EMPTY_ARRAY);

  /**
   * In HBase 0.95 and up, this magic number is found in a couple places.
   * It's used in the znode that points to the .META. region, to
   * indicate that the contents of the znode is a protocol buffer.
   * It's also used in the value of the KeyValue found in the .META. table
   * that contain a {@link RegionInfo}, to indicate that the value contents
   * is a protocol buffer.
   */
  static final int PBUF_MAGIC = 1346524486;  // 4 bytes: "PBUF"

  /**
   * Timer we use to handle all our timeouts.
   * TODO(tsuna): Get it through the ctor to share it with others.
   */
  private final HashedWheelTimer timer;
  
  /** A separate timer thread used for processing RPC timeout callbacks. We
   * keep it separate as a bad HBase server can cause a timeout storm and we 
   * don't want to block any flushes and operations on other region servers.
   */
  private final HashedWheelTimer rpc_timeout_timer;

  /** Up to how many milliseconds can we buffer an edit on the client side.  */
  private volatile short flush_interval;
  
  /** How many different counters do we want to keep in memory for buffering. */
  private volatile int increment_buffer_size;
  
  /**
   * Low and high watermarks when buffering RPCs due to an NSRE.
   * @see #handleNSRE
   */
  private int nsre_low_watermark;
  private int nsre_high_watermark;
  
  /**
   * Factory through which we will create all its channels / sockets.
   */
  private final ClientSocketChannelFactory channel_factory;

  /** Watcher to keep track of the -ROOT- region in ZooKeeper.  */
  private final ZKClient zkclient;

  /**
   * The client currently connected to the -ROOT- region.
   * If this is {@code null} then we currently don't know where the -ROOT-
   * region is and we're waiting for a notification from ZooKeeper to tell
   * us where it is.
   * Note that with HBase 0.95, {@link #has_root} would be false, and this
   * would instead point to the .META. region.
   */
  private volatile RegionClient rootregion;

  /**
   * Whether or not there is a -ROOT- region.
   * When connecting to HBase 0.95 and up, this would be set to false, so we
   * would go straight to .META. instead.
   */
  volatile boolean has_root = true;

  private final ConcurrentSkipListMap<byte[], RegionInfo> regions_cache =
    new ConcurrentSkipListMap<byte[], RegionInfo>(RegionInfo.REGION_NAME_CMP);

  /**
   * Maps a {@link RegionInfo} to the client currently connected to the
   * RegionServer that serves this region.
   * <p>
   * The opposite mapping is stored in {@link #client2regions}.
   * There's no consistency guarantee with that other map.
   * See the javadoc for {@link #regions_cache} regarding consistency.
   */
  private final ConcurrentHashMap<RegionInfo, RegionClient> region2client =
    new ConcurrentHashMap<RegionInfo, RegionClient>();

  /**
   * Maps a client connected to a RegionServer to the list of regions we know
   * it's serving so far.
   * <p>
   * The opposite mapping is stored in {@link #region2client}.
   * There's no consistency guarantee with that other map.
   * See the javadoc for {@link #regions_cache} regarding consistency.
   * <p>
   * Each list in the map is protected by its own monitor lock.
   */
  private final ConcurrentHashMap<RegionClient, ArrayList<RegionInfo>>
    client2regions = new ConcurrentHashMap<RegionClient, ArrayList<RegionInfo>>();


  private final HashMap<String, RegionClient> ip2client =
    new HashMap<String, RegionClient>();


  private final ConcurrentSkipListMap<byte[], ArrayList<HBaseRpc>> got_nsre =
    new ConcurrentSkipListMap<byte[], ArrayList<HBaseRpc>>(RegionInfo.REGION_NAME_CMP);


  private volatile LoadingCache<BufferedIncrement, BufferedIncrement.Amount> increment_buffer;

  /** The configuration for this client */
  private final Config config;
  
  /** Integers for thread naming */
  final static AtomicInteger BOSS_THREAD_ID = new AtomicInteger();
  final static AtomicInteger WORKER_THREAD_ID = new AtomicInteger();
  final static AtomicInteger TIMER_THREAD_ID = new AtomicInteger();
  
  /** Default RPC timeout in milliseconds from the config */
  private final int rpc_timeout;
  
  // ------------------------ //
  // Client usage statistics. //
  // ------------------------ //

  /** Number of connections created by {@link #newClient}.  */
  private final Counter num_connections_created = new Counter();

  /** How many {@code -ROOT-} lookups were made.  */
  private final Counter root_lookups = new Counter();

  /** How many {@code .META.} lookups were made (with a permit).  */
  private final Counter meta_lookups_with_permit = new Counter();

  /** How many {@code .META.} lookups were made (without a permit).  */
  private final Counter meta_lookups_wo_permit = new Counter();

  /** Number of calls to {@link #flush}.  */
  private final Counter num_flushes = new Counter();

  /** Number of NSREs handled by {@link #handleNSRE}.  */
  private final Counter num_nsres = new Counter();

  /** Number of RPCs delayed by {@link #handleNSRE}.  */
  private final Counter num_nsre_rpcs = new Counter();

  /** Number of {@link MultiAction} sent to the network.  */
  final Counter num_multi_rpcs = new Counter();

  /** Number of calls to {@link #get}.  */
  private final Counter num_gets = new Counter();

  /** Number of calls to {@link #openScanner}.  */
  private final Counter num_scanners_opened = new Counter();

  /** Number of calls to {@link #scanNextRows}.  */
  private final Counter num_scans = new Counter();

  /** Number calls to {@link #put}.  */
  private final Counter num_puts = new Counter();

  /** Number calls to {@link #append}.  */
  private final Counter num_appends = new Counter();
  
  /** Number calls to {@link #lockRow}.  */
  private final Counter num_row_locks = new Counter();

  /** Number calls to {@link #delete}.  */
  private final Counter num_deletes = new Counter();

  /** Number of {@link AtomicIncrementRequest} sent.  */
  private final Counter num_atomic_increments = new Counter();
  
  /** Number of region clients closed due to being idle.  */
  private final Counter idle_connections_closed = new Counter();

  /**
   * Constructor.
   * @param quorum_spec The specification of the quorum, e.g.
   * {@code "host1,host2,host3"}.
   */
  public HBaseClient(final String quorum_spec) {
    this(quorum_spec, "/hbase");
  }

  /**
   * Constructor.
   * @param quorum_spec The specification of the quorum, e.g.
   * {@code "host1,host2,host3"}.
   * @param base_path The base path under which is the znode for the
   * -ROOT- region.
   */
  public HBaseClient(final String quorum_spec, final String base_path) {
    this(quorum_spec, base_path, defaultChannelFactory(new Config()));
  }


  public HBaseClient(final String quorum_spec, final String base_path,
                     final Executor executor) {
    this(quorum_spec, base_path, new CustomChannelFactory(executor));
  }


  public HBaseClient(final String quorum_spec, final String base_path,
                     final ClientSocketChannelFactory channel_factory) {
    this.channel_factory = channel_factory;
    zkclient = new ZKClient(quorum_spec, base_path);
    config = new Config();
    rpc_timeout = config.getInt("hbase.rpc.timeout");
    timer = newTimer(config, "HBaseClient");
    rpc_timeout_timer = newTimer(config, "RPC Timeout Timer");
    flush_interval = config.getShort("hbase.rpcs.buffered_flush_interval");
    increment_buffer_size = config.getInt("hbase.increments.buffer_size");
    nsre_low_watermark = config.getInt("hbase.nsre.low_watermark");
    nsre_high_watermark = config.getInt("hbase.nsre.high_watermark");
  }
  
  public HBaseClient(final Config config) {
    this(config, defaultChannelFactory(config));
  }
  

  public HBaseClient(final Config config, final Executor executor) {
    this(config, new CustomChannelFactory(executor));
  }
  

  public HBaseClient(final Config config, 
      final ClientSocketChannelFactory channel_factory) {
    this.channel_factory = channel_factory;
    //连接ZK
    zkclient = new ZKClient(config.getString("hbase.zookeeper.quorum"), 
        config.getString("hbase.zookeeper.znode.parent"));
    this.config = config;
    rpc_timeout = config.getInt("hbase.rpc.timeout");
    timer = newTimer(config, "HBaseClient");
    rpc_timeout_timer = newTimer(config, "RPC Timeout Timer");
    flush_interval = config.getShort("hbase.rpcs.buffered_flush_interval");
    increment_buffer_size = config.getInt("hbase.increments.buffer_size");
    nsre_low_watermark = config.getInt("hbase.nsre.low_watermark");
    nsre_high_watermark = config.getInt("hbase.nsre.high_watermark");
  }
  

  static HashedWheelTimer newTimer(final Config config, final String name) {
    class TimerThreadNamer implements ThreadNameDeterminer {
      @Override
      public String determineThreadName(String currentThreadName,
          String proposedThreadName) throws Exception {
        return "AsyncHBase Timer " + name + " #" + TIMER_THREAD_ID.incrementAndGet();
      }
    }
    if (config == null) {
      return new HashedWheelTimer(Executors.defaultThreadFactory(), 
          new TimerThreadNamer(), 100, MILLISECONDS, 512);
    }
    return new HashedWheelTimer(Executors.defaultThreadFactory(), 
        new TimerThreadNamer(), config.getShort("hbase.timer.tick"), 
        MILLISECONDS, config.getInt("hbase.timer.ticks_per_wheel"));
  }
  
  /** Creates a default channel factory in case we haven't been given one. 
   * The factory will use Netty defaults and provide thread naming rules for
   * easier debugging.
   * @param config The config to pull settings from 
   */
  private static NioClientSocketChannelFactory defaultChannelFactory(
      final Config config) {
    class BossThreadNamer implements ThreadNameDeterminer {
      @Override
      public String determineThreadName(String currentThreadName,
          String proposedThreadName) throws Exception {
        return "AsyncHBase I/O Boss #" + BOSS_THREAD_ID.incrementAndGet();
      }
    }
    
    class WorkerThreadNamer implements ThreadNameDeterminer {
      @Override
      public String determineThreadName(String currentThreadName,
          String proposedThreadName) throws Exception {
        return "AsyncHBase I/O Worker #" + WORKER_THREAD_ID.incrementAndGet();
      }
    }
    
    final Executor executor = Executors.newCachedThreadPool();
    final NioClientBossPool boss_pool = 
        new NioClientBossPool(executor, 1, newTimer(config, "Boss Pool"), 
            new BossThreadNamer());
    final int num_workers = config.hasProperty("hbase.workers.size") ? 
        config.getInt("hbase.workers.size") : 
          Runtime.getRuntime().availableProcessors() * 2;
    final NioWorkerPool worker_pool = new NioWorkerPool(executor, 
        num_workers, new WorkerThreadNamer());
    return new NioClientSocketChannelFactory(boss_pool, worker_pool);
  }

  /** A custom channel factory that doesn't shutdown its executor.  */
  private static final class CustomChannelFactory
    extends NioClientSocketChannelFactory {
      CustomChannelFactory(final Executor executor) {
        super(executor, executor);
      }
      @Override
      public void releaseExternalResources() {
        // Do nothing, we don't want to shut down the executor.
      }
  }

  /**
   * Returns a snapshot of usage statistics for this client.
   * @since 1.3
   */
  public ClientStats stats() {
    final LoadingCache<BufferedIncrement, BufferedIncrement.Amount> cache =
      increment_buffer;
    
    long inflight_rpcs = 0;
    long pending_rpcs = 0;
    long pending_batched_rpcs = 0;
    int dead_region_clients = 0;
    
    final Collection<RegionClient> region_clients = client2regions.keySet();
    
    for (final RegionClient rc : region_clients) {
      final RegionClientStats stats = rc.stats();
      inflight_rpcs += stats.inflightRPCs();
      pending_rpcs += stats.pendingRPCs();
      pending_batched_rpcs += stats.pendingBatchedRPCs();
      if (stats.isDead()) {
        dead_region_clients++;
      }
    }
    
    return new ClientStats(
      num_connections_created.get(),
      root_lookups.get(),
      meta_lookups_with_permit.get(),
      meta_lookups_wo_permit.get(),
      num_flushes.get(),
      num_nsres.get(),
      num_nsre_rpcs.get(),
      num_multi_rpcs.get(),
      num_gets.get(),
      num_scanners_opened.get(),
      num_scans.get(),
      num_puts.get(),
      num_appends.get(),
      num_row_locks.get(),
      num_deletes.get(),
      num_atomic_increments.get(),
      cache != null ? cache.stats() : BufferedIncrement.ZERO_STATS,
      inflight_rpcs,
      pending_rpcs,
      pending_batched_rpcs,
      dead_region_clients,
      region_clients.size(),
      idle_connections_closed.get()
    );
  }

  /**
   * Returns a list of region client stats objects for debugging.
   * @return A list of region client statistics
   * @since 1.7
   */
  public List<RegionClientStats> regionStats() {
    final Collection<RegionClient> region_clients = client2regions.keySet();
    final List<RegionClientStats> stats = 
        new ArrayList<RegionClientStats>(region_clients.size());
    for (final RegionClient rc : region_clients) {
      stats.add(rc.stats());
    }
    return stats;
  }
  
  public Deferred<Object> flush() {
    {
      // If some RPCs are waiting for -ROOT- to be discovered, we too must wait
      // because some of those RPCs could be edits that we must wait on.
      final Deferred<Object> d = zkclient.getDeferredRootIfBeingLookedUp();
      if (d != null) {
        LOG.debug("Flush needs to wait on {} to come back",
                  has_root ? "-ROOT-" : ".META.");
        final class RetryFlush implements Callback<Object, Object> {
          public Object call(final Object arg) {
            LOG.debug("Flush retrying after {} came back",
                      has_root ? "-ROOT-" : ".META.");
            return flush();
          }
          public String toString() {
            return "retry flush";
          }
        }
        return d.addBoth(new RetryFlush());
      }
    }

    num_flushes.increment();
    final boolean need_sync;
    {
      final LoadingCache<BufferedIncrement, BufferedIncrement.Amount> buf =
        increment_buffer;  // Single volatile-read.
      if (buf != null && !buf.asMap().isEmpty()) {
        flushBufferedIncrements(buf);
        need_sync = true;
      } else {
        need_sync = false;
      }
    }
    final ArrayList<Deferred<Object>> d =
      new ArrayList<Deferred<Object>>(client2regions.size()
                                      + got_nsre.size() * 8);
    // Bear in mind that we're traversing a ConcurrentHashMap, so we may get
    // clients that have been removed from the map since we started iterating.
    for (final RegionClient client : client2regions.keySet()) {
      d.add(need_sync ? client.sync() : client.flush());
    }
    for (final ArrayList<HBaseRpc> nsred : got_nsre.values()) {
      synchronized (nsred) {
        for (final HBaseRpc rpc : nsred) {
          if (rpc instanceof HBaseRpc.IsEdit) {
            d.add(rpc.getDeferred());
          }
        }
      }
    }
    @SuppressWarnings("unchecked")
    final Deferred<Object> flushed = (Deferred) Deferred.group(d);
    return flushed;
  }

  public short setFlushInterval(final short flush_interval) {
    // Note: if we have buffered increments, they'll pick up the new flush
    // interval next time the current timer fires.
    if (flush_interval < 0) {
      throw new IllegalArgumentException("Negative: " + flush_interval);
    }
    final short prev = config.getShort("hbase.rpcs.buffered_flush_interval");
    config.overrideConfig("hbase.rpcs.buffered_flush_interval", 
        Short.toString(flush_interval));
    this.flush_interval = flush_interval; 
    return prev;
  }

  public int setIncrementBufferSize(final int increment_buffer_size) {
    if (increment_buffer_size < 0) {
      throw new IllegalArgumentException("Negative: " + increment_buffer_size);
    }
    final int current = config.getInt("hbase.increments.buffer_size");
    if (current == increment_buffer_size) {
      return current;
    }
    config.overrideConfig("hbase.increments.buffer_size", 
        Integer.toString(increment_buffer_size));
    this.increment_buffer_size = increment_buffer_size;
    final LoadingCache<BufferedIncrement, BufferedIncrement.Amount> prev =
      increment_buffer;  // Volatile-read.
    if (prev != null) {  // Need to resize.
      makeIncrementBuffer();  // Volatile-write.
      flushBufferedIncrements(prev);
    }
    return current;
  }

  public Timer getTimer() {
    return timer;
  }

  /**
   * Return the configuration object for this client
   * @return The config object for this client
   * @since 1.7
   */
  public Config getConfig() {
    return config;
  }
  
  /**
   * Schedules a new timeout.
   * @param task The task to execute when the timer times out.
   * @param timeout_ms The timeout, in milliseconds (strictly positive).
   */
  void newTimeout(final TimerTask task, final long timeout_ms) {
    try {
      timer.newTimeout(task, timeout_ms, MILLISECONDS);
    } catch (IllegalStateException e) {
      // This can happen if the timer fires just before shutdown()
      // is called from another thread, and due to how threads get
      // scheduled we tried to call newTimeout() after timer.stop().
      LOG.warn("Failed to schedule timer."
               + "  Ignore this if we're shutting down.", e);
    }
  }

  public short getFlushInterval() {
    return flush_interval;
  }

  public int getDefaultRpcTimeout() {
    return rpc_timeout;
  }
  
  public int getIncrementBufferSize() {
    return increment_buffer_size;
  }


  public Deferred<Object> shutdown() {
    //关闭线程
    final class ShutdownThread extends Thread {
      ShutdownThread() {
        super("HBaseClient@" + HBaseClient.super.hashCode() + " shutdown");
      }
      public void run() {
        // This terminates the Executor.
        channel_factory.releaseExternalResources();
      }
    };

    // 3. Release all other resources.
    final class ReleaseResourcesCB implements Callback<Object, Object> {
      public Object call(final Object arg) {
        LOG.debug("Releasing all remaining resources");
        timer.stop();
        rpc_timeout_timer.stop();
        new ShutdownThread().start();
        return arg;
      }
      public String toString() {
        return "release resources callback";
      }
    }

    // 2. Terminate all connections.
    final class DisconnectCB implements Callback<Object, Object> {
      public Object call(final Object arg) {
        //释放连接
        return disconnectEverything().addCallback(new ReleaseResourcesCB());
      }
      public String toString() {
        return "disconnect callback";
      }
    }

    // If some RPCs are waiting for -ROOT- to be discovered, we too must wait
    // because some of those RPCs could be edits that we must not lose.
    final Deferred<Object> d = zkclient.getDeferredRootIfBeingLookedUp();
    if (d != null) {
      LOG.debug("Shutdown needs to wait on {} to come back",
                has_root ? "-ROOT-" : ".META.");
      final class RetryShutdown implements Callback<Object, Object> {
        public Object call(final Object arg) {
          LOG.debug("Shutdown retrying after {} came back",
                    has_root ? "-ROOT-" : ".META.");
          return shutdown();
        }
        public String toString() {
          return "retry shutdown";
        }
      }
      return d.addBoth(new RetryShutdown());
    }

    // 1. Flush everything.
    return flush().addCallback(new DisconnectCB());
  }

  /**
   * Closes every socket, which will also flush all internal region caches.
   */
  private Deferred<Object> disconnectEverything() {
    HashMap<String, RegionClient> ip2client_copy;

    synchronized (ip2client) {
      // Make a local copy so we can shutdown every Region Server client
      // without hold the lock while we iterate over the data structure.
      ip2client_copy = new HashMap<String, RegionClient>(ip2client);
    }

    final ArrayList<Deferred<Object>> d =
      new ArrayList<Deferred<Object>>(ip2client_copy.values().size() + 1);
    // Shut down all client connections, clear cache.
    for (final RegionClient client : ip2client_copy.values()) {
      d.add(client.shutdown());
    }
    if (rootregion != null && rootregion.isAlive()) {
      // It's OK if we already did that in the loop above.
      d.add(rootregion.shutdown());
    }
    ip2client_copy = null;

    final int size = d.size();
    return Deferred.group(d).addCallback(
      new Callback<Object, ArrayList<Object>>() {
        public Object call(final ArrayList<Object> arg) {
          // Normally, now that we've shutdown() every client, all our caches should
          // be empty since each shutdown() generates a DISCONNECTED event, which
          // causes RegionClientPipeline to call removeClientFromCache().
          HashMap<String, RegionClient> logme = null;
          synchronized (ip2client) {
            if (!ip2client.isEmpty()) {
              logme = new HashMap<String, RegionClient>(ip2client);
            }
          }
          if (logme != null) {
            // Putting this logging statement inside the synchronized block
            // can lead to a deadlock, since HashMap.toString() is going to
            // call RegionClient.toString() on each entry, and this locks the
            // client briefly.  Other parts of the code lock clients first and
            // the ip2client HashMap second, so this can easily deadlock.
            LOG.error("Some clients are left in the client cache and haven't"
                      + " been cleaned up: " + logme);
            logme = null;
            return disconnectEverything();  // Try again.
          }
          zkclient.disconnectZK();
          return arg;
        }
        public String toString() {
          return "wait " + size + " RegionClient.shutdown()";
        }
      });
  }

  public Deferred<Object> ensureTableFamilyExists(final String table,
                                                  final String family) {
    return ensureTableFamilyExists(table.getBytes(), family.getBytes());
  }

  public Deferred<Object> ensureTableFamilyExists(final byte[] table,
                                                  final byte[] family) {
    // Just "fault in" the first region of the table.  Not the most optimal or
    // useful thing to do but gets the job done for now.  TODO(tsuna): Improve.
    final HBaseRpc dummy;
    if (family == EMPTY_ARRAY) {
      dummy = GetRequest.exists(table, probeKey(ZERO_ARRAY));
    } else {
      dummy = GetRequest.exists(table, probeKey(ZERO_ARRAY), family);
    }
    @SuppressWarnings("unchecked")
    final Deferred<Object> d = (Deferred) sendRpcToRegion(dummy);
    return d;
  }

  public Deferred<Object> ensureTableExists(final String table) {
    return ensureTableFamilyExists(table.getBytes(), EMPTY_ARRAY);
  }

  public Deferred<Object> ensureTableExists(final byte[] table) {
    return ensureTableFamilyExists(table, EMPTY_ARRAY);
  }

  /**
   * Retrieves data from HBase.
   * @param request The {@code get} request.
   * @return A deferred list of key-values that matched the get request.
   */
  public Deferred<ArrayList<KeyValue>> get(final GetRequest request) {
    num_gets.increment();
    return sendRpcToRegion(request).addCallbacks(got, Callback.PASSTHROUGH);
  }

  /** Singleton callback to handle responses of "get" RPCs.  */
  private static final Callback<ArrayList<KeyValue>, Object> got =
    new Callback<ArrayList<KeyValue>, Object>() {
      public ArrayList<KeyValue> call(final Object response) {
        if (response instanceof ArrayList) {
          final ArrayList<KeyValue> row = (ArrayList<KeyValue>) response;
          return row;
        } else {
          throw new InvalidResponseException(ArrayList.class, response);
        }
      }
      public String toString() {
        return "type get response";
      }
    };


  public Scanner newScanner(final byte[] table) {
    return new Scanner(this, table);
  }

  public Scanner newScanner(final String table) {
    return new Scanner(this, table.getBytes());
  }


  Deferred<Object> openScanner(final Scanner scanner) {
    num_scanners_opened.increment();
    return sendRpcToRegion(scanner.getOpenRequest()).addCallbacks(
      scanner_opened,
      new Callback<Object, Object>() {
        public Object call(final Object error) {
          // Don't let the scanner think it's opened on this region.
          scanner.invalidate();
          return error;  // Let the error propagate.
        }
        public String toString() {
          return "openScanner errback";
        }
      });
  }

  /** Singleton callback to handle responses of "openScanner" RPCs.  */
  private static final Callback<Object, Object> scanner_opened =
    new Callback<Object, Object>() {
      public Object call(final Object response) {
        if (response instanceof Scanner.Response) {  // HBase 0.95 and up
          return (Scanner.Response) response;
        } else if (response instanceof Long) {
          // HBase 0.94 and before: we expect just a long (the scanner ID).
          return (Long) response;
        } else {
          throw new InvalidResponseException(Long.class, response);
        }
      }
      public String toString() {
        return "type openScanner response";
      }
    };

  /**
   * Returns the client currently known to hose the given region, or NULL.
   */
  private RegionClient clientFor(final RegionInfo region) {
    if (region == null) {
      return null;
    } else if (region == META_REGION || Bytes.equals(region.table(), ROOT)) {
      // HBase 0.95+: META_REGION (which is 0.95 specific) is our root.
      // HBase 0.94 and earlier: if we're looking for -ROOT-, stop here.
      return rootregion;
    }
    return region2client.get(region);
  }

  /**
   * Package-private access point for {@link Scanner}s to scan more rows.
   * @param scanner The scanner to use.
   * @param nrows The maximum number of rows to retrieve.
   * @return A deferred row.
   */
  Deferred<Object> scanNextRows(final Scanner scanner) {
    final RegionInfo region = scanner.currentRegion();
    final RegionClient client = clientFor(region);
    if (client == null) {
      // Oops, we no longer know anything about this client or region.  Our
      // cache was probably invalidated while the client was scanning.  This
      // means that we lost the connection to that RegionServer, so we have to
      // re-open this scanner if we wanna keep scanning.
      scanner.invalidate();        // Invalidate the scanner so that ...
      final Deferred<Object> d = (Deferred) scanner.nextRows();
      return d;  // ... this will re-open it ______.^
    }
    num_scans.increment();
    final HBaseRpc next_request = scanner.getNextRowsRequest();
    final Deferred<Object> d = next_request.getDeferred();
    client.sendRpc(next_request);
    return d;
  }

  /**
   * Package-private access point for {@link Scanner}s to close themselves.
   * @param scanner The scanner to close.
   * @return A deferred object that indicates the completion of the request.
   * The {@link Object} has not special meaning and can be {@code null}.
   */
  Deferred<Object> closeScanner(final Scanner scanner) {
    final RegionInfo region = scanner.currentRegion();
    final RegionClient client = clientFor(region);
    if (client == null) {
      LOG.warn("Cannot close " + scanner + " properly, no connection open for "
               + Bytes.pretty(region == null ? null : region.name()));
      return Deferred.fromResult(null);
    }
    final HBaseRpc close_request = scanner.getCloseRequest();
    final Deferred<Object> d = close_request.getDeferred();
    client.sendRpc(close_request);
    return d;
  }

  /**
   * Atomically and durably increments a value in HBase.
   * <p>
   * This is equivalent to
   * {@link #atomicIncrement(AtomicIncrementRequest, boolean) atomicIncrement}
   * {@code (request, true)}
   * @param request The increment request.
   * @return The deferred {@code long} value that results from the increment.
   */
  public Deferred<Long> atomicIncrement(final AtomicIncrementRequest request) {
    num_atomic_increments.increment();
    return sendRpcToRegion(request).addCallbacks(icv_done,
                                                 Callback.PASSTHROUGH);
  }


  public Deferred<Long> bufferAtomicIncrement(final AtomicIncrementRequest request) {
    final long value = request.getAmount();
    if (!BufferedIncrement.Amount.checkOverflow(value)  // Value too large.
        || flush_interval == 0) {           // Client-side buffer disabled.
      return atomicIncrement(request);
    }

    final BufferedIncrement incr =
      new BufferedIncrement(request.table(), request.key(), request.family(),
                            request.qualifier());

    do {
      BufferedIncrement.Amount amount;
      // Semi-evil: the very first time we get here, `increment_buffer' will
      // still be null (we don't initialize it in our constructor) so we catch
      // the NPE that ensues to allocate the buffer and kick off a timer to
      // regularly flush it.
      try {
        amount = increment_buffer.getUnchecked(incr);
      } catch (NullPointerException e) {
        setupIncrementCoalescing();
        amount = increment_buffer.getUnchecked(incr);
      }
      if (amount.update(value)) {
        final Deferred<Long> deferred = new Deferred<Long>();
        amount.deferred.chain(deferred);
        return deferred;
      }
      // else: Loop again to retry.
      increment_buffer.refresh(incr);
    } while (true);
  }

  /**
   * Called the first time we get a buffered increment.
   * Lazily creates the increment buffer and sets up a timer to regularly
   * flush buffered increments.
   */
  private synchronized void setupIncrementCoalescing() {
    if (increment_buffer != null) {
      return;
    }
    makeIncrementBuffer();  // Volatile-write.

    // Start periodic buffered increment flushes.
    final class FlushBufferedIncrementsTimer implements TimerTask {
      public void run(final Timeout timeout) {
        try {
          flushBufferedIncrements(increment_buffer);
        } finally {
          final short interval = flush_interval; // Volatile-read.
          newTimeout(this, interval > 0 ? interval : 100);
        }
      }
    }
    final short interval = flush_interval; // Volatile-read.
    // Handle the extremely unlikely yet possible racy case where:
    //   flush_interval was > 0
    //   A buffered increment came in
    //   It was the first one ever so we landed here
    //   Meanwhile setFlushInterval(0) to disable buffering
    // In which case we just flush whatever we have in 1ms.
    timer.newTimeout(new FlushBufferedIncrementsTimer(),
                     interval > 0 ? interval : 1, MILLISECONDS);
  }

  /**
   * Flushes all buffered increments.
   * @param increment_buffer The buffer to flush.
   */
  private static void flushBufferedIncrements(// JAVA Y U NO HAVE TYPEDEF? F U!
    final LoadingCache<BufferedIncrement, BufferedIncrement.Amount> increment_buffer) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Flushing " + increment_buffer.size() + " buffered increments");
    }
    synchronized (increment_buffer) {
      increment_buffer.invalidateAll();
    }
  }

  /**
   * Creates the increment buffer according to current configuration.
   */
  private void makeIncrementBuffer() {
    final int size = increment_buffer_size;
    increment_buffer = BufferedIncrement.newCache(this, size);
    if (LOG.isDebugEnabled()) {
      LOG.debug("Created increment buffer of " + size + " entries");
    }
  }

  /** Singleton callback to handle responses of incrementColumnValue RPCs.  */
  private static final Callback<Long, Object> icv_done =
    new Callback<Long, Object>() {
      public Long call(final Object response) {
        if (response instanceof Long) {
          return (Long) response;
        } else {
          throw new InvalidResponseException(Long.class, response);
        }
      }
      public String toString() {
        return "type incrementColumnValue response";
      }
    };

  public Deferred<Long> atomicIncrement(final AtomicIncrementRequest request,
                                        final boolean durable) {
    request.setDurable(durable);
    return atomicIncrement(request);
  }

  public Deferred<Object> put(final PutRequest request) {
    num_puts.increment();
    return sendRpcToRegion(request);
  }

  public Deferred<Object> append(final AppendRequest request) {
    num_appends.increment();
    return sendRpcToRegion(request);
  }
  

  public Deferred<Boolean> compareAndSet(final PutRequest edit,
                                         final byte[] expected) {
    return sendRpcToRegion(new CompareAndSetRequest(edit, expected))
      .addCallback(CAS_CB);
  }

  public Deferred<Boolean> compareAndSet(final PutRequest edit,
                                         final String expected) {
    return compareAndSet(edit, expected.getBytes());
  }

  public Deferred<Boolean> atomicCreate(final PutRequest edit) {
    return compareAndSet(edit, EMPTY_ARRAY);
  }

  /** Callback to type-check responses of {@link CompareAndSetRequest}.  */
  private static final class CompareAndSetCB implements Callback<Boolean, Object> {

    public Boolean call(final Object response) {
      if (response instanceof Boolean) {
        return (Boolean)response;
      } else {
        throw new InvalidResponseException(Boolean.class, response);
      }
    }

    public String toString() {
      return "type compareAndSet response";
    }

  }

  /** Singleton callback for responses of {@link CompareAndSetRequest}.  */
  private static final CompareAndSetCB CAS_CB = new CompareAndSetCB();

  public Deferred<RowLock> lockRow(final RowLockRequest request) {
    num_row_locks.increment();
    return sendRpcToRegion(request).addCallbacks(
      new Callback<RowLock, Object>() {
        public RowLock call(final Object response) {
          if (response instanceof Long) {
            return new RowLock(request.getRegion().name(), (Long) response);
          } else {
            throw new InvalidResponseException(Long.class, response);
          }
        }
        public String toString() {
          return "type lockRow response";
        }
      }, Callback.PASSTHROUGH);
  }

  public Deferred<Object> unlockRow(final RowLock lock) {
    final byte[] region_name = lock.region();
    final RegionInfo region = regions_cache.get(region_name);
    if (knownToBeNSREd(region)) {
      return Deferred.fromResult(null);
    }
    final RegionClient client = clientFor(region);
    if (client == null) {

      LOG.warn("Cannot release " + lock + ", no connection open for "
               + Bytes.pretty(region_name));
      return Deferred.fromResult(null);
    }
    final HBaseRpc release = new RowLockRequest.ReleaseRequest(lock, region);
    release.setRegion(region);
    final Deferred<Object> d = release.getDeferred();
    client.sendRpc(release);
    return d;
  }

  public Deferred<Object> delete(final DeleteRequest request) {
    num_deletes.increment();
    return sendRpcToRegion(request);
  }

  public Deferred<Object> prefetchMeta(final String table) {
    return prefetchMeta(table.getBytes(), EMPTY_ARRAY, EMPTY_ARRAY);
  }

  public Deferred<Object> prefetchMeta(final String table,
                                       final String start,
                                       final String stop) {
    return prefetchMeta(table.getBytes(), start.getBytes(), stop.getBytes());
  }

  public Deferred<Object> prefetchMeta(final byte[] table) {
    return prefetchMeta(table, EMPTY_ARRAY, EMPTY_ARRAY);
  }


  public Deferred<Object> prefetchMeta(final byte[] table,
                                       final byte[] start,
                                       final byte[] stop) {
    return findTableRegions(table, start, stop, true, false);
  }
  

  private Deferred<Object> findTableRegions(final byte[] table,
                                        final byte[] start,
                                        final byte[] stop, 
                                        final boolean cache,
                                        final boolean return_locations) {

    if (Bytes.equals(table, HBASE96_META) || Bytes.equals(table, META) 
        || Bytes.equals(table, ROOT)) {
      return Deferred.fromResult(null);
    }
    
    // Create the scan bounds.
    final byte[] meta_start = createRegionSearchKey(table, start);
    // In this case, we want the scan to start immediately at the
    // first entry, but createRegionSearchKey finds the last entry.
    meta_start[meta_start.length - 1] = 0;

    final byte[] meta_stop;
    if (stop.length == 0) {
      meta_stop = createRegionSearchKey(table, stop); // will return "table,,:"
      meta_stop[table.length] = 0;  // now have "table\0,:"
      meta_stop[meta_stop.length - 1] = ',';  // now have "table\0,,"
    } else {
      meta_stop = createRegionSearchKey(table, stop);
    }
    
    if (rootregion == null) {
      class Retry implements Callback<Object, Object> {
        @Override
        public Object call(final Object unused) {
          return findTableRegions(table, start, stop, cache, return_locations);
        }
        @Override
        public String toString() {
          return "retry (" + Bytes.pretty(table) + ", "
            + Bytes.pretty(start) + ", " + Bytes.pretty(stop) + ")";
        }
      }
      return ensureTableExists(table).addCallback(new Retry());
    }
    
    final List<RegionLocation> regions = 
        return_locations ? new ArrayList<RegionLocation>() : null;

    final Scanner meta_scanner = newScanner(has_root ? META : HBASE96_META);
    meta_scanner.setStartKey(meta_start);
    meta_scanner.setStopKey(meta_stop);
    
    class MetaScanner
      implements Callback<Object, ArrayList<ArrayList<KeyValue>>> {
      @Override
      public Object call(final ArrayList<ArrayList<KeyValue>> results) {
        if (results != null && !results.isEmpty()) {
          for (final ArrayList<KeyValue> row : results) {
            if (return_locations) {
              final RegionLocation region_location = toRegionLocation(row);
              if (region_location != null) {
                regions.add(region_location);
              }
            }
            if (cache) {
              discoverRegion(row);
            }
          }
          return meta_scanner.nextRows().addCallback(this);
        }
        return regions;
      }
      @Override
      public String toString() {
        return "MetaScanner scanner=" + meta_scanner;
      }
    }

    return meta_scanner.nextRows().addCallback(new MetaScanner());
  }
  
  Deferred<Object> sendRpcToRegion(final HBaseRpc request) {
    if (cannotRetryRequest(request)) {
      return tooManyAttempts(request, null);
    }
    request.attempt++;
    final byte[] table = request.table;
    final byte[] key = request.key;
    final RegionInfo region = getRegion(table, key);

    final class RetryRpc implements Callback<Deferred<Object>, Object> {
      public Deferred<Object> call(final Object arg) {
        if (arg instanceof NonRecoverableException) {
          // No point in retrying here, so fail the RPC.
          HBaseException e = (NonRecoverableException) arg;
          if (e instanceof HasFailedRpcException
              && ((HasFailedRpcException) e).getFailedRpc() != request) {
            e = e.make(e, request);  // e is likely a PleaseThrottleException.
          }
          request.callback(e);
          return Deferred.fromError(e);
        }
        return sendRpcToRegion(request);  // Retry the RPC.
      }
      public String toString() {
        return "retry RPC";
      }
    }

    if (region != null) {
      if (knownToBeNSREd(region)) {
        final NotServingRegionException nsre =
          new NotServingRegionException("Region known to be unavailable",
                                        request);
        final Deferred<Object> d = request.getDeferred();
        handleNSRE(request, region.name(), nsre);
        return d;
      }
      final RegionClient client = clientFor(region);
      if (client != null && client.isAlive()) {
        request.setRegion(region);
        final Deferred<Object> d = request.getDeferred();
        client.sendRpc(request);
        return d;
      }
    }
    return locateRegion(request, table, key).addBothDeferring(new RetryRpc());
  }

  @Deprecated
  public long rootLookupCount() {
    return root_lookups.get();
  }

  @Deprecated
  public long uncontendedMetaLookupCount() {
    return meta_lookups_with_permit.get();
  }

  @Deprecated
  public long contendedMetaLookupCount() {
    return meta_lookups_wo_permit.get();
  }

  boolean cannotRetryRequest(final HBaseRpc rpc) {
    return rpc.attempt > config.getInt("hbase.client.retries.number");
  }

  static Deferred<Object> tooManyAttempts(final HBaseRpc request,
                                          final HBaseException cause) {

    final Exception e = new NonRecoverableException("Too many attempts: "
                                                    + request, cause);
    request.callback(e);
    return Deferred.fromError(e);
  }

  private RegionLocation toRegionLocation(final ArrayList<KeyValue> meta_row) {
    // TODO - there's a fair bit of duplication here with the discoverRegion()
    // code. Try cleaning it up.
    if (meta_row.isEmpty()) {
      throw new TableNotFoundException();
    }
    String host = null;
    int port = -1;
    RegionInfo region = null;
    byte[] start_key = null;
    
    for (final KeyValue kv : meta_row) {
      final byte[] qualifier = kv.qualifier();
      if (Arrays.equals(REGIONINFO, qualifier)) {
        final byte[][] tmp = new byte[1][];  // Yes, this is ugly.
        region = RegionInfo.fromKeyValue(kv, tmp);
        start_key = tmp[0];
      } else if (Arrays.equals(SERVER, qualifier)
              && kv.value() != EMPTY_ARRAY) {  // Empty during NSRE.
        final byte[] hostport = kv.value();
        int colon = hostport.length - 1;
        for (/**/; colon > 0 /* Can't be at the beginning */; colon--) {
          if (hostport[colon] == ':') {
            break;
          }
        }
        if (colon == 0) {
          throw BrokenMetaException.badKV(region, "an `info:server' cell"
                  + " doesn't contain `:' to separate the `host:port'"
                  + Bytes.pretty(hostport), kv);
        }
        host = getIP(new String(hostport, 0, colon));
        try {
          port = parsePortNumber(new String(hostport, colon + 1,
                  hostport.length - colon - 1));
        } catch (NumberFormatException e) {
          throw BrokenMetaException.badKV(region, "an `info:server' cell"
                  + " contains an invalid port: " + e.getMessage() + " in "
                  + Bytes.pretty(hostport), kv);
        }
      }
    }
    if (start_key == null) {
      throw new BrokenMetaException(null, "It didn't contain any"
              + " `info:regioninfo' cell:  " + meta_row);
    }
    
    return new RegionLocation(region, start_key, host, port);
  }
  
  public Deferred<List<RegionLocation>> locateRegions(final String table) {
    return locateRegions(table.getBytes());
  }
  
  public Deferred<List<RegionLocation>> locateRegions(final byte[] table) {
    class TypeCB implements Callback<Deferred<List<RegionLocation>>, Object> {
      @SuppressWarnings("unchecked")
      @Override
      public Deferred<List<RegionLocation>> call(final Object results) 
          throws Exception {
        if (results == null) {
          return Deferred.fromResult(null);
        }
        if (results instanceof Exception) {
          return Deferred.fromError((Exception) results);
        }
        return Deferred.fromResult((List<RegionLocation>)results);
      }
      @Override
      public String toString() {
        return "locateRegions type converter CB";
      }
    }
    return findTableRegions(table, EMPTY_ARRAY, EMPTY_ARRAY, false, true)
        .addCallbackDeferring(new TypeCB());
  }

  /** @return the rpc timeout timer */
  HashedWheelTimer getRpcTimeoutTimer() {
    return rpc_timeout_timer;
  }
  
  // --------------------------------------------------- //
  // Code that find regions (in our cache or using RPCs) //
  // --------------------------------------------------- //
  private Deferred<Object> locateRegion(final HBaseRpc request,
      final byte[] table, final byte[] key) {
    final boolean is_meta = Bytes.equals(table, META);
    final boolean is_root = !is_meta && Bytes.equals(table, ROOT);
    // We don't know in which region this row key is.  Let's look it up.
    // First, see if we already know where to look in .META.
    // Except, obviously, we don't wanna search in META for META or ROOT.
    final byte[] meta_key = is_root ? null : createRegionSearchKey(table, key);
    final byte[] meta_name;
    final RegionInfo meta_region;
    if (has_root) {
      meta_region = is_meta || is_root ? null : getRegion(META, meta_key);
      meta_name = META;
    } else {
      meta_region = META_REGION;
      meta_name = HBASE96_META;
    }

    if (meta_region != null) {  // Always true with HBase 0.95 and up.
      // Lookup in .META. which region server has the region we want.
      final RegionClient client = (has_root
                                   ? region2client.get(meta_region) // Pre 0.95
                                   : rootregion);                  // Post 0.95
      if (client != null && client.isAlive()) {
        final boolean has_permit = client.acquireMetaLookupPermit();
        if (!has_permit) {
          // If we failed to acquire a permit, it's worth checking if someone
          // looked up the region we're interested in.  Every once in a while
          // this will save us a META lookup.
          if (getRegion(table, key) != null) {
            return Deferred.fromResult(null);  // Looks like no lookup needed.
          }
        }
        Deferred<Object> d = null;
        try {
          d = client.getClosestRowBefore(meta_region, meta_name, meta_key, INFO)
            .addCallback(meta_lookup_done);
        } catch (RuntimeException e) {
          LOG.error("Unexpected exception while performing meta lookup", e);
          if (has_permit) {
            client.releaseMetaLookupPermit();
          }
          throw e;
        }
        if (has_permit) {
          final class ReleaseMetaLookupPermit implements Callback<Object, Object> {
            public Object call(final Object arg) {
              client.releaseMetaLookupPermit();
              return arg;
            }
            public String toString() {
              return "release .META. lookup permit";
            }
          };
          d.addBoth(new ReleaseMetaLookupPermit());
          meta_lookups_with_permit.increment();
        } else {
          meta_lookups_wo_permit.increment();
        }
        // This errback needs to run *after* the callback above.
        return d.addErrback(newLocateRegionErrback(request, table, key));
      }
    }

    // Make a local copy to avoid race conditions where we test the reference
    // to be non-null but then it becomes null before the next statement.
    final RegionClient rootregion = this.rootregion;
    if (rootregion == null || !rootregion.isAlive()) {
      return zkclient.getDeferredRoot();
    } else if (is_root) {  // Don't search ROOT in ROOT.
      return Deferred.fromResult(null);  // We already got ROOT (w00t).
    }
    // The rest of this function is only executed with HBase 0.94 and before.

    // Alright so we don't even know where to look in .META.
    // Let's lookup the right .META. entry in -ROOT-.
    final byte[] root_key = createRegionSearchKey(META, meta_key);
    final RegionInfo root_region = new RegionInfo(ROOT, ROOT_REGION,
                                                  EMPTY_ARRAY);
    root_lookups.increment();
    return rootregion.getClosestRowBefore(root_region, ROOT, root_key, INFO)
      .addCallback(root_lookup_done)
      // This errback needs to run *after* the callback above.
      .addErrback(newLocateRegionErrback(request, table, key));
  }

  /** Callback executed when a lookup in META completes.  */
  private final class MetaCB implements Callback<Object, ArrayList<KeyValue>> {
    public Object call(final ArrayList<KeyValue> arg) {
      return discoverRegion(arg);
    }
    public String toString() {
      return "locateRegion in META";
    }
  };
  private final MetaCB meta_lookup_done = new MetaCB();

  /** Callback executed when a lookup in -ROOT- completes.  */
  private final class RootCB implements Callback<Object, ArrayList<KeyValue>> {
    public Object call(final ArrayList<KeyValue> arg) {
      return discoverRegion(arg);
    }
    public String toString() {
      return "locateRegion in ROOT";
    }
  };
  private final RootCB root_lookup_done = new RootCB();

  private Callback<Object, Exception> newLocateRegionErrback(
      final HBaseRpc request, final byte[] table, final byte[] key) {
    return new Callback<Object, Exception>() {
      public Object call(final Exception e) {
        if (e instanceof TableNotFoundException) {
          return new TableNotFoundException(table);  // Populate the name.
        } else if (e instanceof RecoverableException) {
          // Retry to locate the region if we haven't tried too many times.  
          // TODO(tsuna): exponential backoff?
          if (cannotRetryRequest(request)) {
            return tooManyAttempts(request, null);
          }
          request.attempt++;
          return locateRegion(request, table, key);
        }
        return e;
      }
      public String toString() {
        return "locateRegion errback";
      }
    };
  }

  private static byte[] createRegionSearchKey(final byte[] table,
                                              final byte[] key) {
    // Rows in .META. look like this:
    //   tablename,startkey,timestamp
    final byte[] meta_key = new byte[table.length + key.length + 3];
    System.arraycopy(table, 0, meta_key, 0, table.length);
    meta_key[table.length] = ',';
    System.arraycopy(key, 0, meta_key, table.length + 1, key.length);
    meta_key[meta_key.length - 2] = ',';
    // ':' is the first byte greater than '9'.  We always want to find the
    // entry with the greatest timestamp, so by looking right before ':'
    // we'll find it.
    meta_key[meta_key.length - 1] = ':';
    return meta_key;
  }

  private RegionInfo getRegion(final byte[] table, final byte[] key) {
    if (has_root) {
      if (Bytes.equals(table, ROOT)) {               // HBase 0.94 and before.
        return new RegionInfo(ROOT, ROOT_REGION, EMPTY_ARRAY);
      }
    } else if (Bytes.equals(table, HBASE96_META)) {  // HBase 0.95 and up.
      return META_REGION;
    }

    byte[] region_name = createRegionSearchKey(table, key);
    Map.Entry<byte[], RegionInfo> entry = regions_cache.floorEntry(region_name);
    if (entry == null) {
      return null;
    }

    if (!isCacheKeyForTable(table, entry.getKey())) {
      return null;
    }

    region_name = null;
    final RegionInfo region = entry.getValue();
    entry = null;

    final byte[] stop_key = region.stopKey();
    if (stop_key != EMPTY_ARRAY
        // If the stop key is an empty byte array, it means this region is the
        // last region for this table and this key ought to be in that region.
        && Bytes.memcmp(key, stop_key) >= 0) {
      return null;
    }
    return region;
  }

  private static boolean isCacheKeyForTable(final byte[] table,
                                            final byte[] cache_key) {
    // Check we found an entry that's really for the requested table.
    for (int i = 0; i < table.length; i++) {
      if (table[i] != cache_key[i]) {  // This table isn't in the map, we found
        return false;                  // a key which is for another table.
      }
    }

    return cache_key[table.length] == ',';
  }

  private RegionClient discoverRegion(final ArrayList<KeyValue> meta_row) {
    if (meta_row.isEmpty()) {
      throw new TableNotFoundException();
    }
    String host = null;
    int port = -42;
    RegionInfo region = null;
    byte[] start_key = null;
    for (final KeyValue kv : meta_row) {
      final byte[] qualifier = kv.qualifier();
      if (Arrays.equals(REGIONINFO, qualifier)) {
        final byte[][] tmp = new byte[1][];  // Yes, this is ugly.
        region = RegionInfo.fromKeyValue(kv, tmp);
        if (knownToBeNSREd(region)) {
          invalidateRegionCache(region.name(), true, "has marked it as split.");
          return null;
        }
        start_key = tmp[0];
      } else if (Arrays.equals(SERVER, qualifier)
                 && kv.value() != EMPTY_ARRAY) {  // Empty during NSRE.
        final byte[] hostport = kv.value();
        int colon = hostport.length - 1;
        for (/**/; colon > 0 /* Can't be at the beginning */; colon--) {
          if (hostport[colon] == ':') {
            break;
          }
        }
        if (colon == 0) {
          throw BrokenMetaException.badKV(region, "an `info:server' cell"
            + " doesn't contain `:' to separate the `host:port'"
            + Bytes.pretty(hostport), kv);
        }
        host = getIP(new String(hostport, 0, colon));
        try {
          port = parsePortNumber(new String(hostport, colon + 1,
                                            hostport.length - colon - 1));
        } catch (NumberFormatException e) {
          throw BrokenMetaException.badKV(region, "an `info:server' cell"
            + " contains an invalid port: " + e.getMessage() + " in "
            + Bytes.pretty(hostport), kv);
        }
      }
    }
    if (start_key == null) {
      throw new BrokenMetaException("It didn't contain any"
        + " `info:regioninfo' cell:  " + meta_row);
    }

    final byte[] region_name = region.name();
    if (host == null) {
      invalidateRegionCache(region_name, true, "no longer has it assigned.");
      return null;
    }
    //使用Netty同服务端交互
    final RegionClient client = newClient(host, port);
    final RegionClient oldclient = region2client.put(region, client);
    if (client == oldclient) {  // We were racing with another thread to
      return client;            // discover this region, we lost the race.
    }
    RegionInfo oldregion;
    final ArrayList<RegionInfo> regions;
    int nregions;
    synchronized (client) {
      oldregion = regions_cache.put(region_name, region);
      regions = client2regions.get(client);
      if (regions != null) {
        synchronized (regions) {
          regions.add(region);
          nregions = regions.size();
        }
      } else {
        nregions = 0;
      }
    }
    if (nregions == 0 || regions != client2regions.get(client)) {
      return null;
    }

    // Don't interleave logging with the operations above, in order to attempt
    // to reduce the duration of the race windows.
    LOG.info((oldclient == null ? "Added" : "Replaced") + " client for"
             + " region " + region + ", which was "
             + (oldregion == null ? "added to" : "updated in") + " the"
             + " regions cache.  Now we know that " + client + " is hosting "
             + nregions + " region" + (nregions > 1 ? 's' : "") + '.');

    return client;
  }

  private void invalidateRegionCache(final byte[] region_name,
                                     final boolean mark_as_nsred,
                                     final String reason) {
    if ((region_name == META_REGION_NAME && !has_root)  // HBase 0.95+
        || region_name == ROOT_REGION) {                // HBase <= 0.94
      if (reason != null) {
        LOG.info("Invalidated cache for " + (has_root ? "-ROOT-" : ".META.")
                 + " as " + rootregion + ' ' + reason);
      }
      rootregion = null;
      return;
    }
    final RegionInfo oldregion = mark_as_nsred
      ? regions_cache.put(region_name, new RegionInfo(EMPTY_ARRAY, region_name,
                                                      EMPTY_ARRAY))
      : regions_cache.remove(region_name);
    final RegionInfo region = (oldregion != null ? oldregion
                               : new RegionInfo(EMPTY_ARRAY, region_name,
                                                EMPTY_ARRAY));
    final RegionClient client = region2client.remove(region);

    if (oldregion != null && !Bytes.equals(oldregion.name(), region_name)) {
      // XXX do we want to just re-add oldregion back?  This exposes another
      // race condition (we re-add it and overwrite yet another region change).
      LOG.warn("Oops, invalidated the wrong regions cache entry."
               + "  Meant to remove " + Bytes.pretty(region_name)
               + " but instead removed " + oldregion);
    }

    if (client == null) {
      return;
    }
    final ArrayList<RegionInfo> regions = client2regions.get(client);
    if (regions != null) {
      // `remove()' on an ArrayList causes an array copy.  Should we switch
      // to a LinkedList instead?
      synchronized (regions) {
        regions.remove(region);
      }
    }
    if (reason != null) {
      LOG.info("Invalidated cache for " + region + " as " + client
               + ' ' + reason);
    }
  }

  /**
   * Returns true if this region is known to be NSRE'd and shouldn't be used.
   * @see #handleNSRE
   */
  private static boolean knownToBeNSREd(final RegionInfo region) {
    return region.table() == EMPTY_ARRAY;
  }

  /** Log a message for every N RPCs we buffer due to an NSRE.  */
  private static final short NSRE_LOG_EVERY      =   500;

  void handleNSRE(HBaseRpc rpc,
                  final byte[] region_name,
                  final RecoverableException e) {
    num_nsre_rpcs.increment();
    if (rpc.isProbe()) {
      synchronized (rpc) {
        rpc.setSuspendedProbe(true);
      }
    }
    final boolean can_retry_rpc = !cannotRetryRequest(rpc);
    boolean known_nsre = true;  // We already aware of an NSRE for this region?
    ArrayList<HBaseRpc> nsred_rpcs = got_nsre.get(region_name);
    HBaseRpc exists_rpc = null;  // Our "probe" RPC.
    if (nsred_rpcs == null) {  // Looks like this could be a new NSRE...
      final ArrayList<HBaseRpc> newlist = new ArrayList<HBaseRpc>(64);
      // In HBase 0.95 and up, the exists RPC can't use the empty row key,
      // which could happen if we were trying to scan from the beginning of
      // the table.  So instead use "\0" as the key.
      exists_rpc = GetRequest.exists(rpc.table, probeKey(rpc.key));
      newlist.add(exists_rpc);
      if (can_retry_rpc) {
        newlist.add(rpc);
      }
      nsred_rpcs = got_nsre.putIfAbsent(region_name, newlist);
      if (nsred_rpcs == null) {  // We've just put `newlist'.
        nsred_rpcs = newlist;    //   => We're the first thread to get
        known_nsre = false;      //      the NSRE for this region.
      }
    }

    if (known_nsre) {  // Some RPCs seem to already be pending due to this NSRE
      boolean reject = true;  // Should we reject this RPC (too many pending)?
      int size;               // How many RPCs are already pending?

      synchronized (nsred_rpcs) {
        size = nsred_rpcs.size();
        if (size == 0) {
          final ArrayList<HBaseRpc> added =
            got_nsre.putIfAbsent(region_name, nsred_rpcs);
          if (added == null) {  // We've just put `nsred_rpcs'.
            exists_rpc = GetRequest.exists(rpc.table, probeKey(rpc.key));
            nsred_rpcs.add(exists_rpc);  // We hold the lock on nsred_rpcs
            if (can_retry_rpc) {
              nsred_rpcs.add(rpc);         // so we can safely add those 2.
            }
            known_nsre = false;  // We mistakenly believed it was known.
          } else {  // We lost the second race.
            if (can_retry_rpc) {
              synchronized (added) {  // Won't deadlock (explanation above).
                if (added.isEmpty()) {
                  LOG.error("WTF?  Shouldn't happen!  Lost 2 races and found"
                            + " an empty list of NSRE'd RPCs (" + added
                            + ") for " + Bytes.pretty(region_name));
                  exists_rpc = GetRequest.exists(rpc.table, probeKey(rpc.key));
                  added.add(exists_rpc);
                } else {
                  exists_rpc = added.get(0);
                }
                if (can_retry_rpc) {
                  added.add(rpc);  // Add ourselves in the existing array...
                }
              }
            }
            nsred_rpcs = added;  // ... and update our reference.
          }
        }
        // If `rpc' is the first element in nsred_rpcs, it's our "probe" RPC,
        // in which case we must not add it to the array again.
        else if ((exists_rpc = nsred_rpcs.get(0)) != rpc) {
          if (size < nsre_high_watermark) {
            if (size == nsre_low_watermark) {
              nsred_rpcs.add(null);  // "Skip" one slot.
            } else if (can_retry_rpc) {
              reject = false;
              if (nsred_rpcs.contains(rpc)) {  // XXX O(n) check...  :-/
                // This can happen when a probe gets an NSRE and regions_cache
                // is updated by another thread while it's retrying
                LOG.debug("Trying to add " + rpc + " twice to NSREd RPC"
                            + " on " + Bytes.pretty(region_name));
              } else {
                nsred_rpcs.add(rpc);
              }
            }
          }
        } else {           // This is our probe RPC.
          reject = false;  // So don't reject it.
        }
      } // end of the synchronized block.

      // Stop here if this is a known NSRE and `rpc' is not our probe RPC that
      // is not suspended
      synchronized (exists_rpc) {
        if (known_nsre && exists_rpc != rpc && !exists_rpc.isSuspendedProbe()) {
          if (size != nsre_high_watermark && size % NSRE_LOG_EVERY == 0) {
            final String msg = "There are now " + size
              + " RPCs pending due to NSRE on " + Bytes.pretty(region_name);
            if (size + NSRE_LOG_EVERY < nsre_high_watermark) {
              LOG.info(msg);  // First message logged at INFO level.
            } else {
              LOG.warn(msg);  // Last message logged with increased severity.
            }
          }
          if (reject) {
            rpc.callback(new PleaseThrottleException(size + " RPCs waiting on "
              + Bytes.pretty(region_name) + " to come back online", e, rpc,
              exists_rpc.getDeferred()));
          }
          return;  // This NSRE is already known and being handled.
        }
        exists_rpc.setSuspendedProbe(false);
      }
    }

    num_nsres.increment();
    // Mark this region as being NSRE'd in our regions_cache.
    invalidateRegionCache(region_name, true, (known_nsre ? "still " : "")
                          + "seems to be splitting or closing it.");

    // Need a `final' variable to access from within the inner class below.
    final ArrayList<HBaseRpc> rpcs = nsred_rpcs;  // Guaranteed non-null.
    final HBaseRpc probe = exists_rpc;  // Guaranteed non-null.
    nsred_rpcs = null;
    exists_rpc = null;

    if (known_nsre && probe.attempt > 1) {
      probe.attempt--;
    } else if (!can_retry_rpc) {
      // `rpc' isn't a probe RPC and can't be retried, make it fail-fast now.
      rpc.callback(tooManyAttempts(rpc, e));
    }

    rpc = null;  // No longer need this reference.

    final class RetryNSREd implements Callback<Object, Object> {
      public Object call(final Object arg) {
        if (arg instanceof Exception) {
          LOG.warn("Probe " + probe + " failed", (Exception) arg);
        }
        ArrayList<HBaseRpc> removed = got_nsre.remove(region_name);
        if (removed != rpcs && removed != null) {  // Should never happen.
          synchronized (removed) {                 // But just in case...
            synchronized (rpcs) {
              LOG.error("WTF?  Impossible!  Removed the wrong list of RPCs"
                + " from got_nsre.  Was expecting list@"
                + System.identityHashCode(rpcs) + " (size=" + rpcs.size()
                + "), got list@" + System.identityHashCode(removed)
                + " (size=" + removed.size() + ')');
            }
            for (final HBaseRpc r : removed) {
              if (r != null && r != probe) {
                sendRpcToRegion(r);  // We screwed up but let's not lose RPCs.
              }
            }
            removed.clear();
          }
        }
        removed = null;

        synchronized (rpcs) {
          if (LOG.isDebugEnabled()) {
            if (arg instanceof Exception) {
              LOG.debug("Retrying " + rpcs.size() + " RPCs on NSREd region "
                  + Bytes.pretty(region_name));
            } else {
              LOG.debug("Retrying " + rpcs.size() + " RPCs now that the NSRE on "
                  + Bytes.pretty(region_name) + " seems to have cleared");
            }
          }
          final ArrayList<HBaseRpc> rpcs_to_replay = new ArrayList<HBaseRpc>(rpcs);
          rpcs.clear();  // To avoid cyclic RPC chain

          final Iterator<HBaseRpc> i = rpcs_to_replay.iterator();
          if (i.hasNext()) {
            HBaseRpc r = i.next();
            if (r != probe) {
              LOG.error("WTF?  Impossible!  Expected first == probe but first="
                        + r + " and probe=" + probe);
              sendRpcToRegion(r);
            }
            while (i.hasNext()) {
              if ((r = i.next()) != null) {
                sendRpcToRegion(r);
              }
            }
          } else {
            // We avoided cyclic RPC chain
            LOG.debug("Empty rpcs array=" + rpcs_to_replay + " found by " + this);
          }
        }

        return arg;
      }
      public String toString() {
        return "retry other RPCs NSRE'd on " + Bytes.pretty(region_name);
      }
    };

    // It'll take a short while for HBase to clear the NSRE.  If a
    // region is being split, we should be able to use it again pretty
    // quickly, but if a META entry is stale (e.g. due to RegionServer
    // failures / restarts), it may take up to several seconds.
    final class NSRETimer implements TimerTask {
      public void run(final Timeout timeout) {
        if (probe.attempt == 0) {  // Brand new probe.
          probe.getDeferred().addBoth(new RetryNSREd());
        }
        if (LOG.isDebugEnabled()) {
          LOG.debug("Done waiting after NSRE on " + Bytes.pretty(region_name)
                    + ", retrying " + probe);
        }
        // Make sure the probe will cause a META lookup.
        invalidateRegionCache(region_name, false, null);
        sendRpcToRegion(probe);  // Restart the RPC.
      }
      public String toString() {
        return "probe NSRE " + probe;
      }
    };

    // Linear backoff followed by exponential backoff.  Some NSREs can be
    // resolved in a second or so, some seem to easily take ~6 seconds,
    // sometimes more when a RegionServer has failed and the master is slowly
    // splitting its logs and re-assigning its regions.
    final int wait_ms = probe.attempt < 4
      ? 200 * (probe.attempt + 2)     // 400, 600, 800, 1000
      : 1000 + (1 << probe.attempt);  // 1016, 1032, 1064, 1128, 1256, 1512, ..
    newTimeout(new NSRETimer(), wait_ms);
  }

  protected static byte[] PROBE_SUFFIX = {
    ':', 'A', 's', 'y', 'n', 'c', 'H', 'B', 'a', 's', 'e',
    '~', 'p', 'r', 'o', 'b', 'e', '~', '<', ';', '_', '<',
  };

  private static byte[] probeKey(final byte[] key) {
    final byte[] testKey = new byte[key.length + 64];
    System.arraycopy(key, 0, testKey, 0, key.length);
    System.arraycopy(PROBE_SUFFIX, 0,
                     testKey, testKey.length - PROBE_SUFFIX.length,
                     PROBE_SUFFIX.length);
    return testKey;
  }

  // ----------------------------------------------------------------- //
  // Code that manages connection and disconnection to Region Servers. //
  // ----------------------------------------------------------------- //
  private RegionClient newClient(final String host, final int port) {

    final String hostport = host + ':' + port;

    RegionClient client;
    SocketChannel chan = null;
    synchronized (ip2client) {
      client = ip2client.get(hostport);
      if (client != null && client.isAlive()) {
        return client;
      }
      final RegionClientPipeline pipeline = new RegionClientPipeline();
      client = pipeline.init();
      chan = channel_factory.newChannel(pipeline);
      ip2client.put(hostport, client);  // This is guaranteed to return null.
    }
    client2regions.put(client, new ArrayList<RegionInfo>());
    num_connections_created.increment();
    // Configure and connect the channel without locking ip2client.
    final SocketChannelConfig socket_config = chan.getConfig();
    socket_config.setConnectTimeoutMillis(
        config.getInt("hbase.ipc.client.socket.timeout.connect"));
    socket_config.setTcpNoDelay(
        config.getBoolean("hbase.ipc.client.tcpnodelay"));
    // Unfortunately there is no way to override the keep-alive timeout in
    // Java since the JRE doesn't expose any way to call setsockopt() with
    // TCP_KEEPIDLE.  And of course the default timeout is >2h.  Sigh.
    socket_config.setKeepAlive(
        config.getBoolean("hbase.ipc.client.tcpkeepalive"));
    
    // socket overrides using system defaults instead of setting them in a conf
    if (config.hasProperty("hbase.ipc.client.socket.write.high_watermark")) {
      ((NioChannelConfig)config).setWriteBufferHighWaterMark(
          config.getInt("hbase.ipc.client.socket.write.high_watermark"));
    }
    if (config.hasProperty("hbase.ipc.client.socket.write.low_watermark")) {
      ((NioChannelConfig)config).setWriteBufferLowWaterMark(
          config.getInt("hbase.ipc.client.socket.write.low_watermark"));
    }
    if (config.hasProperty("hbase.ipc.client.socket.sendBufferSize")) {
      socket_config.setOption("sendBufferSize", 
          config.getInt("hbase.ipc.client.socket.sendBufferSize"));
    }
    if (config.hasProperty("hbase.ipc.client.socket.receiveBufferSize")) {
      socket_config.setOption("receiveBufferSize", 
          config.getInt("hbase.ipc.client.socket.receiveBufferSize"));
    }
    //连接服务端
    chan.connect(new InetSocketAddress(host, port));  // Won't block.
    return client;
  }

  private final class RegionClientPipeline extends DefaultChannelPipeline {

    private boolean disconnected = false;

    /** A handler to close the connection if we haven't used it in some time */
    private final ChannelHandler timeout_handler;
    
    RegionClientPipeline() {
      timeout_handler = new IdleStateHandler(timer, 0, 0, 
          config.getInt("hbase.hbase.ipc.client.connection.idle_timeout"));
    }

    RegionClient init() {
      final RegionClient client = new RegionClient(HBaseClient.this);
      super.addLast("idle_handler", this.timeout_handler);
      super.addLast("idle_cleanup", new RegionClientIdleStateHandler());
      super.addLast("handler", client);
      return client;
    }

    @Override
    public void sendDownstream(final ChannelEvent event) {
      //LoggerFactory.getLogger(RegionClientPipeline.class)
      //  .debug("hooked sendDownstream " + event);
      if (event instanceof ChannelStateEvent) {
        handleDisconnect((ChannelStateEvent) event);
      }
      super.sendDownstream(event);
    }

    @Override
    public void sendUpstream(final ChannelEvent event) {
      //LoggerFactory.getLogger(RegionClientPipeline.class)
      //  .debug("hooked sendUpstream " + event);
      if (event instanceof ChannelStateEvent) {
        handleDisconnect((ChannelStateEvent) event);
      }
      super.sendUpstream(event);
    }

    private void handleDisconnect(final ChannelStateEvent state_event) {
      if (disconnected) {
        return;
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("Channel " + state_event.getChannel().toString() + 
            "'s state changed: " + state_event);
      }
      switch (state_event.getState()) {
        case OPEN:
          if (state_event.getValue() == Boolean.FALSE) {
            break;  // CLOSED
          }
          return;
        case CONNECTED:
          if (state_event.getValue() == null) {
            break;  // DISCONNECTED
          }
          return;
        default:
          return;  // Not an event we're interested in, ignore it.
      }

      LOG.info("Channel " + state_event.getChannel().toString() + 
          " is disconnecting: " + state_event);
      disconnected = true;  // So we don't clean up the same client twice.
      try {
        final RegionClient client = super.get(RegionClient.class);
        SocketAddress remote = super.getChannel().getRemoteAddress();
        if (remote == null) {
          remote = slowSearchClientIP(client);
        }

        // Prevent the client from buffering requests while we invalidate
        // everything we have about it.
        synchronized (client) {
          removeClientFromCache(client, remote);
        }
      } catch (Exception e) {
        LoggerFactory.getLogger(RegionClientPipeline.class)
          .error("Uncaught exception when handling a disconnection of "
                 + getChannel(), e);
      }
    }

  }

  /**
   * Handles the cases where sockets are either idle or leaked due to
   * connections closed from HBase or no data flowing downstream.
   */
  private final class RegionClientIdleStateHandler
          extends IdleStateAwareChannelHandler {

    /** Empty constructor */
    public RegionClientIdleStateHandler() {
    }

    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e)
            throws Exception {
      if(e.getState() == IdleState.ALL_IDLE) {
        idle_connections_closed.increment();
        LOG.info("Closing idle connection to HBase region server: " 
            + e.getChannel());
        e.getChannel().close();
      }
    }
  }
  
  private InetSocketAddress slowSearchClientIP(final RegionClient client) {
    String hostport = null;
    synchronized (ip2client) {
      for (final Map.Entry<String, RegionClient> e : ip2client.entrySet()) {
        if (e.getValue() == client) {
          hostport = e.getKey();
          break;
        }
      }
    }

    if (hostport == null) {
      HashMap<String, RegionClient> copy;
      synchronized (ip2client) {
        copy = new HashMap<String, RegionClient>(ip2client);
      }
      LOG.error("WTF?  Should never happen!  Couldn't find " + client
                + " in " + copy);
      return null;
    }

    LOG.warn("Couldn't connect to the RegionServer @ " + hostport);
    final int colon = hostport.indexOf(':', 1);
    if (colon < 1) {
      LOG.error("WTF?  Should never happen!  No `:' found in " + hostport);
      return null;
    }
    final String host = getIP(hostport.substring(0, colon));
    int port;
    try {
      port = parsePortNumber(hostport.substring(colon + 1,
                                                hostport.length()));
    } catch (NumberFormatException e) {
      LOG.error("WTF?  Should never happen!  Bad port in " + hostport, e);
      return null;
    }
    return new InetSocketAddress(host, port);
  }

  /**
   * Removes all the cache entries referred to the given client.
   * @param client The client for which we must invalidate everything.
   * @param remote The address of the remote peer, if known, or null.
   */
  private void removeClientFromCache(final RegionClient client,
                                     final SocketAddress remote) {
    if (client == rootregion) {
      LOG.info("Lost connection with the "
               + (has_root ? "-ROOT-" : ".META.") + " region");
      rootregion = null;
    }
    ArrayList<RegionInfo> regions = client2regions.remove(client);
    if (regions != null) {
      // Make a copy so we don't need to synchronize on it while iterating.
      RegionInfo[] regions_copy;
      synchronized (regions) {
        regions_copy = regions.toArray(new RegionInfo[regions.size()]);
        regions = null;
        // If any other thread still has a reference to `regions', their
        // updates will be lost (and we don't care).
      }
      for (final RegionInfo region : regions_copy) {
        final byte[] table = region.table();
        final byte[] stop_key = region.stopKey();
        final byte[] search_key =
          createRegionSearchKey(stop_key.length == 0
                                ? Arrays.copyOf(table, table.length + 1)
                                : table, stop_key);
        final Map.Entry<byte[], RegionInfo> entry =
          regions_cache.lowerEntry(search_key);
        if (entry != null && entry.getValue() == region) {
          // Invalidate the regions cache first, as it's the most damaging
          // one if it contains stale data.
          regions_cache.remove(entry.getKey());
          LOG.debug("Removed from regions cache: {}", region);
        }
        final RegionClient oldclient = region2client.remove(region);
        if (client == oldclient) {
          LOG.debug("Association removed: {} -> {}", region, client);
        } else if (oldclient != null) {  // Didn't remove what we expected?!
          LOG.warn("When handling disconnection of " + client
                   + " and removing " + region + " from region2client"
                   + ", it was found that " + oldclient + " was in fact"
                   + " serving this region");
        }
      }
    }

    if (remote == null) {
      return;  // Can't continue without knowing the remote address.
    }

    String hostport = null;
    if (remote instanceof InetSocketAddress) {
      final InetSocketAddress sock = (InetSocketAddress) remote;
      final InetAddress addr = sock.getAddress();
      if (addr == null) {
        LOG.error("WTF?  Unresolved IP for " + remote
                  + ".  This shouldn't happen.");
        return;
      } else {
        hostport = addr.getHostAddress() + ':' + sock.getPort();
      }
    } else {
        LOG.error("WTF?  Found a non-InetSocketAddress remote: " + remote
                  + ".  This shouldn't happen.");
        return;
    }

    RegionClient old;
    synchronized (ip2client) {
      old = ip2client.remove(hostport);
    }
    LOG.debug("Removed from IP cache: {} -> {}", hostport, client);
    if (old == null) {
      LOG.warn("When expiring " + client + " from the client cache (host:port="
               + hostport + "), it was found that there was no entry"
               + " corresponding to " + remote + ".  This shouldn't happen.");
    }
  }

  // ---------------- //
  // ZooKeeper stuff. //
  // ---------------- //
  protected final class ZKClient implements Watcher {

    /** The specification of the quorum, e.g. "host1,host2,host3"  */
    private final String quorum_spec;

    /** The base path under which is the znode for the -ROOT- region.  */
    private final String base_path;

    /**
     * Our ZooKeeper instance.
     * Must grab this' monitor before accessing.
     */
    private ZooKeeper zk;

    /**
     * When we're not connected to ZK, users who are trying to access the
     * -ROOT- region can queue up here to be called back when it's available.
     * Must grab this' monitor before accessing.
     */
    private ArrayList<Deferred<Object>> deferred_rootregion;

    /**
     * Constructor.
     * @param quorum_spec The specification of the quorum, e.g.
     * {@code "host1,host2,host3"}.
     * @param base_path The base path under which is the znode for the
     * -ROOT- region.
     */
    public ZKClient(final String quorum_spec, final String base_path) {
      this.quorum_spec = quorum_spec;
      this.base_path = base_path;
    }

    /**
     * Returns a deferred that will be called back once we found -ROOT-.
     * @return A deferred which will be invoked with an unspecified argument
     * once we know where -ROOT- is.  Note that by the time you get called
     * back, we may have lost the connection to the -ROOT- region again.
     */
    public Deferred<Object> getDeferredRoot() {
      final Deferred<Object> d = new Deferred<Object>();
      synchronized (this) {
        try {
          connectZK();  // Kick off a connection if needed.
          if (deferred_rootregion == null) {
            LOG.info("Need to find the "
                     + (has_root ? "-ROOT-" : ".META.") + " region");
            deferred_rootregion = new ArrayList<Deferred<Object>>();
          }
          deferred_rootregion.add(d);
        } catch (NonRecoverableException e) {
          LOG.error(e.getMessage(), e.getCause());
          d.callback(e);
        }
      }
      return d;
    }

    Deferred<Object> getDeferredRootIfBeingLookedUp() {
      synchronized (this) {
        if (deferred_rootregion == null) {
          return null;
        }
        final Deferred<Object> d = new Deferred<Object>();
        deferred_rootregion.add(d);
        return d;
      }
    }

    private ArrayList<Deferred<Object>> atomicGetAndRemoveWaiters() {
      synchronized (this) {
        try {
          return deferred_rootregion;
        } finally {
          deferred_rootregion = null;
        }
      }
    }

    public void process(final WatchedEvent event) {
      LOG.debug("Got ZooKeeper event: {}", event);
      try {
        switch (event.getState()) {
          case SyncConnected:
            getRootRegion();
            break;
          default:
            disconnectZK();
            // Reconnect only if we're still trying to locate -ROOT-.
            synchronized (this) {
              if (deferred_rootregion != null) {
                LOG.warn("No longer connected to ZooKeeper, event=" + event);
                connectZK();
              }
            }
            return;
        }
      } catch (Exception e) {
        LOG.error("Uncaught exception when handling event " + event, e);
        return;
      }
      LOG.debug("Done handling ZooKeeper event: {}", event);
    }

    private void connectZK() {
      try {
        // Session establishment is asynchronous, so this won't block.
        synchronized (this) {
          if (zk != null) {  // Already connected.
            return;
          }
          zk = new ZooKeeper(quorum_spec, 
              config.getInt("hbase.zookeeper.session.timeout"), this);
        }
      } catch (UnknownHostException e) {
        // No need to retry, we usually cannot recover from this.
        throw new NonRecoverableException("Cannot connect to ZooKeeper,"
          + " is the quorum specification valid? " + quorum_spec, e);
      } catch (IOException e) {
        LOG.error("Failed to connect to ZooKeeper", e);
        // XXX don't retry recursively, create a timer with an exponential
        // backoff and schedule the reconnection attempt for later.
        connectZK();
      }
    }

    public void disconnectZK() {
      synchronized (this) {
        if (zk == null) {
          return;
        }
        try {
          LOG.debug("Ignore any DEBUG exception from ZooKeeper");
          final long start = System.nanoTime();
          zk.close();
          LOG.debug("ZooKeeper#close completed in {}ns",
                    System.nanoTime() - start);
        } catch (InterruptedException e) {
          LOG.error("Should never happen", e);
        }
        zk = null;
      }
    }

    /** Schedule a timer to retry {@link #getRootRegion} after some time.  */
    private void retryGetRootRegionLater() {
      newTimeout(new TimerTask() {
          public void run(final Timeout timeout) {
            if (!getRootRegion()) {  // Try to read the znodes
              connectZK();  // unless we need to connect first.
            }
          }
        }, config.getInt("hbase.zookeeper.getroot.retry_delay") 
          /* milliseconds */);
    }

    /**
     * Puts a watch in ZooKeeper to monitor the file of the -ROOT- region.
     * This method just registers an asynchronous callback.
     */
    final class ZKCallback implements AsyncCallback.DataCallback {

      /**
       * HBASE-3065 (r1151751) prepends meta-data in ZooKeeper files.
       * The meta-data always starts with this magic byte.
       */
      protected static final byte MAGIC = (byte) 0xFF;

      private static final byte UNKNOWN = 0;  // Callback still pending.
      private static final byte FOUND = 1;    // We found the znode.
      private static final byte NOTFOUND = 2; // The znode didn't exist.

      private byte found_root;
      private byte found_meta;  // HBase 0.95 and up

      public void processResult(final int rc, final String path,
                                final Object ctx, final byte[] data,
                                final Stat stat) {
        final boolean is_root;  // True if ROOT znode, false if META znode.
        if (path.endsWith("/root-region-server")) {
          is_root = true;
        } else if (path.endsWith("/meta-region-server")) {
          is_root = false;
        } else {
          LOG.error("WTF? We got a callback from ZooKeeper for a znode we did"
                    + " not expect: " + path + " / stat: " + stat + " / data: "
                    + Bytes.pretty(data));
          retryGetRootRegionLater();
          return;
        }

        if (rc == Code.NONODE.intValue()) {
          final boolean both_znode_failed;
          if (is_root) {
            found_root = NOTFOUND;
            both_znode_failed = found_meta == NOTFOUND;
          } else {  // META (HBase 0.95 and up)
            found_meta = NOTFOUND;
            both_znode_failed = found_root == NOTFOUND;
          }
          if (both_znode_failed) {
            LOG.error("The znode for the -ROOT- region doesn't exist!");
            retryGetRootRegionLater();
          }
          return;
        } else if (rc != Code.OK.intValue()) {
          LOG.error("Looks like our ZK session expired or is broken, rc="
                    + rc + ": " + Code.get(rc));
          disconnectZK();
          connectZK();
          return;
        }
        if (data == null || data.length == 0 || data.length > Short.MAX_VALUE) {
          LOG.error("The location of the -ROOT- region in ZooKeeper is "
                    + (data == null || data.length == 0 ? "empty"
                       : "too large (" + data.length + " bytes!)"));
          retryGetRootRegionLater();
          return;  // TODO(tsuna): Add a watch to wait until the file changes.
        }

        final RegionClient client;
        if (is_root) {
          found_root = FOUND;
          client = handleRootZnode(data);
        } else {  // META (HBase 0.95 and up)
          found_meta = FOUND;
          client = handleMetaZnode(data);
        }

        if (client == null) {         // We failed to get a client.
          retryGetRootRegionLater();  // So retry later.
          return;
        }

        final ArrayList<Deferred<Object>> ds = atomicGetAndRemoveWaiters();
        if (ds != null) {
          for (final Deferred<Object> d : ds) {
            d.callback(client);
          }
        }

        disconnectZK();

        synchronized (ZKClient.this) {
          if (deferred_rootregion != null) {
            connectZK();
          }
        }
      }

      protected RegionClient handleRootZnode(final byte[] data) {
        boolean newstyle;     // True if we expect a 0.91 style location.
        final short offset;   // Bytes to skip at the beginning of data.
        short firstsep = -1;  // Index of the first separator (':' or ',').
        if (data[0] == MAGIC) {
          newstyle = true;
          final int metadata_length = Bytes.getInt(data, 1);
          if (metadata_length < 1 || metadata_length > 65000) {
            LOG.error("Malformed meta-data in " + Bytes.pretty(data)
                      + ", invalid metadata length=" + metadata_length);
            return null;  // TODO(tsuna): Add a watch to wait until the file changes.
          }
          offset = (short) (1 + 4 + metadata_length);
        } else {
          newstyle = false;  // Maybe true, the loop below will tell us.
          offset = 0;
        }
        final short n = (short) data.length;
        // Look for the first separator.  Skip the offset, and skip the
        // first byte, because we know the separate can only come after
        // at least one byte.
        loop: for (short i = (short) (offset + 1); i < n; i++) {
           switch (data[i]) {
            case ',':
              newstyle = true;
              /* fall through */
            case ':':
              firstsep = i;
              break loop;
          }
        }
        if (firstsep == -1) {
          LOG.error("-ROOT- location doesn't contain a separator"
                    + " (':' or ','): " + Bytes.pretty(data));
          return null;  // TODO(tsuna): Add a watch to wait until the file changes.
        }
        final String host;
        final short portend;  // Index past where the port number ends.
        if (newstyle) {
          host = new String(data, offset, firstsep - offset);
          short i;
          for (i = (short) (firstsep + 2); i < n; i++) {
            if (data[i] == ',') {
              break;
            }
          }
          portend = i;  // Port ends on the comma.
        } else {
          host = new String(data, 0, firstsep);
          portend = n;  // Port ends at the end of the array.
        }
        final int port = parsePortNumber(new String(data, firstsep + 1,
                                                    portend - firstsep - 1));
        final String ip = getIP(host);
        if (ip == null) {
          LOG.error("Couldn't resolve the IP of the -ROOT- region from "
                    + host + " in \"" + Bytes.pretty(data) + '"');
          return null;  // TODO(tsuna): Add a watch to wait until the file changes.
        }
        LOG.info("Connecting to -ROOT- region @ " + ip + ':' + port);
        has_root = true;
        final RegionClient client = rootregion = newClient(ip, port);
        return client;
      }

      /**
       * Returns a new client for the RS found in the meta-region-server.
       * This is used in HBase 0.95 and up.
       */
      protected RegionClient handleMetaZnode(final byte[] data) {
        if (data[0] != MAGIC) {
          LOG.error("Malformed META region meta-data in " + Bytes.pretty(data)
                    + ", invalid leading magic number: " + data[0]);
          return null;
        }

        final int metadata_length = Bytes.getInt(data, 1);
        if (metadata_length < 1 || metadata_length > 65000) {
          LOG.error("Malformed META region meta-data in " + Bytes.pretty(data)
                    + ", invalid metadata length=" + metadata_length);
          return null;  // TODO(tsuna): Add a watch to wait until the file changes.
        }
        short offset = (short) (1 + 4 + metadata_length);

        final int pbuf_magic = Bytes.getInt(data, offset);
        if (pbuf_magic != PBUF_MAGIC) {
          LOG.error("Malformed META region meta-data in " + Bytes.pretty(data)
                    + ", invalid magic number=" + pbuf_magic);
          return null;  // TODO(tsuna): Add a watch to wait until the file changes.
        }
        offset += 4;

        final String ip;
        final int port;
        try {
          final ZooKeeperPB.MetaRegionServer meta =
            ZooKeeperPB.MetaRegionServer.newBuilder()
            .mergeFrom(data, offset, data.length - offset).build();
          ip = getIP(meta.getServer().getHostName());
          port = meta.getServer().getPort();
        } catch (InvalidProtocolBufferException e) {
          LOG.error("Failed to parse the protobuf in " + Bytes.pretty(data), e);
          return null;  // TODO(tsuna): Add a watch to wait until the file changes.
        }

        LOG.info("Connecting to .META. region @ " + ip + ':' + port);
        has_root = false;
        final RegionClient client = rootregion = newClient(ip, port);
        return client;
      }

    }

    private boolean getRootRegion() {
      synchronized (this) {
        if (zk != null) {
          LOG.debug("Finding the -ROOT- or .META. region in ZooKeeper");
          final ZKCallback cb = new ZKCallback();
          zk.getData(base_path + "/root-region-server", this, cb, null);
          zk.getData(base_path + "/meta-region-server", this, cb, null);
          return true;
        }
      }
      return false;
    }

  }

  // --------------- //
  // Little helpers. //
  // --------------- //
  private static String getIP(final String host) {
    final long start = System.nanoTime();
    try {
      final String ip = InetAddress.getByName(host).getHostAddress();
      final long latency = System.nanoTime() - start;
      if (latency > 500000/*ns*/ && LOG.isDebugEnabled()) {
        LOG.debug("Resolved IP of `" + host + "' to "
                  + ip + " in " + latency + "ns");
      } else if (latency >= 3000000/*ns*/) {
        LOG.warn("Slow DNS lookup!  Resolved IP of `" + host + "' to "
                 + ip + " in " + latency + "ns");
      }
      return ip;
    } catch (UnknownHostException e) {
      LOG.error("Failed to resolve the IP of `" + host + "' in "
                + (System.nanoTime() - start) + "ns");
      return null;
    }
  }

  private static int parsePortNumber(final String portnum)
    throws NumberFormatException {
    final int port = Integer.parseInt(portnum);
    if (port <= 0 || port > 65535) {
      throw new NumberFormatException(port == 0 ? "port is zero" :
                                      (port < 0 ? "port is negative: "
                                       : "port is too large: ") + port);
    }
    return port;
  }

}
