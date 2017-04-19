package org.hbase.async;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.util.CharsetUtil;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stumbleupon.async.Deferred;

public abstract class HBaseRpc {
  private static final Logger LOG = LoggerFactory.getLogger(HBaseRpc.class);
  
  /**
   * An RPC from which you can get a table name.
   * @since 1.1
   */
  public interface HasTable {
    public byte[] table();
  }

  public interface HasKey {
    public byte[] key();
  }

  public interface HasFamily {
    public byte[] family();
  }

  public interface HasQualifier {
    public byte[] qualifier();
  }

  public interface HasQualifiers {
    public byte[][] qualifiers();
  }

  public interface HasValue {
    public byte[] value();
  }

  public interface HasValues {
    public byte[][] values();
  }

  public interface HasTimestamp {
    public long timestamp();
  }

  interface IsEdit {
    /** RPC method name to use with HBase 0.95+.  */
    static final byte[] MUTATE = { 'M', 'u', 't', 'a', 't', 'e' };
  }
  private boolean trace_rpc;

  public boolean isTraceRPC() {
    return trace_rpc;
  }

  public void setTraceRPC(boolean trace_rpc) {
    this.trace_rpc = trace_rpc;
  }
  

  // ------ //
  // Flags. //
  // ------ //
  // 5th byte into the response.
  // See ipc/ResponseFlag.java in HBase's source code.

  static final byte RPC_SUCCESS = 0x00;
  static final byte RPC_ERROR = 0x01;


  static final byte RPC_FRAMED = 0x02;

  // ----------- //
  // RPC Status. //
  // ----------- //
  // 4 byte integer (on wire), located 9 byte into the response, only if
  // {@link RPC_FRAMED} is set.
  // See ipc/Status.java in HBase's source code.
  static final byte RPC_FATAL = -1;


  abstract ChannelBuffer serialize(byte server_version);

  abstract Object deserialize(ChannelBuffer buf, int cell_size);

  /**
   * Throws an exception if the argument is non-zero.
   */
  static void ensureNoCell(final int cell_size) {
    if (cell_size != 0) {
      throw new InvalidResponseException(
        "Should not have gotten any cell blocks, yet there are "
        + cell_size + " bytes that follow the protobuf response."
        + "  This should never happen."
        + "  Are you using an incompatible version of HBase?", null);
    }
  }

  private Deferred<Object> deferred;

  final byte[] table;  // package-private for subclasses, not other classes.

  final byte[] key;  // package-private for subclasses, not other classes.

  RegionInfo region;  // package-private for subclasses, not other classes.

  byte attempt;  // package-private for RegionClient and HBaseClient only.

  private int timeout = -1;
  
  /** If the RPC has a timeout set this will be set on submission to the 
   * timer thread. */
  Timeout timeout_handle; // package-private for RegionClient and HBaseClient only.
  
  private TimerTask timeout_task;
  
  /** Whether or not this RPC has timed out already */
  private boolean has_timedout;
  
  /**
   * If true, this RPC should fail-fast as soon as we know we have a problem.
   */
  boolean failfast = false;

  /** The ID of this RPC as set by the last region client that handled it */
  int rpc_id;
  
  /** A reference to the last region client that handled this RPC */
  private RegionClient region_client;
  

  public final boolean setFailfast(final boolean failfast) {
    return this.failfast = failfast;
  }

  public final boolean failfast() {
    return failfast;
  }


  boolean probe = false;

  public boolean isProbe() {
    return probe;
  }

  public void setProbe(boolean probe) {
    this.probe = probe;
  }

  /**
   * Whether or not if this RPC is a probe that is suspended by an NSRE
   */
  private boolean suspended_probe = false;

  boolean isSuspendedProbe() {
    return suspended_probe;
  }

  void setSuspendedProbe(boolean suspended_probe) {
    this.suspended_probe = suspended_probe;
  }

  /**
   * Package private constructor for RPCs that aren't for any region.
   */
  HBaseRpc() {
    table = null;
    key = null;
  }

  HBaseRpc(final byte[] table, final byte[] key) {
    KeyValue.checkTable(table);
    KeyValue.checkKey(key);
    this.table = table;
    this.key = key;
  }

  public void setTimeout(final int timeout) {
    if (timeout < 0) {
      throw new IllegalArgumentException("The timeout cannot be negative");
    }
    this.timeout = timeout;
  }
  
