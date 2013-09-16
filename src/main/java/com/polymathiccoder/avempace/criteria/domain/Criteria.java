package com.polymathiccoder.avempace.criteria.domain;

import java.util.HashSet;
import java.util.Set;

public abstract class Criteria<T> {
// Fields
	protected final Set<? extends Criterion<T>> values;

// Life cycle
	// Constructor
	protected Criteria() {
		values = new HashSet<>();
	}

// Behavior
	public abstract Set<? extends Criterion<T>> get();
}
