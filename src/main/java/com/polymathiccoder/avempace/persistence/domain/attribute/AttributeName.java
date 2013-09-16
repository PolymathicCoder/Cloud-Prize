package com.polymathiccoder.avempace.persistence.domain.attribute;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class AttributeName {
// Fields
	private final String value;

// Life cycle
	public AttributeName(final String value) {
		this.value = value;
	}

// Accessors and mutators
	public String get() { return value; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