  public int getTimeout() {
    return timeout;
  }
  
  // ---------------------- //
  // Package private stuff. //
  // ---------------------- //

  /**
   * Package private way of getting the name of the RPC method.
   * @param server_version What RPC protocol version the server is running.
   */
  abstract byte[] method(byte server_version);

  final void setRegion(final RegionInfo region) {
    if (table == null) {
      throw new AssertionError("Can't use setRegion if no table was given.");
    }
    this.region = region;
  }

  /**
   * Returns the region this RPC is supposed to go to (can be {@code null}).
   */
  final RegionInfo getRegion() {
    return region;
  }

  /** Package private way of accessing / creating the Deferred of this RPC.  */
  final Deferred<Object> getDeferred() {
    if (deferred == null) {
      deferred = new Deferred<Object>();
    }
    return deferred;
  }

  private final class TimeoutTask implements TimerTask {
    @Override
    public void run(final Timeout time_out) throws Exception {
      synchronized (HBaseRpc.this) {
        if (has_timedout) {
          throw new IllegalStateException(
              "This RPC has already timed out " + HBaseRpc.this);
        }
        has_timedout = true;
      }
      
      if (timeout_handle == null) {
        LOG.error("Received a timeout handle " + time_out 
            + " but this RPC did not have one " + this);
      }
      if (time_out != timeout_handle) {
        LOG.error("Receieved a timeout handle " + time_out + 
            " that doesn't match our own " + this);
      }
      if (region_client == null) {
        LOG.error("Somehow the region client was null when timing out RPC " 
            + this);
      } else {
        region_client.removeRpc(HBaseRpc.this, true);
      }
      
      callback(new RpcTimedOutException("RPC ID [" + rpc_id + 
          "] timed out waiting for response from HBase on region client [" + 
          region_client + " ] for over " + timeout + "ms"));
      timeout_task = null;
      timeout_handle = null;
    }
  }
  
  void enqueueTimeout(final RegionClient region_client) {
    // TODO - it's possible that we may actually retry a timed out RPC in which
    // case we want to allow this.
    if (has_timedout) {
      throw new IllegalStateException("This RPC has already timed out " + this);
    }
    if (timeout == -1) {
      timeout = region_client.getHBaseClient().getDefaultRpcTimeout();
    }
    if (timeout > 0) {
      this.region_client = region_client;
      if (timeout_task == null) {
        // we can re-use the task if this RPC is sent to another region server
        timeout_task = new TimeoutTask();
      }
      try {
        if (timeout_handle != null) {
          LOG.warn("RPC " + this + " had a previous timeout task");
        }
        timeout_handle = region_client.getHBaseClient().getRpcTimeoutTimer()
            .newTimeout(timeout_task, timeout, TimeUnit.MILLISECONDS);
      } catch (IllegalStateException e) {
        // This can happen if the timer fires just before shutdown()
        // is called from another thread, and due to how threads get
        // scheduled we tried to schedule a timeout after timer.stop().
        // Region clients will handle the RPCs on shutdown so we don't need 
        // to here.
        LOG.warn("Failed to schedule RPC timeout: " + this
                 + "  Ignore this if we're shutting down.", e);
        timeout_handle = null;
      }
    }
  }
  
  /** @return Whether or not this particular RPC has timed out and should not
   * be retried */
  final synchronized boolean hasTimedOut() {
    return has_timedout;
  }
  
  final void callback(final Object result) {
    if (timeout_handle != null) {
      timeout_handle.cancel();
      timeout_task = null;
      timeout_handle = null;
    }
    
    final Deferred<Object> d = deferred;
    if (d == null) {
      return;
    }
    deferred = null;
    attempt = 0;
    d.callback(result);
  }

  /** Checks whether or not this RPC has a Deferred without creating one.  */
  final boolean hasDeferred() {
    return deferred != null;
  }

