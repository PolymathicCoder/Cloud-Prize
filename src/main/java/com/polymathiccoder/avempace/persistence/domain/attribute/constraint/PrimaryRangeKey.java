package com.polymathiccoder.avempace.persistence.domain.attribute.constraint;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class PrimaryRangeKey extends AttributeConstraint {
// Life cycle
	// Constructors
	private PrimaryRangeKey() {
		super(AttributeConstraintType.PRIMARY_RANGE_KEY);
	}

	// Factories
	public static PrimaryRangeKey create() {
		return new PrimaryRangeKey();
	}

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
