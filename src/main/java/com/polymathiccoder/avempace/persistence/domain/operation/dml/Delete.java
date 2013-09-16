package com.polymathiccoder.avempace.persistence.domain.operation.dml;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.util.HashSet;
import java.util.Set;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.google.common.base.Optional;
import com.polymathiccoder.avempace.persistence.domain.Table;
import com.polymathiccoder.avempace.persistence.domain.attribute.Attribute;
import com.polymathiccoder.avempace.persistence.domain.attribute.constraint.AttributeConstraint.AttributeConstraintType;
import com.polymathiccoder.avempace.persistence.domain.operation.BatchableWrite;
import com.polymathiccoder.avempace.persistence.domain.operation.DMLOperation;
import com.polymathiccoder.avempace.persistence.domain.operation.OperationValidationResult;
import com.polymathiccoder.avempace.persistence.domain.operation.OperationValidator;
import com.polymathiccoder.avempace.persistence.domain.operation.RequiringPrimaryKeyHash;
import com.polymathiccoder.avempace.persistence.domain.operation.RequiringPrimaryKeyRange;
import com.polymathiccoder.avempace.persistence.error.InvalidOperationException;

@AutoProperty
public final class Delete extends DMLOperation implements RequiringPrimaryKeyHash, RequiringPrimaryKeyRange, BatchableWrite {
// Fields
	// Must be specified
	private final Set<Attribute> keyAttributes;
	// Verify the exist
	private final Set<Attribute> expectedAttributes;

// Life cycle
	private Delete(final Builder builder) {
		super(builder.table);
		keyAttributes = builder.keyAttributes;
		expectedAttributes = builder.expectedAttributes;
	}

// Behavior
	@Override
	public void validate() {
		final OperationValidationResult operationValidationResult = OperationValidationResult.create(this, OperationValidator.hasPrimaryKeyAttributes(this));
		if (! operationValidationResult.getVerdict().isValid()) {
			throw new InvalidOperationException(operationValidationResult);
		}
	}

	@Override
	public Attribute getPrimaryKeyHashAttribute() {
		return selectFirst(keyAttributes, having(on(Attribute.class).getSchema().getConstraint().getType(), equalTo(AttributeConstraintType.PRIMARY_HASH_KEY)));
	}

	@Override
	public Optional<? extends Attribute> getPrimaryKeyRangeAttribute() {
		final Attribute attribute = selectFirst(keyAttributes, having(on(Attribute.class).getSchema().getConstraint().getType(), equalTo(AttributeConstraintType.PRIMARY_RANGE_KEY)));
		return Optional.fromNullable(attribute);
	}

	public Optional<? extends Attribute> getVersionAttribute() {
		final Attribute attribute = selectFirst(expectedAttributes, having(on(Attribute.class).getSchema().getConstraint().getType(), equalTo(AttributeConstraintType.VERSION)));
		return Optional.fromNullable(attribute);
	}

	public Set<Attribute> getNonConstrainedAttributes() {
		Set<Attribute> nonConstrainedAttributes = new HashSet<>();
		nonConstrainedAttributes.addAll(select(expectedAttributes, having(on(Attribute.class).getSchema().getConstraint().getType(), not(equalTo(AttributeConstraintType.NONE)))));
		return nonConstrainedAttributes;
	}

// Types
	public static class Builder {
		// Required
		private Table table;

		private Set<Attribute> keyAttributes;

		// Optional
		private Set<Attribute> expectedAttributes;

		private Builder() {
			defaults();
		}

		public static Builder create(final Table table, final Set<Attribute> keyAttributes) {
			final Builder builder = new Builder();
			builder.table = table;
			builder.keyAttributes = keyAttributes;
			return builder;
		}

		private void defaults() {
			keyAttributes = new HashSet<>();
			expectedAttributes = new HashSet<>();
		}

		public Builder withExpectedAttribute(final Attribute expectedAttribute) {
			this.expectedAttributes.add(expectedAttribute);
			return this;
		}

		public Delete build() {
			return new Delete(this);
		}
	}

// Accessors and mutators
	public Set<Attribute> getKeyAttributes() { return keyAttributes; }

	public Set<Attribute> getExpectedAttributes() { return expectedAttributes; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