  public String toString() {
    // Try to rightsize the buffer.
    final String method = new String(this.method((byte) 0));
    final StringBuilder buf = new StringBuilder(16 + method.length() + 2
      + 8 + (table == null ? 4 : table.length + 2)  // Assumption: ASCII => +2
      + 6 + (key == null ? 4 : key.length * 2)      // Assumption: binary => *2
      + 9 + (region == null ? 4 : region.stringSizeHint())
      + 10 + 1 + 1);
    buf.append("HBaseRpc(method=");
    buf.append(method);
    buf.append(", table=");
    Bytes.pretty(buf, table);
    buf.append(", key=");
    Bytes.pretty(buf, key);
    buf.append(", region=");
    if (region == null) {
      buf.append("null");
    } else {
      region.toStringbuf(buf);
    }
    buf.append(", attempt=").append(attempt)
       .append(", timeout=").append(timeout)
       .append(", hasTimedout=").append(has_timedout);
    buf.append(')');
    return buf.toString();
  }

  final String toStringWithQualifiers(final String classname,
                                      final byte[] family,
                                      final byte[][] qualifiers) {
    return toStringWithQualifiers(classname, family, qualifiers, null, "");
  }


  final String toStringWithQualifiers(final String classname,
                                      final byte[] family,
                                      final byte[][] qualifiers,
                                      final byte[][] values,
                                      final String fields) {
    final StringBuilder buf = new StringBuilder(256  // min=182
                                                + fields.length());
    buf.append(classname).append("(table=");
    Bytes.pretty(buf, table);
    buf.append(", key=");
    Bytes.pretty(buf, key);
    buf.append(", family=");
    Bytes.pretty(buf, family);
    buf.append(", qualifiers=");
    Bytes.pretty(buf, qualifiers);
    if (values != null) {
      buf.append(", values=");
      Bytes.pretty(buf, values);
    }
    buf.append(fields);
    buf.append(", attempt=").append(attempt)
      .append(", region=");
    if (region == null) {
      buf.append("null");
    } else {
      region.toStringbuf(buf);
    }
    buf.append(')');
    return buf.toString();
  }

  final String toStringWithQualifier(final String classname,
                                     final byte[] family,
                                     final byte[] qualifier,
                                     final String fields) {
    final StringBuilder buf = new StringBuilder(256  // min=181
                                                + fields.length());
    buf.append(classname).append("(table=");
    Bytes.pretty(buf, table);
    buf.append(", key=");
    Bytes.pretty(buf, key);
    buf.append(", family=");
    Bytes.pretty(buf, family);
    buf.append(", qualifier=");
    Bytes.pretty(buf, qualifier);
    buf.append(fields);
    buf.append(", attempt=").append(attempt)
      .append(", region=");
    if (region == null) {
      buf.append("null");
    } else {
      region.toStringbuf(buf);
    }
    buf.append(')');
    return buf.toString();
  }

  // --------------------- //
  // RPC utility functions //
  // --------------------- //
  final ChannelBuffer newBuffer(final byte server_version,
                                final int max_payload_size) {
    // Add extra bytes for the RPC header:
    //   4 bytes: Payload size (always present, even in HBase 0.95+).
    //   4 bytes: RPC ID.
    //   2 bytes: Length of the method name.
    //   N bytes: The method name.
    final int header = 4 + 4 + 2 + method(server_version).length
      // Add extra bytes for the RPC header used in HBase 0.92 and above:
      //   1 byte:  RPC header version.
      //   8 bytes: Client version.  Yeah, 8 bytes, WTF seriously.
      //   4 bytes: Method fingerprint.
      + (server_version < RegionClient.SERVER_VERSION_092_OR_ABOVE ? 0
         : 1 + 8 + 4);
    // Note: with HBase 0.95 and up, the size of the protobuf header varies.
    // It is currently made of (see RequestHeader in RPC.proto):
    //   - uint32 callId: varint 1 to 5 bytes
    //   - RPCTInfo traceInfo: two uint64 varint so 4 to 20 bytes.
    //   - string methodName: varint length (1 byte) and method name.
    //   - bool requestParam: 1 byte
    //   - CellBlockMeta cellBlockMeta: one uint32 varint so 2 to 6 bytes.
    // Additionally each field costs an extra 1 byte, and there is a varint
    // prior to the header for the size of the header.  We don't set traceInfo
    // right now so that leaves us with 4 fields for a total maximum size of
    // 1 varint + 4 fields + 5 + 1 + N + 1 + 6 = 18 bytes max + method name.
    // Since for HBase 0.92 we reserve 19 bytes, we're good, we over-allocate
    // at most 1 bytes.  So the logic above doesn't need to change for 0.95+.
    final ChannelBuffer buf = ChannelBuffers.buffer(header + max_payload_size);
    buf.setIndex(0, header);  // Advance the writerIndex past the header.
    return buf;
  }

