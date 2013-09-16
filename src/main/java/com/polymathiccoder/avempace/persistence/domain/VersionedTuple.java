package com.polymathiccoder.avempace.persistence.domain;

import java.util.Set;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.polymathiccoder.avempace.persistence.domain.attribute.Attribute;

@AutoProperty
public class VersionedTuple extends Tuple {
// Fields
	// 1MB tops including other attributes
	//public final Attribute version;

// life cycle
	public VersionedTuple(final Table belongsIn, final Attribute... attributes) {
		super(belongsIn, attributes);

		/*final AttributeSchema attributeSchema = new AttributeSchema(
				new AttributeName(
						"__version__",
						"version"),
				new AttributeType(
						NumberValue.class,
						Long.class),
				new AttributeConstraint(AttributeConstraintType.VERSION));
		version = new Attribute(
				attributeSchema,
				AttributeValue.fromEntityPropertyValue(attributeSchema, System.nanoTime()));*/
	}

	public VersionedTuple(final Table belongsIn, final Set<Attribute> attributes) {
		super(belongsIn, attributes);

		/*final AttributeSchema attributeSchema = new AttributeSchema(
				new AttributeName(
						"__version__",
						"version"),
				new AttributeType(
						NumberValue.class,
						Long.class),
				new AttributeConstraint(AttributeConstraintType.VERSION));
		version = new Attribute(
				attributeSchema,
				AttributeValue.fromEntityPropertyValue(attributeSchema, System.nanoTime()));*/
	}

// Accessors and mutators
	//FIXME
	public Attribute getVersion() { return null; }

	// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
