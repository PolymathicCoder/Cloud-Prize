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
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeSchema;
import com.polymathiccoder.avempace.persistence.domain.attribute.constraint.AttributeConstraint.AttributeConstraintType;
import com.polymathiccoder.avempace.persistence.domain.operation.DMLOperation;
import com.polymathiccoder.avempace.persistence.domain.operation.OperationValidationResult;
import com.polymathiccoder.avempace.persistence.domain.operation.OperationValidator;
import com.polymathiccoder.avempace.persistence.domain.operation.RequiringPrimaryKeyHash;
import com.polymathiccoder.avempace.persistence.domain.operation.RequiringPrimaryKeyRange;
import com.polymathiccoder.avempace.persistence.error.InvalidOperationException;

@AutoProperty
public final class Get extends DMLOperation implements RequiringPrimaryKeyHash, RequiringPrimaryKeyRange {
// Fields
	// Must be specified
	private final Set<Attribute> keyAttributes;
	// Verify the exist
	private final Set<AttributeSchema> includedAttributesSchemas;

	private final boolean isConsistentlyRead;

// Life cycle
	private Get(final Builder builder) {
		super(builder.table);
		keyAttributes = builder.keyAttributes;
		includedAttributesSchemas = builder.includedAttributesSchemas;
		isConsistentlyRead = builder.isConsistentlyRead;
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

	public Optional<? extends AttributeSchema> getVersionAttributeSchema() {
		final AttributeSchema attributeSchema = selectFirst(includedAttributesSchemas, having(on(AttributeSchema.class).getConstraint().getType(), equalTo(AttributeConstraintType.VERSION)));
		return Optional.fromNullable(attributeSchema);
	}

	public Set<AttributeSchema> getNonConstrainedAttributesSchemas() {
		Set<AttributeSchema> nonConstrainedAttributesSchemas = new HashSet<>();
		nonConstrainedAttributesSchemas.addAll(select(includedAttributesSchemas, having(on(AttributeSchema.class).getConstraint().getType(), not(equalTo(AttributeConstraintType.NONE)))));
		return nonConstrainedAttributesSchemas;
	}

// Types
	public static class Builder {
		// Required
		private Table table;
		private Set<Attribute> keyAttributes;

		// Optional
		private Set<AttributeSchema> includedAttributesSchemas;
		private boolean isConsistentlyRead;

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
			includedAttributesSchemas = new HashSet<>();
			isConsistentlyRead = false;
		}

		public Builder withIncludedAttributeSchema(final AttributeSchema includedAttributeSchema) {
			this.includedAttributesSchemas.add(includedAttributeSchema);
			return this;
		}

		public Builder withConsistentlyRead(final boolean isConsistentlyRead) {
			this.isConsistentlyRead = isConsistentlyRead;
			return this;
		}

		public Get build() {
			return new Get(this);
		}
	}

// Accessors and mutators
	public boolean isConsistentlyRead() { return isConsistentlyRead; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