  static final ChannelBuffer toChannelBuffer(final byte[] method,
                                             final AbstractMessageLite pb) {
    final int pblen = pb.getSerializedSize();
    final int vlen = CodedOutputStream.computeRawVarint32Size(pblen);
    final byte[] buf = new byte[4 + 19 + method.length + vlen + pblen];
    try {
      final CodedOutputStream out = CodedOutputStream.newInstance(buf, 4 + 19 + method.length,
                                                                  vlen + pblen);
      out.writeRawVarint32(pblen);
      pb.writeTo(out);
      out.checkNoSpaceLeft();
    } catch (IOException e) {
      throw new RuntimeException("Should never happen", e);
    }
    return ChannelBuffers.wrappedBuffer(buf);
  }

  static void writeHBaseBool(final ChannelBuffer buf, final boolean b) {
    buf.writeByte(1);  // Code for Boolean.class in HbaseObjectWritable
    buf.writeByte(b ? 0x01 : 0x00);
  }

  static void writeHBaseInt(final ChannelBuffer buf, final int v) {
    buf.writeByte(5);  // Code for Integer.class in HbaseObjectWritable
    buf.writeInt(v);
  }

  static void writeHBaseLong(final ChannelBuffer buf, final long v) {
    buf.writeByte(6);  // Code for Long.class in HbaseObjectWritable
    buf.writeLong(v);
  }

  static void writeHBaseString(final ChannelBuffer buf, final String s) {
    buf.writeByte(10);  // Code for String.class in HbaseObjectWritable
    final byte[] b = s.getBytes(CharsetUtil.UTF_8);
    writeVLong(buf, b.length);
    buf.writeBytes(b);
  }

  static void writeHBaseByteArray(final ChannelBuffer buf, final byte[] b) {
    buf.writeByte(11);     // Code for byte[].class in HbaseObjectWritable
    writeByteArray(buf, b);
  }

  static void writeByteArray(final ChannelBuffer buf, final byte[] b) {
    writeVLong(buf, b.length);
    buf.writeBytes(b);
  }

  /**
   * Serializes a `null' reference.
   * @param buf The buffer to write to.
   */
  static void writeHBaseNull(final ChannelBuffer buf) {
    buf.writeByte(14);  // Code type for `Writable'.
    buf.writeByte(17);  // Code type for `NullInstance'.
    buf.writeByte(14);  // Code type for `Writable'.
  }

  /**
   * Upper bound on the size of a byte array we de-serialize.
   * This is to prevent HBase from OOM'ing us, should there be a bug or
   * undetected corruption of an RPC on the network, which would turn a
   * an innocuous RPC into something allocating a ton of memory.
   * The Hadoop RPC protocol doesn't do any checksumming as they probably
   * assumed that TCP checksums would be sufficient (they're not).
   */
  static final long MAX_BYTE_ARRAY_MASK =
    0xFFFFFFFFF0000000L;  // => max = 256MB == 268435455

  static void checkArrayLength(final ChannelBuffer buf, final long length) {
    // 2 checks in 1.  If any of the high bits are set, we know the value is
    // either too large, or is negative (if the most-significant bit is set).
    if ((length & MAX_BYTE_ARRAY_MASK) != 0) {
      if (length < 0) {
        throw new IllegalArgumentException("Read negative byte array length: "
          + length + " in buf=" + buf + '=' + Bytes.pretty(buf));
      } else {
        throw new IllegalArgumentException("Read byte array length that's too"
          + " large: " + length + " > " + ~MAX_BYTE_ARRAY_MASK + " in buf="
          + buf + '=' + Bytes.pretty(buf));
      }
    }
  }

  static void checkArrayLength(final byte[] array) {
    if ((array.length & MAX_BYTE_ARRAY_MASK) != 0) {//
      if (array.length < 0) {  // Not possible unless there's a JVM bug.
        throw new AssertionError("Negative byte array length: "
                                 + array.length + ' ' + Bytes.pretty(array));
      } else {
        throw new IllegalArgumentException("Byte array length too big: "
          + array.length + " > " + ~MAX_BYTE_ARRAY_MASK);
        // Don't dump the gigantic byte array in the exception message.
      }
    }
  }

