package com.polymathiccoder.avempace.persistence.domain.operation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.polymathiccoder.avempace.persistence.domain.TableDefinition;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeSchema;
import com.polymathiccoder.avempace.persistence.domain.attribute.constraint.LocalSecondaryIndex;

public final class OperationValidator {
	public static <T extends Operation & RequiringPrimaryKeyHash & RequiringPrimaryKeyRange> List<String> hasPrimaryKeyAttributes(final T operation) {
		final List<String> errors = new ArrayList<>();

		errors.addAll(hasPrimaryKeyHashAttribute(operation));
		errors.addAll(hasPrimaryKeyRangeAttribute(operation));

		return errors;
	}

	public static <T extends Operation & RequiringPrimaryKeyHash & RequiringLocalSecondaryIndex> List<String> hasLocalSecondaryIndexAttributes(final T operation) {
		final List<String> errors = new ArrayList<>();

		errors.addAll(hasPrimaryKeyHashAttribute(operation));
		errors.addAll(hasLocalSecondaryIndexedAttribute(operation));

		return errors;
	}

	public static <T extends Operation & RequiringPrimaryKeyHash> List<String> hasPrimaryKeyHashAttribute(final T operation) {
		final List<String> errors = new ArrayList<>();

		final TableDefinition tableDefinition = operation.getTable().getDefinition();

		if (operation.getPrimaryKeyHashAttribute() == null) {
			errors.add(
					String.format(
							OperationValidationResult.MISSING_REQUIRED_PARAMETER_VALUE,
							tableDefinition.getHashKeySchema().getName().get()));
		}

		return errors;
	}

	public static <T extends Operation & RequiringPrimaryKeyRange> List<String> hasPrimaryKeyRangeAttribute(final T operation) {
		final List<String> errors = new ArrayList<>();

		final TableDefinition tableDefinition = operation.getTable().getDefinition();

		if (! operation.getPrimaryKeyRangeAttribute().isPresent()) {
			errors.add(
					String.format(
							OperationValidationResult.MISSING_REQUIRED_PARAMETER_VALUE,
							tableDefinition.getRangeKeySchema().get().getName().get()));
		}

		return errors;
	}

	public static <T extends Operation & RequiringLocalSecondaryIndex> List<String> hasLocalSecondaryIndexedAttribute(final T operation) {
		final List<String> errors = new ArrayList<>();

		final TableDefinition tableDefinition = operation.getTable().getDefinition();
		final String indexName = operation.getIndexName();

		if (! operation.getLocalSecondaryIndexAttribute(indexName).isPresent()) {
			String indexedAttributeName = StringUtils.EMPTY;
			for (final AttributeSchema indexedAttributeSchema : tableDefinition.getLocalSecondaryIndexes()) {
				LocalSecondaryIndex localSecondaryIndex = (LocalSecondaryIndex) indexedAttributeSchema.getConstraint();
				if (localSecondaryIndex.getIndexName().equals(indexName)) {
					indexedAttributeName = indexedAttributeSchema.getName().get();
				}
			}

			errors.add(
					String.format(
							OperationValidationResult.MISSING_REQUIRED_PARAMETER_VALUE,
							indexedAttributeName));
		}

		return errors;
	}
}
