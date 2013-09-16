package com.polymathiccoder.avempace.entity.domain;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class PropertySchema {
// Fields
	private final PropertyName name;
	private final PropertyType type;

// Life cycle
	public PropertySchema(final PropertyName name, final PropertyType type) {
		this.name = name;
		this.type = type;
	}

// Accessors and mutators
	public PropertyName getName() { return name; }

	public PropertyType getType() { return type; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