  static void checkNonEmptyArrayLength(final ChannelBuffer buf,
                                       final long length) {
    if (length == 0) {
      throw new IllegalArgumentException("Read zero-length byte array "
        + " in buf=" + buf + '=' + Bytes.pretty(buf));
    }
    checkArrayLength(buf, length);
  }

  static byte[] readByteArray(final ChannelBuffer buf) {
    final long length = readVLong(buf);
    checkArrayLength(buf, length);
    final byte[] b = new byte[(int) length];
    buf.readBytes(b);
    return b;
  }

  static String readHadoopString(final ChannelBuffer buf) {
    final int length = buf.readInt();
    checkArrayLength(buf, length);
    final byte[] s = new byte[length];
    buf.readBytes(s);
    return new String(s, CharsetUtil.UTF_8);
  }

  static <T> T readProtobuf(final ChannelBuffer buf, final Parser<T> parser) {
    final int length = HBaseRpc.readProtoBufVarint(buf);
    HBaseRpc.checkArrayLength(buf, length);
    final byte[] payload;
    final int offset;
    if (buf.hasArray()) {  // Zero copy.
      payload = buf.array();
      offset = buf.arrayOffset() + buf.readerIndex();
      buf.readerIndex(buf.readerIndex() + length);
    } else {  // We have to copy the entire payload out of the buffer :(
      payload = new byte[length];
      buf.readBytes(payload);
      offset = 0;
    }
    try {
      return parser.parseFrom(payload, offset, length);
    } catch (InvalidProtocolBufferException e) {
      final String msg = "Invalid RPC response: length=" + length
        + ", payload=" + Bytes.pretty(payload);
      LOG.error("Invalid RPC from buffer: " + buf);
      throw new InvalidResponseException(msg, e);
    }
  }

