package com.polymathiccoder.avempace.persistence.domain.operation.dml;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashSet;
import java.util.Set;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.google.common.base.Optional;
import com.polymathiccoder.avempace.persistence.domain.Table;
import com.polymathiccoder.avempace.persistence.domain.attribute.Attribute;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeSchema;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeValueCriterion;
import com.polymathiccoder.avempace.persistence.domain.attribute.constraint.AttributeConstraint.AttributeConstraintType;
import com.polymathiccoder.avempace.persistence.domain.operation.OperationValidationResult;
import com.polymathiccoder.avempace.persistence.domain.operation.OperationValidator;
import com.polymathiccoder.avempace.persistence.domain.operation.RequiringPrimaryKeyRange;
import com.polymathiccoder.avempace.persistence.error.InvalidOperationException;

@AutoProperty
public final class QueryByPrimaryKeyRange extends Query implements RequiringPrimaryKeyRange {
// Life cycle
	private QueryByPrimaryKeyRange(final Builder builder) {
		super(builder.table,
				builder.criteria,
				builder.includedAttributesSchemas,
				builder.isConsistentlyRead,
				builder.limit);
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
	public Optional<Attribute> getPrimaryKeyRangeAttribute() {
		final AttributeValueCriterion attributeValueCriterion = selectFirst(
				conditions,
				having(
						on(AttributeValueCriterion.class).getOf().getConstraint().getType(),
						equalTo(AttributeConstraintType.PRIMARY_RANGE_KEY)));

		return Optional.of(new Attribute(
				attributeValueCriterion.getOf(),
				attributeValueCriterion.getCriteria().iterator().next().getArguments().iterator().next()));
	}

// Types
	public static class Builder {
		// Required
		private Table table;
		private Set<AttributeValueCriterion> criteria;

		// Optional
		private Set<AttributeSchema> includedAttributesSchemas;
		private boolean isConsistentlyRead;
		private int limit;

		private Builder() {
			defaults();
		}

		public static Builder create(final Table table, final Set<AttributeValueCriterion> criteria) {
			final Builder builder = new Builder();
			builder.table = table;
			builder.criteria = criteria;
			return builder;
		}

		private void defaults() {
			includedAttributesSchemas = new HashSet<>();
			isConsistentlyRead = false;
			limit = DEFAULT_ITEM_LIMIT;
		}

		public Builder withIncludedAttributeSchema(final AttributeSchema includedAttributeSchema) {
			this.includedAttributesSchemas.add(includedAttributeSchema);
			return this;
		}

		public Builder withConsistentlyRead(final boolean isConsistentlyRead) {
			this.isConsistentlyRead = isConsistentlyRead;
			return this;
		}

		public Builder withLimit(final int limit) {
			this.limit = limit;
			return this;
		}

		public QueryByPrimaryKeyRange build() {
			return new QueryByPrimaryKeyRange(this);
		}
	}

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
