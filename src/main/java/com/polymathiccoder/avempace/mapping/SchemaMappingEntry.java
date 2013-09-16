package com.polymathiccoder.avempace.mapping;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.polymathiccoder.avempace.entity.domain.PropertySchema;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeSchema;

@AutoProperty
public class SchemaMappingEntry {
// Fields
	private final PropertySchema propertySchema;
	private final AttributeSchema attributeSchema;

// Life cycle
	public SchemaMappingEntry(final PropertySchema propertySchema, final AttributeSchema attributeSchema) {
		this.propertySchema = propertySchema;
		this.attributeSchema = attributeSchema;
	}

// Accessors and mutators
	public AttributeSchema getAttributeSchema() { return attributeSchema; }

	public PropertySchema getPropertySchema() { return propertySchema; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