  // -------------------------------------- //
  // Variable-length integer value encoding //
  // -------------------------------------- //
  /*
   * Unofficial documentation of the Hadoop VLong encoding
   * *****************************************************
   *
   * The notation used to refer to binary numbers here is `0b' followed by
   * the bits, as is printed by Python's built-in `bin' function for example.
   *
   * Values between
   *   -112 0b10010000
   * and
   *    127 0b01111111
   * (inclusive) are encoded on a single byte using their normal
   * representation.  The boundary "-112" sounds weird at first (and it is)
   * but it'll become clearer once you understand the format.
   *
   * Values outside of the boundaries above are encoded by first having
   * 1 byte of meta-data followed by a variable number of bytes that make up
   * the value being encoded.
   *
   * The "meta-data byte" always starts with the prefix 0b1000.  Its format
   * is as follows:
   *   1 0 0 0 | S | L L L
   * The bit `S' is the sign bit (1 = positive value, 0 = negative, yes
   * that's weird, I would've done it the other way around).
   * The 3 bits labeled `L' indicate how many bytes make up this variable
   * length value.  They're encoded like so:
   *   1 1 1 = 1 byte follows
   *   1 1 0 = 2 bytes follow
   *   1 0 1 = 3 bytes follow
   *   1 0 0 = 4 bytes follow
   *   0 1 1 = 5 bytes follow
   *   0 1 0 = 6 bytes follow
   *   0 0 1 = 7 bytes follow
   *   0 0 0 = 8 bytes follow
   * Yes, this is weird too, it goes backwards, requires more operations to
   * convert the length into something human readable, and makes sorting the
   * numbers unnecessarily complicated.
   * Notice that the prefix wastes 3 bits.  Also, there's no "VInt", all
   * variable length encoded values are eventually transformed to `long'.
   *
   * The remaining bytes are just the original number, as-is, without the
   * unnecessary leading bytes (that are all zeroes).
   *
   * Examples:
   *   42 is encoded as                   00101010 (as-is, 1 byte)
   *  127 is encoded as                   01111111 (as-is, 1 bytes)
   *  128 is encoded as          10001111 10000000 (2 bytes)
   *  255 is encoded as          10001111 11111111 (2 bytes)
   *  256 is encoded as 10001110 00000001 00000000 (3 bytes)
   *   -1 is encoded as                   11111111 (as-is, 1 byte)
   *  -42 is encoded as                   11010110 (as-is, 1 byte)
   * -112 is encoded as                   10010000 (as-is, 1 byte)
   * -113 is encoded as          10000111 01110000 (2 bytes)
   * -256 is encoded as          10000111 11111111 (2 bytes)
   * -257 is encoded as 10000110 00000001 00000000 (3 bytes)
   *
   * The implementations of writeVLong and readVLong below are on average
   * 14% faster than Hadoop's implementation given a uniformly distributed
   * input (lots of values of all sizes), and can be up to 40% faster on
   * certain input sizes (e.g. big values that fit on 8 bytes).  This is due
   * to two main things: fewer arithmetic and logic operations, and processing
   * multiple bytes together when possible.
   * Reading is about 6% faster than writing (negligible difference).
   * My MacBook Pro with a 2.66 GHz Intel Core 2 Duo easily does 5000 calls to
   * readVLong or writeVLong per millisecond.
   *
   * However, since we use Netty, we don't have to deal with the stupid Java
   * I/O library, so unlike Hadoop we don't use DataOutputStream and
   * ByteArrayOutputStream, instead we use ChannelBuffer.  This gives us a
   * significant extra performance boost over Hadoop.  The 14%-60% difference
   * above becomes a 70% to 80% difference!  Yes, that's >4 times faster!  With
   * the code below my MacBook Pro with a 2.66 GHz Intel Core 2 Duo easily
   * does 11000 writeVLong/ms or 13500 readVLong/ms (notice that reading is
   * 18% faster) when using a properly sized dynamicBuffer.  When using a
   * fixed-size buffer, writing (14200/s) is almost as fast as reading
   * (14500/s).
   *
   * So there's really no reason on Earth to use java.io.  Its API is horrible
   * and so is its performance.
   */
  static void writeVLong(final ChannelBuffer buf, long n) {
    // All those values can be encoded on 1 byte.
    if (n >= -112 && n <= 127) {//127:0b1111111,-112:11111111111111111111111110010000
      buf.writeByte((byte) n);
      return;
    }

    // Set the high bit to indicate that more bytes are to come.
    //设置高位，以指示更多字节来,0x为16进制
    // Both 0x90 and 0x88 have the high bit set (and are thus negative).
    //Java中负数编码，即取绝对值原码，反码，而后取补码
    //Long为64位
    byte b = (byte) 0x90; // 0b1001 0000 -112
    if (n < 0) {
      n = ~n;//取反,即符合位不变，其他位取反
      b = (byte) 0x88;    // 0b1000 1000 -120
    }

    {
      long tmp = n;
      do {
        tmp >>>= 8;//除法，八位位移，八位为一个字节
        // The first time we decrement `b' here, it's going to move the
        // rightmost `1' in `b' to the right, due to the way 2's complement
        // representation works.
        // So if `n' is positive, and we started with
        // b = 0b1001 0000, now we'll have b = 0b1000 1111, which correctly
        // indicates that `n' is positive (5th bit set) and has 1 byte so far
        // (last 3 bits are set).
        // If `n' is negative, and we started with
        // b = 0b1000 1000, now we'll have b = 0b1000 0111, which correctly
        // indicates that `n' is negative (5th bit not set) and has 1 byte.
        // Each time we keep decrementing this value, the last remaining 3
        // bits are going to change according to the format described above.
        //1000 11 1110 1000 >>>8  0011
        //900  11 1000 0100
        //1000 11111 01000
        //900  11100 00100
        //9    1001
        //127  1111111
        //int型为32个字节

        //-1    11111111111111111111111111111111
        //-127  1111 1111 1111 1111 1111 1111 1000 0001

        //0x90; // 0b                         1001 0000 -112
        //-112  1111 1111 1111 1111 1111 1111 1001 0000

        //-113  1111 1111 1111 1111 1111 1111 1000 1111 15
        //-114  1111 1111 1111 1111 1111 1111 1000 1110 14
        //-115  1111 1111 1111 1111 1111 1111 1000 1101 13
        //-116  1111 1111 1111 1111 1111 1111 1000 1100 12
        //-117  1111 1111 1111 1111 1111 1111 1000 1011 11
        //-118  1111 1111 1111 1111 1111 1111 1000 1010 10
        //-119  1111 1111 1111 1111 1111 1111 1000 1001 9

        //-120  1111 1111 1111 1111 1111 1111 1000 1000 8
        //负数, //0x88; //                  0b1000 1000 -120
        //-121  1111 1111 1111 1111 1111 1111 1000 0111 7
        //-122  1111 1111 1111 1111 1111 1111 1000 0110 6
        //-123  1111 1111 1111 1111 1111 1111 1000 0101 5
        //-124  1111 1111 1111 1111 1111 1111 1000 0100 4
        //-125  1111 1111 1111 1111 1111 1111 1000 0011 3
        //-126  1111 1111 1111 1111 1111 1111 1000 0010 2
        //-127  1111 1111 1111 1111 1111 1111 1000 0001 1

        //-128  1111 1111 1111 1111 1111 1111 1000 0000 0
        /////////////////////////////////////////////////
        //-9    1111 1111 1111 1111 1111 1111 1111 0111
        //-900  1111 1111 1111 1111 1111 1100 0111 1100
        //512   10 0000 0000
        //87       0101 0111
        //88       0101 1000
        //90       1011010
        b--;
      } while (tmp != 0);
    }
    //1000 1001
    // 8999999999999999999/1000 1000
    // 899999999999999/1000 1001
    // 8999999999999/1000 1010
    //AbstractChannelBuffer
    //BigEndianHeapChannelBuffer
    //四位标志位
    buf.writeByte(b);
    switch (b & 0x07) {  // Look at the low 3 bits (the length). 获取后三位，0000 0111
      case 0x00:
        buf.writeLong(n);
        break;
      case 0x01:
        buf.writeInt((int) (n >>> 24));
        buf.writeMedium((int) n);
        break;
      case 0x02:
        buf.writeMedium((int) (n >>> 24));
        buf.writeMedium((int) n);
        break;
      case 0x03:
        buf.writeByte((byte) (n >>> 32));
      case 0x04:
        buf.writeInt((int) n);
        break;
      case 0x05:
        buf.writeMedium((int) n);
        break;
      case 0x06:
        buf.writeShort((short) n);
        break;
      case 0x07:
        buf.writeByte((byte) n);
    }
  }

