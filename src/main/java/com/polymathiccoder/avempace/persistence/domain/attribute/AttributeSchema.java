package com.polymathiccoder.avempace.persistence.domain.attribute;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.polymathiccoder.avempace.persistence.domain.attribute.constraint.AttributeConstraint;
import com.polymathiccoder.avempace.persistence.domain.value.BinarySetValue;
import com.polymathiccoder.avempace.persistence.domain.value.BinaryValue;
import com.polymathiccoder.avempace.persistence.domain.value.NumberSetValue;
import com.polymathiccoder.avempace.persistence.domain.value.NumberValue;
import com.polymathiccoder.avempace.persistence.domain.value.PersistentValue;
import com.polymathiccoder.avempace.persistence.domain.value.StringSetValue;
import com.polymathiccoder.avempace.persistence.domain.value.StringValue;

@AutoProperty
public class AttributeSchema {
// Fields
	private final AttributeName name;
	private final AttributeType type;
	private final AttributeConstraint constraint;

// Life cycle
	// Constructors
	private AttributeSchema(final AttributeName name, final AttributeType type, final AttributeConstraint constraint) {
		this.name = name;
		this.type = type;
		this.constraint = constraint;
	}

	// Factories
	public static AttributeSchema create(final String name, final Class<? extends PersistentValue> type, final AttributeConstraint constraint) {
		return new AttributeSchema(
				new AttributeName(name),
				new AttributeType(type),
				constraint);
	}

// Behavior
	public AttributeDefinition toDynamoDBAttributeDefinition() {
		AttributeDefinition attributeDefinition = new AttributeDefinition();
		attributeDefinition.withAttributeName(name.get());
		if (type.get().equals(BinarySetValue.class)) {
			return attributeDefinition.withAttributeType("BS");
		} else if (type.get().equals(BinaryValue.class)) {
			return attributeDefinition.withAttributeType("B");
		} else if (type.get().equals(NumberSetValue.class)) {
			return attributeDefinition.withAttributeType("NS");
		} else if (type.get().equals(NumberValue.class)) {
			return attributeDefinition.withAttributeType("N");
		} else if (type.get().equals(StringSetValue.class)) {
			return attributeDefinition.withAttributeType("SS");
		} else if (type.get().equals(StringValue.class)) {
			return attributeDefinition.withAttributeType("S");
		}
		//TODO Handle better
		throw new RuntimeException();
	}

// Accessors and mutators
	public AttributeName getName() { return name; }

	public AttributeType getType() { return type; }

	public AttributeConstraint getConstraint() { return constraint; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
