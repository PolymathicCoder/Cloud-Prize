package com.polymathiccoder.avempace.persistence.domain.attribute;

import static org.fest.reflect.core.Reflection.staticMethod;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.polymathiccoder.avempace.persistence.domain.value.PersistentValue;

@AutoProperty
public class AttributeValue {
// Fields
	private final PersistentValue value;

// Life cycle
	public AttributeValue(final PersistentValue value) {
		this.value = value;
	}

// Static behavior
	public static AttributeValue fromDynamoDBAttributeValue(AttributeSchema attributeSchema, com.amazonaws.services.dynamodbv2.model.AttributeValue dynamoDBAttributeValue) {
		final PersistentValue persistentValue = staticMethod("fromDynamoDBAttributeValue")
				.withReturnType(PersistentValue.class)
				.withParameterTypes(com.amazonaws.services.dynamodbv2.model.AttributeValue.class)
				.in(attributeSchema.getType().get())
				.invoke(dynamoDBAttributeValue);

		return new AttributeValue(persistentValue);
	}

	public static AttributeValue fromEntityPropertyValue(AttributeSchema attributeSchema, final Object entityPropertyValue) {
		final PersistentValue persistentValue = staticMethod("fromEntityPropertyValue")
				.withReturnType(PersistentValue.class)
				.withParameterTypes(Object.class)
				.in(attributeSchema.getType().get())
				.invoke(entityPropertyValue);

		return new AttributeValue(persistentValue);
	}

// Accessors and mutators
	public PersistentValue get() { return value; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