  static long readVLong(final ChannelBuffer buf) {
    byte b = buf.readByte();
    // Unless the first half of the first byte starts with 0xb1000, we're
    // dealing with a single-byte value.
    if ((b & 0xF0) != 0x80) {  // 0xF0 = 0b11110000, 0x80 = 0b10000000
      return b;
    }

    // The value is negative if the 5th bit is 0.
    final boolean negate = (b & 0x08) == 0;    // 0x08 = 0b00001000
    long result = 0;//读取时左移位
    switch (b & 0x07) {  // Look at the low 3 bits (the length).
      case 0x00:
        result = buf.readLong();
        break;
      case 0x01:
        result = buf.readUnsignedInt();
        result <<= 32;
        result |= buf.readUnsignedMedium();
        break;
      case 0x02:
        result = buf.readUnsignedMedium();
        result <<= 24;
        result |= buf.readUnsignedMedium();
        break;
      case 0x03:
        b = buf.readByte();
        result <<= 8;
        result |= b & 0xFF;
      case 0x04:
        result <<= 32;
        result |= buf.readUnsignedInt();
        break;
      case 0x05:
        result |= buf.readUnsignedMedium();
        break;
      case 0x06:
        result |= buf.readUnsignedShort();
        break;
      case 0x07:
        b = buf.readByte();
        result <<= 8;
        result |= b & 0xFF;
    }
    return negate ? ~result : result;
  }

  static int readProtoBufVarint(final ChannelBuffer buf) {
    int result = buf.readByte();
    if (result >= 0) {
      return result;
    }
    result &= 0x7F;
    result |= buf.readByte() << 7;
    if (result >= 0) {
      return result;
    }
    result &= 0x3FFF;
    result |= buf.readByte() << 14;
    if (result >= 0) {
      return result;
    }
    result &= 0x1FFFFF;
    result |= buf.readByte() << 21;
    if (result >= 0) {
      return result;
    }
    result &= 0x0FFFFFFF;
    final byte b = buf.readByte();
    result |= b << 28;
    if (b >= 0) {
      return result;
    }
    throw new IllegalArgumentException("Not a 32 bit varint: " + result
                                       + " (5th byte: " + b + ")");
  }

}
