package com.polymathiccoder.avempace.persistence.domain.value;

import java.lang.reflect.Type;


public abstract class PersistentValue {
// Static behavior
	public static PersistentValue fromDynamoDBAttributeValue(final com.amazonaws.services.dynamodbv2.model.AttributeValue dynamoDBAttributeValue) {
		throw new IllegalStateException();
	}

	public static PersistentValue fromEntityPropertyValue(final Object entityPropertyValue) {
		throw new IllegalStateException();
	}

// Behavior
	public abstract com.amazonaws.services.dynamodbv2.model.AttributeValue toDynamoDBAttributeValue();

	public abstract Object toPojo(final Type type);
}
