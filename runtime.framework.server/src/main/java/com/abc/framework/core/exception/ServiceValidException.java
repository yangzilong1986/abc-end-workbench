package com.abc.framework.core.exception;

public class ServiceValidException extends RuntimeException {

	public ServiceValidException() {
	}

	public ServiceValidException(String message) {
		super(message);
	}

	public ServiceValidException(Throwable cause) {
		super(cause);
	}

	public ServiceValidException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceValidException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
