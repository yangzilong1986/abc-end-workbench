package com.abc.framework.core.exception;

public class SecurityException extends RuntimeException {

	public SecurityException() {
	}

	public SecurityException(String message) {
		super(message);
	}

	public SecurityException(Throwable cause) {
		super(cause);
	}

	public SecurityException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public SecurityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
