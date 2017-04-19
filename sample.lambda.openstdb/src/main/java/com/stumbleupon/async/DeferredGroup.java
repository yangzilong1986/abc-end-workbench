package com.stumbleupon.async;

import java.util.Collection;
import java.util.ArrayList;

final class DeferredGroup<T> {

  private final Deferred<ArrayList<T>> parent = new Deferred<ArrayList<T>>();

  private int nresults;

  private final ArrayList<Object> results;

  public DeferredGroup(final Collection<Deferred<T>> deferreds,
                       final boolean ordered) {
    nresults = deferreds.size();
    results = new ArrayList<Object>(nresults);

    if (nresults == 0) {
      parent.callback(results);
      return;
    }

    // Callback used to collect results in the order in which they appear.
    final class Notify<T> implements Callback<T, T> {
      public T call(final T arg) {
        recordCompletion(arg);
        return arg;
      }
      public String toString() {
        return "notify DeferredGroup@" + DeferredGroup.super.hashCode();
      }
    };

    // Callback that preserves the original orders of the Deferreds.
    final class NotifyOrdered<T> implements Callback<T, T> {
      private final int index;
      NotifyOrdered(int index) {
        this.index = index;
      }
      public T call(final T arg) {
        recordCompletion(arg, index);
        return arg;
      }
      public String toString() {
        return "notify #" + index + " DeferredGroup@"
          + DeferredGroup.super.hashCode();
      }
    };

    if (ordered) {
      int i = 0;
      for (final Deferred<T> d : deferreds) {
        results.add(null);  // ensures results.set(i, result) is valid.
        // Note: it's important to add the callback after the line above,
        // as the callback can fire at any time once it's been added, and
        // if it fires before results.set(i, result) is valid, we'll get
        // an IndexOutOfBoundsException.
        d.addBoth(new NotifyOrdered<T>(i++));
      }
    } else {
      final Notify<T> notify = new Notify<T>();
      for (final Deferred<T> d : deferreds) {
        d.addBoth(notify);
      }
    }
  }

  /**
   * Returns the parent {@link Deferred} of the group.
   */
  public Deferred<ArrayList<T>> getDeferred() {
    return parent;
  }

  /**
   * Called back when one of the {@link Deferred} in the group completes.
   * @param result The result of the deferred.
   */
  private void recordCompletion(final Object result) {
    int left;
    synchronized (this) {
      results.add(result);
      left = --nresults;
    }
    if (left == 0) {
      done();
    }
  }

  /**
   * Called back when one of the {@link Deferred} in the group completes.
   * @param result The result of the deferred.
   * @param index The index of the result.
   */
  private void recordCompletion(final Object result, final int index) {
    int left;
    synchronized (this) {
      results.set(index, result);
      left = --nresults;
    }
    if (left == 0) {
      done();
    }
  }

  /** Called once we have obtained all the results of this group.  */
  private void done() {
    // From this point on, we no longer need to synchronize in order to
    // access `results' since we know we're done, so no other thread is
    // going to call recordCompletion() again.
    for (final Object r : results) {
      if (r instanceof Exception) {
        parent.callback(new DeferredGroupException(results, (Exception) r));
        return;
      }
    }
    parent.callback(results);
  }

  public String toString() {
    return "DeferredGroup"
      + "(parent=" + parent
      + ", # results=" + results.size() + " / " + nresults + " left)";
  }

}
