package com.polymathiccoder.avempace.criteria.domain;

import java.util.ArrayList;
import java.util.List;

public abstract class Criterion<T> {
// Fields
	protected final Object of;

	protected final List<Condition<? extends T>> criteria;

// Life cycle
	// Constructor
	protected Criterion(final Object of) {
		this.of = of;
		criteria = new ArrayList<>();
	}

// Behavior
	public abstract Object getOf();

	public abstract Criterion<T> is(final Condition<? extends T> condition);
	public abstract Criterion<T> does(final Condition<? extends T> condition);
	public abstract Criterion<T> and(final Condition<? extends T> condition);

// Accessors and mutators
	public List<Condition<? extends T>> getCriteria() { return criteria; }
}
