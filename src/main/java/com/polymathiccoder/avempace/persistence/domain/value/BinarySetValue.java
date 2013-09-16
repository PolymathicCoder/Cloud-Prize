package com.polymathiccoder.avempace.persistence.domain.value;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class BinarySetValue extends SetValue<BinaryValue, ByteBuffer, ByteBuffer> {
// Life cycle
	public BinarySetValue(Set<ByteBuffer> intrinsicDataRepresentations) {
		super(intrinsicDataRepresentations, ByteBuffer.class, BinaryValue.class);
	}

// Static behavior
	public static PersistentValue fromDynamoDBAttributeValue(final com.amazonaws.services.dynamodbv2.model.AttributeValue dynamoDBAttributeValue) {
		return BinarySetValue.fromPhysicalDataRepresentations(new HashSet<>(dynamoDBAttributeValue.getBS()));
	}

	private static BinarySetValue fromPhysicalDataRepresentations(final Set<ByteBuffer> physicalDataRepresentations) {
		Set<ByteBuffer> intrinsicValues = new HashSet<>();
		for (final ByteBuffer  physicalDataRepresentation : physicalDataRepresentations) {
			intrinsicValues.add(BinaryValue.toIntrinsicDataRepresentation(physicalDataRepresentation));
		}
		return new BinarySetValue(intrinsicValues);
	}

	@SuppressWarnings("unchecked")
	public static PersistentValue fromEntityPropertyValue(final Object entityPropertyValue) {
		if (ClassUtils.isAssignable(entityPropertyValue.getClass(), Collection.class)) {
			return new BinarySetValue(new HashSet<ByteBuffer>((Collection<ByteBuffer>) entityPropertyValue));
		} else {
			throw new IllegalArgumentException();
		}
	}

// Behavior
	@Override
	public com.amazonaws.services.dynamodbv2.model.AttributeValue toDynamoDBAttributeValue() {
		return new com.amazonaws.services.dynamodbv2.model.AttributeValue().withBS(toPhysicalDataRepresentations());
	}

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
