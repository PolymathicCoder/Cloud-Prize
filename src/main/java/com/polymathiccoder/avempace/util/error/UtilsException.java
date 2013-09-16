package com.polymathiccoder.avempace.util.error;


@SuppressWarnings("serial")
public abstract class UtilsException extends RuntimeException {
// Life cycle
	protected UtilsException(final String message) {
		super(message);
	}

	protected UtilsException(final String message, final Throwable cause) {
		super(message, cause);
	}
}