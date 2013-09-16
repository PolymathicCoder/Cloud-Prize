package com.polymathiccoder.avempace.persistence.domain.attribute;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class Attribute {
// Fields
	private final AttributeSchema schema;
	private final AttributeValue value;

// Life cycle
	public Attribute(final AttributeSchema schema, final AttributeValue value) {
		this.schema = schema;
		this.value = value;
	}

// Accessors and mutators
	public AttributeSchema getSchema() { return schema; }

	public AttributeValue getValue() { return value; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
