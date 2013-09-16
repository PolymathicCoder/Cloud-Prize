package com.polymathiccoder.avempace.persistence.error;


@SuppressWarnings("serial")
public abstract class PersistenceException extends RuntimeException {
// Life cycle
	protected PersistenceException(final String message) {
		super(message);
	}

	protected PersistenceException(final String message, final Throwable cause) {
		super(message, cause);
	}
}