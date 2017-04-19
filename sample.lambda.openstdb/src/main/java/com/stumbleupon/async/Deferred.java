package com.stumbleupon.async;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Deferred<T> {

  private static final Logger LOG = LoggerFactory.getLogger(Deferred.class);

  private static final short MAX_CALLBACK_CHAIN_LENGTH = (1 << 14) - 1;

  private static final byte INIT_CALLBACK_CHAIN_SIZE = 4;

  private static final byte PENDING = 0;
  private static final byte RUNNING = 1;
  private static final byte PAUSED = 2;
  private static final byte DONE = 3;

  private volatile int state;

  private Object result;

  private Callback[] callbacks;

  private short next_callback;

  private short last_callback;

  /** Helper for atomic CAS on the state.  */
  private static final AtomicIntegerFieldUpdater<Deferred> stateUpdater =
    AtomicIntegerFieldUpdater.newUpdater(Deferred.class, "state");

  private boolean casState(final int cmp, final int val) {
    return stateUpdater.compareAndSet(this, cmp, val);
  }

  /** Constructor.  */
  public Deferred() {
    state = PENDING;
  }

  /** Private constructor used when the result is already available.  */
  private Deferred(final Object result) {
    this.result = result;
    state = DONE;
  }

  public static <T> Deferred<T> fromResult(final T result) {
    return new Deferred<T>(result);
  }


  public static <T> Deferred<T> fromError(final Exception error) {
    return new Deferred<T>(error);
  }

  public <R, R2, E> Deferred<R> addCallbacks(final Callback<R, T> cb,
                                             final Callback<R2, E> eb) {
    if (cb == null) {
      throw new NullPointerException("null callback");
    } else if (eb == null) {
      throw new NullPointerException("null errback");
    }
    // We need to synchronize on `this' first before the CAS, to prevent
    // runCallbacks from switching our state from RUNNING to DONE right
    // before we add another callback.
    synchronized (this) {
      // If we're DONE, switch to RUNNING atomically.
      if (state == DONE) {
        // This "check-then-act" sequence is safe as this is the only code
        // path that transitions from DONE to RUNNING and it's synchronized.
        state = RUNNING;
      } else {
        // We get here if weren't DONE (most common code path)
        //  -or-
        // if we were DONE and another thread raced with us to change the
        // state and we lost the race (uncommon).
        if (callbacks == null) {
          callbacks = new Callback[INIT_CALLBACK_CHAIN_SIZE];
        }
        // Do we need to grow the array?
        else if (last_callback == callbacks.length) {
          final int oldlen = callbacks.length;
          if (oldlen == MAX_CALLBACK_CHAIN_LENGTH * 2) {
            throw new CallbackOverflowError("Too many callbacks in " + this
              + " (size=" + (oldlen / 2) + ") when attempting to add cb="
              + cb + '@' + cb.hashCode() + ", eb=" + eb + '@' + eb.hashCode());
          }
          final int len = Math.min(oldlen * 2, MAX_CALLBACK_CHAIN_LENGTH * 2);
          final Callback[] newcbs = new Callback[len];
          System.arraycopy(callbacks, next_callback,  // Outstanding callbacks.
                           newcbs, 0,            // Move them to the beginning.
                           last_callback - next_callback);  // Number of items.
          last_callback -= next_callback;
          next_callback = 0;
          callbacks = newcbs;
        }
        callbacks[last_callback++] = cb;
        callbacks[last_callback++] = eb;
        return (Deferred<R>) ((Deferred) this);
      }
    }  // end of synchronized block

    if (!doCall(result instanceof Exception ? eb : cb)) {

      boolean more;
      synchronized (this) {
        more = callbacks != null && next_callback != last_callback;
      }
      if (more) {
        runCallbacks();  // Will put us back either in DONE or in PAUSED.
      } else {
        state = DONE;
      }
    }
    return (Deferred<R>) ((Object) this);
  }

   public <R> Deferred<R> addCallback(final Callback<R, T> cb) {
    return addCallbacks(cb, Callback.PASSTHROUGH);
  }


  public <R, D extends Deferred<R>>
    Deferred<R> addCallbackDeferring(final Callback<D, T> cb) {
    return addCallbacks((Callback<R, T>) ((Object) cb), Callback.PASSTHROUGH);
  }

  public <R, E> Deferred<T> addErrback(final Callback<R, E> eb) {
    return addCallbacks((Callback<T, T>) ((Object) Callback.PASSTHROUGH), eb);
  }

  public <R> Deferred<R> addBoth(final Callback<R, T> cb) {
    return addCallbacks(cb, cb);
  }

  public <R, D extends Deferred<R>>
    Deferred<R> addBothDeferring(final Callback<D, T> cb) {
    return addCallbacks((Callback<R, T>) ((Object) cb),
                        (Callback<R, T>) ((Object) cb));
  }

  public Deferred<T> chain(final Deferred<T> other) {
    if (this == other) {
      throw new AssertionError("A Deferred cannot be chained to itself."
                               + "  this=" + this);
    }
    final Chain<T> cb = new Chain<T>(other);
    return addCallbacks(cb, cb);
  }


  private static final class Chain<T> implements Callback<T, T> {
    private final Deferred<T> other;


    public Chain(final Deferred<T> other) {
      this.other = other;
    }

    public T call(final T arg) {
      other.callback(arg);
      return arg;
    }

    public String toString() {
      return "chain with Deferred@" + other.hashCode();
    }
  };

  public static <T>
    Deferred<ArrayList<T>> group(final Collection<Deferred<T>> deferreds) {
    return new DeferredGroup<T>(deferreds, false).getDeferred();
  }

   public static <T>
    Deferred<ArrayList<T>> groupInOrder(final Collection<Deferred<T>> deferreds) {
    return new DeferredGroup<T>(deferreds, true).getDeferred();
  }

  public static <T>
    Deferred<ArrayList<T>> group(final Deferred<T> d1, final Deferred<T> d2) {
    final ArrayList<Deferred<T>> tmp = new ArrayList<Deferred<T>>(2);
    tmp.add(d1);
    tmp.add(d2);
    return new DeferredGroup<T>(tmp, false).getDeferred();
  }

  public static <T>
    Deferred<ArrayList<T>> group(final Deferred<T> d1,
                                 final Deferred<T> d2,
                                 final Deferred<T> d3) {
    final ArrayList<Deferred<T>> tmp = new ArrayList<Deferred<T>>(3);
    tmp.add(d1);
    tmp.add(d2);
    tmp.add(d3);
    return new DeferredGroup<T>(tmp, false).getDeferred();
  }

  public void callback(final Object initresult) {
    if (!casState(PENDING, RUNNING)) {
      throw new AssertionError("This Deferred was already called!"
        + "  New result=" + initresult + ", this=" + this);
    }
    result = initresult;
    if (initresult instanceof Deferred) {
      final Deferred d = (Deferred) initresult;
      if (this == d) {
        throw new AssertionError("A Deferred cannot be given to itself"
                                 + " as a result.  this=" + this);
      }
      handleContinuation(d, null);
    }
    runCallbacks();
  }

  public T join() throws InterruptedException, Exception {
    return doJoin(true, 0);
  }

  public T join(final long timeout) throws InterruptedException, Exception {
    return doJoin(true, timeout);
  }

  public T joinUninterruptibly() throws Exception {
    try {
      return doJoin(false, 0);
    } catch (InterruptedException e) {
      throw new AssertionError("Impossible");
    }
  }
  public T joinUninterruptibly(final long timeout) throws Exception {
    try {
      return doJoin(false, timeout);
    } catch (InterruptedException e) {
      throw new AssertionError("Impossible");
    }
  }

  //线程执行
  private T doJoin(final boolean interruptible, final long timeout)
  throws InterruptedException, Exception {
    if (state == DONE) {  // Nothing to join, we're already DONE.
      if (result instanceof Exception) {
        throw (Exception) result;
      }
      return (T) result;
    }

    final Signal signal_cb = new Signal();

    // Dealing with InterruptedException properly is a PITA.  I highly
    // recommend reading http://goo.gl/aeOOXT to understand how this works.
    boolean interrupted = false;
    try {
      while (true) {
        try {
          boolean timedout = false;
          synchronized (signal_cb) {
            addBoth((Callback<T, T>) ((Object) signal_cb));
            if (timeout == 0) {  // No timeout, we can use a simple loop.
              // If we get called back immediately, we won't enter the loop.
              while (signal_cb.result == signal_cb) {
                signal_cb.wait();
              }
            } else if (timeout < 0) {
              throw new IllegalArgumentException("negative timeout: " + timeout);
            } else {  // We have a timeout, the loop is a bit more complicated.
              long timeleft = timeout * 1000000L;  // Convert to nanoseconds.
              if (timeout > 31556926000L) {  // One year in milliseconds.
                // Most likely a programming bug.
                LOG.warn("Timeout (" + timeout + ") is long than 1 year."
                         + "  this=" + this);
                if (timeleft <= 0) {  // Very unlikely.
                  throw new IllegalArgumentException("timeout overflow after"
                    + " conversion to nanoseconds: " + timeout);
                }
              }
              // If we get called back immediately, we won't enter the loop.
              while (signal_cb.result == signal_cb) {
                long duration = System.nanoTime();
                final long millis = timeleft / 1000000L;
                final int nanos = (int) (timeleft % 1000000);
                // This API is annoying because it won't let us specify just
                // nanoseconds.  The second argument must be less than 1000000.
                signal_cb.wait(millis, nanos);
                duration = System.nanoTime() - duration;
                timeleft -= duration;
                if (timeleft < 100) {
                  timedout = true;
                  break;
                }
              }
            }
          }
          if (timedout && signal_cb.result == signal_cb) {
            // Give up if we timed out *and* we haven't gotten a result yet.
            throw new TimeoutException(this, timeout);
          } else if (signal_cb.result instanceof Exception) {
            throw (Exception) signal_cb.result;
          }
          return (T) signal_cb.result;
        } catch (InterruptedException e) {
          LOG.debug("While joining {}: interrupted", this);
          interrupted = true;
          if (interruptible) {
            throw e;  // Let the exception propagate out.
          }
        }
      }
    } finally {
      if (interrupted) {
        Thread.currentThread().interrupt();  // Restore the interrupted status.
      }
    }
  }

  static final class Signal implements Callback<Object, Object> {
    Object result = this;

    private final String thread = Thread.currentThread().getName();

    public Object call(final Object arg) {
      synchronized (this) {
        result = arg;
        super.notify();  // Guaranteed to have only 1 thread wait()ing.
      }
      return arg;
    }

    public String toString() {
      return "wakeup thread " + thread;
    }
  };

  /**
   * Executes all the callbacks in the current chain.
   */
  private void runCallbacks() {
    while (true) {
      Callback cb = null;
      Callback eb = null;
      synchronized (this) {
        if (callbacks != null && next_callback != last_callback) {
          cb = callbacks[next_callback++];
          eb = callbacks[next_callback++];
        }
        else {
          state = DONE;
          callbacks = null;
          next_callback = last_callback = 0;
          break;
        }
      }
      if (doCall(result instanceof Exception ? eb : cb)) {
        break;
      }
    }
  }

  private boolean doCall(final Callback cb) {
    try {
      //LOG.debug("doCall(" + cb + '@' + cb.hashCode() + ')' + super.hashCode());
      result = cb.call(result);
    } catch (Exception e) {
      result = e;
    }

    if (result instanceof Deferred) {
      handleContinuation((Deferred) result, cb);
      return true;
    }
    return false;
  }

  private void handleContinuation(final Deferred d, final Callback cb) {
    if (this == d) {
      final String cb2s = cb == null ? "null" : cb + "@" + cb.hashCode();
      throw new AssertionError("After " + this + " executed callback=" + cb2s
        + ", the result returned was the same Deferred object.  This is illegal"
        + ", a Deferred can't run itself recursively.  Something is wrong.");
    }
    if (d.casState(DONE, RUNNING)) {
      result = d.result;  // No one will change `d.result' now.
      d.state = DONE;
      runCallbacks();
      return;
    }

    state = PAUSED;
    d.addBoth(new Continue(d, cb));
    if (LOG.isDebugEnabled() && state == PAUSED) {
      if (cb != null) {
        LOG.debug("callback=" + cb + '@' + cb.hashCode() + " returned " + d
                  + ", so the following Deferred is getting paused: " + this);
      } else {
        LOG.debug("The following Deferred is getting paused: " + this
                  + " as it received another Deferred as a result: " + d);
      }
    }
  }

  /**
   * A {@link Callback} to resume execution after another Deferred.
   */
  private final class Continue implements Callback<Object, Object> {
    private final Deferred d;
    private final Callback cb;

    /**
     * Constructor.
     * @param d The other Deferred we need to resume after.
     * @param cb The callback that returned that Deferred or {@code null} if we
     * don't know where this Deferred comes from (it was our initial result).
     */
    public Continue(final Deferred d, final Callback cb) {
      this.d = d;
      this.cb = cb;
    }

    public Object call(final Object arg) {
      if (arg instanceof Deferred) {
        handleContinuation((Deferred) arg, cb);
      } else if (!casState(PAUSED, RUNNING)) {
        final String cb2s = cb == null ? "null" : cb + "@" + cb.hashCode();
        throw new AssertionError("Tried to resume the execution of "
          + Deferred.this + ") although it's not in state=PAUSED."
          + "  This occurred after the completion of " + d
          + " which was originally returned by callback=" + cb2s);
      }
      result = arg;
      runCallbacks();
      return arg;
    }

    public String toString() {
      return "(continuation of Deferred@" + Deferred.super.hashCode()
        + " after " + (cb != null ? cb + "@" + cb.hashCode() : d) + ')';
    }
  };

  public String toString() {
    final int state = this.state;  // volatile access before reading result.
    final Object result = this.result;
    final String str;
    if (result == null) {
      str = "null";
    } else if (result instanceof Deferred) {  // Nested Deferreds
      str = "Deferred@" + result.hashCode();  // are hard to read.
    } else {
      str = result.toString();
    }

    final StringBuilder buf = new StringBuilder((9 + 10 + 7 + 7
                                                 + str.length()) * 2);
    buf.append("Deferred@").append(super.hashCode())
      .append("(state=").append(stateString(state))
      .append(", result=").append(str)
      .append(", callback=");
    synchronized (this) {
      if (callbacks == null || next_callback == last_callback) {
        buf.append("<none>, errback=<none>");
      } else {
        for (int i = next_callback; i < last_callback; i += 2) {
          buf.append(callbacks[i]).append(" -> ");
        }
        buf.setLength(buf.length() - 4);  // Remove the extra " -> ".
        buf.append(", errback=");
        for (int i = next_callback + 1; i < last_callback; i += 2) {
          buf.append(callbacks[i]).append(" -> ");
        }
        buf.setLength(buf.length() - 4);  // Remove the extra " -> ".
      }
    }
    buf.append(')');
    return buf.toString();
  }

  private static String stateString(final int state) {
    switch (state) {
      case PENDING: return "PENDING";
      case RUNNING: return "RUNNING";
      case PAUSED:  return "PAUSED";
      case DONE:    return "DONE";
    }
    throw new AssertionError("Should never be here.  WTF: state=" + state);
  }

}
