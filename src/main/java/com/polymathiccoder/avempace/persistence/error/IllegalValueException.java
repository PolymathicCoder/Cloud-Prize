package com.polymathiccoder.avempace.persistence.error;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.polymathiccoder.avempace.persistence.domain.operation.OperationValidationResult;

@SuppressWarnings("serial")
public class IllegalValueException extends PersistenceException {
// Static fields
	public static final String ERROR_ILLEGAL_VALUE = "Could not persist the value '%s' as '%s'";
// Life cycle
    public IllegalValueException(final OperationValidationResult operationValidationResult) {
		super(Joiner.on("\n").join(operationValidationResult.getVerdict().getErrors()));
	}
}