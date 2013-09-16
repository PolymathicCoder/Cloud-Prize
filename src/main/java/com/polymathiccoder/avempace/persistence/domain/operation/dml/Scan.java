package com.polymathiccoder.avempace.persistence.domain.operation.dml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.polymathiccoder.avempace.persistence.domain.Table;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeSchema;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeValueCriterion;
import com.polymathiccoder.avempace.persistence.domain.operation.DMLOperation;
import com.polymathiccoder.avempace.persistence.domain.operation.OperationValidationResult;
import com.polymathiccoder.avempace.persistence.error.InvalidOperationException;

@AutoProperty
public final class Scan extends DMLOperation {
// Static fields
	private static final int DEFAULT_ITEM_LIMIT = 10;

// Fields
	// Must be specified hash at least
	private final Set<AttributeValueCriterion> criteria;

	private final Set<AttributeSchema> includedAttributesSchemas;

	private final boolean isConsistentlyRead;

	private final int limit;

// Life cycle
	private Scan(final Builder builder) {
		super(builder.table);
		criteria = builder.criteria;
		includedAttributesSchemas = builder.includedAttributesSchemas;
		isConsistentlyRead = builder.isConsistentlyRead;
		limit = builder.limit;
	}

// Behavior
	@Override
	public void validate() {
		final List<String> errors = new ArrayList<>();
		final OperationValidationResult operationValidationResult = OperationValidationResult.create(this, errors);
		if (! operationValidationResult.getVerdict().isValid()) {
			throw new InvalidOperationException(operationValidationResult);
		}
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

		public Scan build() {
			return new Scan(this);
		}
	}

// Accessors and mutators
	public Set<AttributeValueCriterion> getCriteria() { return criteria; }

	public Set<AttributeSchema> getIncludedAttributesSchemas() { return includedAttributesSchemas; }

	public boolean isConsistentlyRead() { return isConsistentlyRead; }

	public int getLimit() {return limit; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
