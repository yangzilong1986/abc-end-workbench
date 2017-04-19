package com.abc.core.crypto;

public class CryptoException extends RuntimeException {

	public CryptoException() {
	}

	public CryptoException(String message) {
		super(message);
	}

	public CryptoException(Throwable cause) {
		super(cause);
	}

	public CryptoException(String message, Throwable cause) {
		super(message, cause);
	}

	public CryptoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
