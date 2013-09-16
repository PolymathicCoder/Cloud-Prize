package com.polymathiccoder.avempace.persistence.error;

@SuppressWarnings("serial")
public class NotFoundException extends PersistenceException {
// Static fields
	public static final String ERROR_DML_GET__NOT_FOUND = "Persistence: Could not find entity of type '%s' to an IP";
    public static final String ERROR_DML_GET__DUPLACTE = "Persistence: Could not resolve the IP '%s' to a hostname";
// Life cycle
    protected NotFoundException(final String message) {
		super(message);
	}

	protected NotFoundException(final String message, final Throwable cause) {
		super(message, cause);
	}
}