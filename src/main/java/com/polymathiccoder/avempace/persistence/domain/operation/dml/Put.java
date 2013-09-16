package com.polymathiccoder.avempace.persistence.domain.operation.dml;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.util.HashSet;
import java.util.Set;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.google.common.base.Optional;
import com.polymathiccoder.avempace.persistence.domain.Table;
import com.polymathiccoder.avempace.persistence.domain.Tuple;
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
public final class Put extends DMLOperation implements RequiringPrimaryKeyHash, RequiringPrimaryKeyRange, BatchableWrite {
// Fields
	// Must have primary key
	private final Tuple tuple;
	private final Set<Attribute> expectedAttributes;

// Life cycle
	private Put(final Builder builder) {
		super(builder.table);
		tuple = builder.tuple;
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
		return selectFirst(tuple.getAttributes(), having(on(Attribute.class).getSchema().getConstraint().getType(), not(equalTo(AttributeConstraintType.PRIMARY_HASH_KEY))));
	}

	@Override
	public Optional<? extends Attribute> getPrimaryKeyRangeAttribute() {
		final Attribute attribute = selectFirst(tuple.getAttributes(), having(on(Attribute.class).getSchema().getConstraint().getType(), equalTo(AttributeConstraintType.PRIMARY_RANGE_KEY)));
		return Optional.fromNullable(attribute);
	}

	public Optional<? extends Attribute> getVersionAttribute() {
		final Attribute attribute = selectFirst(expectedAttributes, having(on(Attribute.class).getSchema().getConstraint().getType(), equalTo(AttributeConstraintType.VERSION)));
		return Optional.fromNullable(attribute);
	}

// Types
	public static class Builder {
		// Required
		private Table table;
		private Tuple tuple;

		// Optional
		private Set<Attribute> expectedAttributes;

		private Builder() {
			defaults();
		}

		public static Builder create(final Table table, final Tuple tuple) {
			final Builder builder = new Builder();
			builder.table = table;
			builder.tuple = tuple;
			return builder;
		}

		private void defaults() {
			expectedAttributes = new HashSet<>();
		}

		public Builder withExpectedAttribute(final Attribute expectedAttribute) {
			expectedAttributes.add(expectedAttribute);
			return this;
		}

		public Put build() {
			return new Put(this);
		}
	}

// Accessors and mutators
	public Tuple getTuple() { return tuple; }

	public Set<Attribute> getExpectedAttributes() { return expectedAttributes; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
