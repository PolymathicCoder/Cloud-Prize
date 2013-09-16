package com.polymathiccoder.avempace.persistence.domain.value;

import java.nio.ByteBuffer;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.polymathiccoder.avempace.util.BinaryUtils;

@AutoProperty
public class BinaryValue extends ScalarValue<ByteBuffer, ByteBuffer> {
// Life cycle
	public BinaryValue(final ByteBuffer intrinsicDataRepresentation) {
		super(intrinsicDataRepresentation);
	}

// Static behavior
	public static PersistentValue fromDynamoDBAttributeValue(final com.amazonaws.services.dynamodbv2.model.AttributeValue dynamoDBAttributeValue) {
		return new BinaryValue(BinaryValue.toIntrinsicDataRepresentation(dynamoDBAttributeValue.getB()));
	}

	static ByteBuffer toIntrinsicDataRepresentation(final ByteBuffer physicalDataRepresentation) {
		return physicalDataRepresentation;
	}

	public static PersistentValue fromEntityPropertyValue(final Object entityPropertyValue) {
		return new BinaryValue(BinaryUtils.toBinary(entityPropertyValue.getClass(), entityPropertyValue));
	}

// Behavior
	@Override
	public ByteBuffer toPhysicalDataRepresentation() {
		return intrinsicDataRepresentation;
	}

	@Override
	public <F> F toEntityPropertyValue(Class<F> entityPropertyValueType) {
		return BinaryUtils.fromBinary(entityPropertyValueType, intrinsicDataRepresentation);
	}

	@Override
	public com.amazonaws.services.dynamodbv2.model.AttributeValue toDynamoDBAttributeValue() {
		return new com.amazonaws.services.dynamodbv2.model.AttributeValue().withB(toPhysicalDataRepresentation());
	}

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
