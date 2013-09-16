package com.polymathiccoder.avempace.persistence.domain.value;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class NumberSetValue extends SetValue<NumberValue, Number, String> {
// Life cycle
	public NumberSetValue(final Set<Number> intrinsicDataRepresentations) {
		super(intrinsicDataRepresentations, Number.class, NumberValue.class);
	}

// Static behavior
	public static PersistentValue fromDynamoDBAttributeValue(final com.amazonaws.services.dynamodbv2.model.AttributeValue dynamoDBAttributeValue) {
		return NumberSetValue.fromPhysicalDataRepresentations(new HashSet<>(dynamoDBAttributeValue.getNS()));
	}

	private static NumberSetValue fromPhysicalDataRepresentations(final Set<String> physicalDataRepresentations) {
		final Set<Number> intrinsicValues = new HashSet<>();
		for (final String  physicalDataRepresentation : physicalDataRepresentations) {
			intrinsicValues.add(NumberValue.toIntrinsicDataRepresentation(physicalDataRepresentation));
		}
		return new NumberSetValue(intrinsicValues);
	}

	@SuppressWarnings("unchecked")
	public static PersistentValue fromEntityPropertyValue(final Object entityPropertyValue) {
		if (ClassUtils.isAssignable(entityPropertyValue.getClass(), Collection.class)) {
			return new NumberSetValue(new HashSet<Number>((Collection<Number>) entityPropertyValue));
		} else {
			throw new IllegalArgumentException();
		}
	}

// Behavior
	@Override
	public com.amazonaws.services.dynamodbv2.model.AttributeValue toDynamoDBAttributeValue() {
		return new com.amazonaws.services.dynamodbv2.model.AttributeValue().withNS(toPhysicalDataRepresentations());
	}


// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
