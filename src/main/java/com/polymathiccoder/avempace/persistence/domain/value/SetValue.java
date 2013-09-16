package com.polymathiccoder.avempace.persistence.domain.value;

import static org.fest.reflect.core.Reflection.constructor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public abstract class SetValue<SCALAR extends ScalarValue<INTRINSIC, PERSISTENT>, INTRINSIC, PERSISTENT> extends PersistentValue {
// Fields
	protected final Set<SCALAR> scalarValues;

// Life cycle
	protected SetValue(final Set<INTRINSIC> intrinsicDataRepresentations, final Class<INTRINSIC> intrinsicDataType, Class<SCALAR> scalarType) {
		scalarValues = new HashSet<>();
		for (final INTRINSIC value : intrinsicDataRepresentations) {
			scalarValues.add(constructor()
					.withParameterTypes(intrinsicDataType)
					.in(scalarType)
					.newInstance(value));
		}
	}

// Behavior
	public Set<PERSISTENT> toPhysicalDataRepresentations() {
		final Set<PERSISTENT> physicalDataRepresentations = new HashSet<>();

		for (final SCALAR scalarValue : scalarValues) {
			physicalDataRepresentations.add(scalarValue.toPhysicalDataRepresentation());
		}

		return physicalDataRepresentations;
	}

	public <F> Collection<F> toPojoValues(final Class<? extends Collection<?>> collectionType, final Class<F> elementsType) {
		Collection<F> entityPropertyValues;
		if (ClassUtils.isAssignable(collectionType, List.class)) {
			entityPropertyValues = new ArrayList<>();
		} else if (ClassUtils.isAssignable(collectionType, Set.class)) {
			entityPropertyValues = new HashSet<>();
		} else {
			throw new RuntimeException();
		}

		for (final SCALAR scalarValue : scalarValues) {
			entityPropertyValues.add(scalarValue.toEntityPropertyValue(elementsType));
		}

		return entityPropertyValues;
	}

	@Override
	public Object toPojo(final Type type) {
		if (TypeUtils.isAssignable(type, Collection.class)) {
			final ParameterizedType parameterizedType = (ParameterizedType) type;
			final Class<?> elementsType = (Class<?>) TypeUtils.getTypeArguments(parameterizedType).entrySet().iterator().next().getValue();
			@SuppressWarnings("unchecked")
			final Class<? extends Collection<?>> collectionType = (Class<? extends Collection<?>>) parameterizedType.getRawType();
			return toPojoValues(collectionType, elementsType);
		} else {
			throw new RuntimeException();
		}
	}

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
