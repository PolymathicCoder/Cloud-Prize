package com.polymathiccoder.avempace.persistence.domain.value;

import java.text.NumberFormat;
import java.text.ParseException;

import org.apache.commons.lang3.ClassUtils;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class NumberValue extends ScalarValue<Number, String> {
// Life cycle
	public NumberValue(final Number intrinsicDataRepresentation) {
		super(intrinsicDataRepresentation);
	}

// Static behavior
	public static PersistentValue fromDynamoDBAttributeValue(final com.amazonaws.services.dynamodbv2.model.AttributeValue dynamoDBAttributeValue) {
		return new NumberValue(NumberValue.toIntrinsicDataRepresentation(dynamoDBAttributeValue.getN()));
	}

	static Number toIntrinsicDataRepresentation(final String physicalDataRepresentation) {
		try {
			return NumberFormat.getInstance().parse(physicalDataRepresentation);
		} catch (final ParseException parseException) {
			throw new IllegalStateException();
		}
	}

	public static PersistentValue fromEntityPropertyValue(final Object entityPropertyValue) {
		if (ClassUtils.isAssignable(entityPropertyValue.getClass(), Number.class)) {
			return new NumberValue((Number) entityPropertyValue);
		} else {
			throw new IllegalArgumentException();
		}
	}

// Behavior
	@Override
	public String toPhysicalDataRepresentation() {
		return intrinsicDataRepresentation.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T toEntityPropertyValue(final Class<T> clazz) {
		T entityPropertyValue = null;
		try {
			final Number numericValue = NumberFormat.getInstance().parse(intrinsicDataRepresentation.toString());

			if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
				entityPropertyValue = (T) new Byte(numericValue.byteValue());
			} else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
				entityPropertyValue = (T) new Short(numericValue.shortValue());
			} else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
				entityPropertyValue = (T) new Integer(numericValue.intValue());
			} else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
				entityPropertyValue = (T) new Long(numericValue.longValue());
			} else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
				entityPropertyValue = (T) new Float(numericValue.floatValue());
			} else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
				entityPropertyValue = (T) new Double(numericValue.doubleValue());
			} else {
				//TODO Handle better
				throw new IllegalArgumentException();
			}
		} catch (final ParseException parseException) {
			//TODO Handle better
			throw new IllegalArgumentException();
		}

		return entityPropertyValue;
	}

	@Override
	public com.amazonaws.services.dynamodbv2.model.AttributeValue toDynamoDBAttributeValue() {
		return new com.amazonaws.services.dynamodbv2.model.AttributeValue()
				.withN(toPhysicalDataRepresentation());
	}

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
