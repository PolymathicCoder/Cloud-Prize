package com.polymathiccoder.avempace.persistence.domain.value;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class StringSetValue extends SetValue<StringValue, String, String> {
// Life cycle
	public StringSetValue(Set<String> intrinsicDataRepresentations) {
		super(intrinsicDataRepresentations, String.class, StringValue.class);
	}

// Static behavior
	public static PersistentValue fromDynamoDBAttributeValue(final com.amazonaws.services.dynamodbv2.model.AttributeValue dynamoDBAttributeValue) {
		return StringSetValue.fromPhysicalDataRepresentations(new HashSet<>(dynamoDBAttributeValue.getSS()));
	}

	private static StringSetValue fromPhysicalDataRepresentations(final Set<String> physicalDataRepresentations) {
		final Set<String> intrinsicValues = new HashSet<>();
		for (final String  physicalDataRepresentation : physicalDataRepresentations) {
			intrinsicValues.add(StringValue.toIntrinsicDataRepresentation(physicalDataRepresentation));
		}
		return new StringSetValue(intrinsicValues);
	}

	@SuppressWarnings("unchecked")
	public static PersistentValue fromEntityPropertyValue(final Object entityPropertyValue) {
		if (ClassUtils.isAssignable(entityPropertyValue.getClass(), Collection.class)) {
			return new StringSetValue(new HashSet<String>((Collection<String>) entityPropertyValue));
		} else {
			throw new IllegalArgumentException();
		}
	}

// Behavior
	@Override
	public com.amazonaws.services.dynamodbv2.model.AttributeValue toDynamoDBAttributeValue() {
		return new com.amazonaws.services.dynamodbv2.model.AttributeValue().withSS(toPhysicalDataRepresentations());
	}

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
