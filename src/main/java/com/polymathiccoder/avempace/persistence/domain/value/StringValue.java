package com.polymathiccoder.avempace.persistence.domain.value;

import org.apache.commons.lang3.ClassUtils;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.polymathiccoder.avempace.util.JsonUtils;

@AutoProperty
public class StringValue extends ScalarValue<String, String> {
// Life cycle
	public StringValue(final String intrinsicDataRepresentation) {
		super(intrinsicDataRepresentation);
	}

// Static behavior
	public static PersistentValue fromDynamoDBAttributeValue(final com.amazonaws.services.dynamodbv2.model.AttributeValue dynamoDBAttributeValue) {
		return new StringValue(StringValue.toIntrinsicDataRepresentation(dynamoDBAttributeValue.getS()));
	}

	static String toIntrinsicDataRepresentation(final String physicalDataRepresentation) {
		return physicalDataRepresentation;
	}

	public static PersistentValue fromEntityPropertyValue(final Object entityPropertyValue) {
		if (ClassUtils.isAssignable(entityPropertyValue.getClass(), String.class)) {
			return new StringValue((String) entityPropertyValue);
		} else {
			return new StringValue(JsonUtils.toJson(entityPropertyValue.getClass(), entityPropertyValue));
		}
	}

// Behavior
	@Override
	public String toPhysicalDataRepresentation() {
		return intrinsicDataRepresentation;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <F> F toEntityPropertyValue(Class<F> entityPropertyValueType) {
		if (ClassUtils.isAssignable(entityPropertyValueType, String.class)) {
			return (F) intrinsicDataRepresentation;
		} else {
			return JsonUtils.fromJson(entityPropertyValueType, intrinsicDataRepresentation);
		}
	}

	@Override
	public com.amazonaws.services.dynamodbv2.model.AttributeValue toDynamoDBAttributeValue() {
		return new com.amazonaws.services.dynamodbv2.model.AttributeValue().withS(toPhysicalDataRepresentation());
	}

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
