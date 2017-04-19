package com.stumbleupon.async;

public final class CallbackOverflowError extends StackOverflowError {

  public CallbackOverflowError(final String msg) {
    super(msg);
  }

  private static final long serialVersionUID = 1350030042;
}
