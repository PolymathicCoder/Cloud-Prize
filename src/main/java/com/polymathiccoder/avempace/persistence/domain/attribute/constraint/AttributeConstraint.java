package com.polymathiccoder.avempace.persistence.domain.attribute.constraint;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public abstract class AttributeConstraint {
// Fields
	private AttributeConstraintType type;

// Life cycle
	public AttributeConstraint(final AttributeConstraintType type) {
		this.type = type;
	}

// Types
	public enum AttributeConstraintType {
		//TODO Remove
		NONE,
		PRIMARY_HASH_KEY,
		PRIMARY_RANGE_KEY,
		VERSION,
		// Cannot be of type set
		LOCAL_SECONDARY_INDEX_KEY
	}

// Accessors and mutators
	public AttributeConstraintType getType() { return type; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}

