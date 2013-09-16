package com.polymathiccoder.avempace.persistence.domain.attribute.constraint;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class PrimaryHashKey extends AttributeConstraint {
// Life cycle
	// Constructors
	private PrimaryHashKey() {
		super(AttributeConstraintType.PRIMARY_HASH_KEY);
	}

	// Factories
	public static PrimaryHashKey create() {
		return new PrimaryHashKey();
	}

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
