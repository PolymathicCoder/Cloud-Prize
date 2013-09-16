package com.polymathiccoder.avempace.persistence.domain.operation.dml;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashSet;
import java.util.Set;

import org.pojomatic.Pojomatic;

import com.google.common.base.Optional;
import com.polymathiccoder.avempace.persistence.domain.Table;
import com.polymathiccoder.avempace.persistence.domain.attribute.Attribute;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeSchema;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeValueCriterion;
import com.polymathiccoder.avempace.persistence.domain.attribute.constraint.AttributeConstraint.AttributeConstraintType;
import com.polymathiccoder.avempace.persistence.domain.operation.OperationValidationResult;
import com.polymathiccoder.avempace.persistence.domain.operation.OperationValidator;
import com.polymathiccoder.avempace.persistence.domain.operation.RequiringLocalSecondaryIndex;
import com.polymathiccoder.avempace.persistence.error.InvalidOperationException;

public final class QueryByLocalSecondaryIndex extends Query implements RequiringLocalSecondaryIndex {
// Fields
	private final String indexName;

// Life cycle
	private QueryByLocalSecondaryIndex(final Builder builder) {
		super(builder.table,
				builder.conditions,
				builder.includedAttributesSchemas,
				builder.isConsistentlyRead,
				builder.limit);
		indexName = builder.indexName;
	}

// Behavior
	@Override
	public void validate() {
		final OperationValidationResult operationValidationResult = OperationValidationResult.create(this, OperationValidator.hasLocalSecondaryIndexAttributes(this));
		if (! operationValidationResult.getVerdict().isValid()) {
			throw new InvalidOperationException(operationValidationResult);
		}
	}

	@Override
	public Optional<Attribute> getLocalSecondaryIndexAttribute(final String indexName) {
		final AttributeValueCriterion attributeValueCriterion = selectFirst(
				conditions,
				having(
						on(AttributeSchema.class).getConstraint().getType(),
						equalTo(AttributeConstraintType.LOCAL_SECONDARY_INDEX_KEY)));

		return Optional.of(new Attribute(
				attributeValueCriterion.getOf(),
				attributeValueCriterion.getCriteria().iterator().next().getArguments().iterator().next()));
	}

	@Override
	public String getIndexName() {
		return indexName;
	}

	public Set<AttributeValueCriterion> getLocalSecondaryIndexConditions() {
		Set<AttributeValueCriterion> rangeKeyConditions = new HashSet<>();
		rangeKeyConditions.addAll(
				select(
						conditions,
						having(
								on(AttributeValueCriterion.class).getOf().getConstraint().getType(),
								equalTo(AttributeConstraintType.LOCAL_SECONDARY_INDEX_KEY))));
		return rangeKeyConditions;
	}

// Types
	public static class Builder {
		// Required
		private Table table;
		private String indexName;
		private Set<AttributeValueCriterion> conditions;

		// Optional
		private Set<AttributeSchema> includedAttributesSchemas;
		private boolean isConsistentlyRead;
		private int limit;

		private Builder() {
			defaults();
		}

		public static Builder create(final Table table, final String indexName, final Set<AttributeValueCriterion> conditions) {
			final Builder builder = new Builder();
			builder.table = table;
			builder.indexName = indexName;
			builder.conditions = conditions;
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

		public QueryByLocalSecondaryIndex build() {
			return new QueryByLocalSecondaryIndex(this);
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
