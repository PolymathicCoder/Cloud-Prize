package com.polymathiccoder.avempace.meta.annotation;

import com.polymathiccoder.avempace.persistence.domain.value.BinarySetValue;
import com.polymathiccoder.avempace.persistence.domain.value.BinaryValue;
import com.polymathiccoder.avempace.persistence.domain.value.NumberSetValue;
import com.polymathiccoder.avempace.persistence.domain.value.NumberValue;
import com.polymathiccoder.avempace.persistence.domain.value.PersistentValue;
import com.polymathiccoder.avempace.persistence.domain.value.StringSetValue;
import com.polymathiccoder.avempace.persistence.domain.value.StringValue;

public enum PersistAsType {
// Static fields
	BINARY_SET(BinarySetValue.class),
	BINARY(BinaryValue.class),
	NUMBER_SET(NumberSetValue.class),
	NUMBER(NumberValue.class),
	STRING_SET(StringSetValue.class),
	STRING(StringValue.class);

// Fields
	private Class<? extends PersistentValue> persistentValueType;

// Life cycle
	private PersistAsType(final Class<? extends PersistentValue> persistentValueType) {
		this.persistentValueType = persistentValueType;
	}

// Accessors and mutators
	public Class<? extends PersistentValue> getPersistentValueType() { return persistentValueType; }
}