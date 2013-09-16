package com.polymathiccoder.avempace.persistence.domain;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashSet;
import java.util.Set;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.google.common.base.Optional;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeSchema;
import com.polymathiccoder.avempace.persistence.domain.attribute.constraint.AttributeConstraint.AttributeConstraintType;

@AutoProperty
public class TableDefinition {
// Static fields
	private static final long DEFAULT_READ_CAPACITY_UNITS = 5l;
	private static final long DEFAULT_WRITE_CAPACITY_UNITS = 5l;

// Fields
	private final String name;
	private final long readCapacityUnits;
	private final long writeCapacityUnits;

	// Must define range key if you have indexes and max number of indexes is 5
	private final Set<AttributeSchema> attributesSchemas;

// Life cycle
	private TableDefinition(final Builder builder) {
		name = builder.name;
		readCapacityUnits = builder.readCapacityUnits;
		writeCapacityUnits = builder.writeCapacityUnits;
		attributesSchemas = builder.attributesSchemas;
	}

// Behavior
	public AttributeSchema getHashKeySchema() {
		return selectFirst(attributesSchemas, having(on(AttributeSchema.class).getConstraint().getType(), equalTo(AttributeConstraintType.PRIMARY_HASH_KEY)));
	}

	public Optional<? extends AttributeSchema> getRangeKeySchema() {
		final AttributeSchema attributeSchema = selectFirst(attributesSchemas, having(on(AttributeSchema.class).getConstraint().getType(), equalTo(AttributeConstraintType.PRIMARY_RANGE_KEY)));
		return Optional.fromNullable(attributeSchema);
	}

	public Optional<? extends AttributeSchema> getVersionSchema() {
		final AttributeSchema attributeSchema = selectFirst(attributesSchemas, having(on(AttributeSchema.class).getConstraint().getType(), equalTo(AttributeConstraintType.VERSION)));
		return Optional.fromNullable(attributeSchema);
	}

	public Set<AttributeSchema> getLocalSecondaryIndexes() {
		final Set<AttributeSchema> attributeSchemas = new HashSet<>();
		attributeSchemas.addAll(select(attributesSchemas, having(on(AttributeSchema.class).getConstraint().getType(), equalTo(AttributeConstraintType.LOCAL_SECONDARY_INDEX_KEY))));
		return attributeSchemas;
	}

// Types
	public static class Builder {
	// Fields
		// Required
		private String name;
		private Set<AttributeSchema> attributesSchemas;

		// Optionals
		private long readCapacityUnits;
		private long writeCapacityUnits;

	// Life cycle
		private Builder() {
			defaults();
		}

		// Defaults
		private void defaults() {
			attributesSchemas = new HashSet<>();
			readCapacityUnits = DEFAULT_READ_CAPACITY_UNITS;
			writeCapacityUnits = DEFAULT_WRITE_CAPACITY_UNITS;
		}

	// Factories
		public static Builder create(final String name, final Set<AttributeSchema> attributesSchemas) {
			final Builder builder = new Builder();
			builder.name = name;
			builder.attributesSchemas = attributesSchemas;
			return builder;
		}

	// Chained mutators
		public Builder withReadCapacityUnits(final long readCapacityUnits) {
			this.readCapacityUnits = readCapacityUnits;
			return this;
		}

		public Builder withWriteCapacityUnits(final long writeCapacityUnits) {
			this.writeCapacityUnits = writeCapacityUnits;
			return this;
		}

	// Build
		public TableDefinition build() {
			return new TableDefinition(this);
		}
	}

// Accessors and mutators
	public String getName() { return name;}

	public Set<AttributeSchema> getAttributesSchemas() { return attributesSchemas; }

	public long getReadCapacityUnits() { return readCapacityUnits; }

	public long getWriteCapacityUnits() { return writeCapacityUnits; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
