package com.abc.framework.core.exception;

public class ControllerValidException extends RuntimeException {

	public ControllerValidException() {
		super();
	}

	public ControllerValidException(String message) {
		super(message);
	}

	public ControllerValidException(Throwable cause) {
		super(cause);
	}

	public ControllerValidException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ControllerValidException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
