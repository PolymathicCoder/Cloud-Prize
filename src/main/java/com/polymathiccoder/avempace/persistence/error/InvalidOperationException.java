package com.polymathiccoder.avempace.persistence.error;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.polymathiccoder.avempace.persistence.domain.operation.OperationValidationResult;

@SuppressWarnings("serial")
public class InvalidOperationException extends PersistenceException {
// Life cycle
    public InvalidOperationException(final OperationValidationResult operationValidationResult) {
		super(Joiner.on("\n").join(operationValidationResult.getVerdict().getErrors()));
	}
}