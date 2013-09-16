package com.polymathiccoder.avempace.criteria.domain;

import java.util.HashSet;
import java.util.Set;

public abstract class Operations<T> {
// Fields
	protected final Set<? extends Operation<T>> values;

// Life cycle
	// Constructor
	protected Operations() {
		values = new HashSet<>();
	}

	// Factories
	public static <T> Operations<T> apply(final Operation<T> operation) {
		throw new UnsupportedOperationException();
	}

	public static <T> Operations<? extends T> anything() {
		throw new UnsupportedOperationException();
	}

// Behavior
	public abstract Set<? extends Operation<T>> get();

	public abstract Operations<T> and(final Operation<T> operation);
}
