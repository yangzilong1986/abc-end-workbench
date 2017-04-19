package com.stumbleupon.async;

public interface Callback<R, T> {

  /**
   * The callback.
   * @param arg The argument to the callback.
   * @return The return value of the callback.
   * @throws Exception any exception.
   */
  public R call(T arg) throws Exception;

  /** The identity function (returns its argument).  */
  public static final Callback<Object, Object> PASSTHROUGH =
    new Callback<Object, Object>() {
      public Object call(final Object arg) {
        return arg;
      }
      public String toString() {
        return "passthrough";
      }
    };

}
