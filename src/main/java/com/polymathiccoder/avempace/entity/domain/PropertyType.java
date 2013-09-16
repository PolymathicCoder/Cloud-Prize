package com.polymathiccoder.avempace.entity.domain;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class PropertyType {
// Fields
	private final Class<?> value;

// Life cycle
	public PropertyType(final Class<?> value) {
		this.value = value;
	}

// Accessors and mutators
	public Class<?> get() { return value; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
