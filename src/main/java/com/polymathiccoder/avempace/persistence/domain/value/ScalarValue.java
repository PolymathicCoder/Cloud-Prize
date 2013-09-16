package com.polymathiccoder.avempace.persistence.domain.value;

import java.lang.reflect.Type;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public abstract class ScalarValue<INTRINSIC, PHYSICAL> extends PersistentValue {
// Fields
	protected final INTRINSIC intrinsicDataRepresentation;

// Life cycle
	public ScalarValue(final INTRINSIC intrinsicDataRepresentation) {
		this.intrinsicDataRepresentation = intrinsicDataRepresentation;
	}

// Behavior
	public abstract PHYSICAL toPhysicalDataRepresentation();
	public abstract <F> F toEntityPropertyValue(Class<F> entityPropertyValueType);

	public INTRINSIC get() {
		return intrinsicDataRepresentation;
	}

	@Override
	public Object toPojo(final Type type) {
		return toEntityPropertyValue((Class<?>) type);
	}

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
