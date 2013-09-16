package com.polymathiccoder.avempace.criteria.domain;

import static org.fest.reflect.core.Reflection.constructor;

import java.util.ArrayList;
import java.util.List;

public abstract class Condition<T> {
// Fields
	protected List<T> arguments;

	protected boolean isAffirnative;

// Life cycle
	// Constructors
	protected Condition() {
		arguments = new ArrayList<>();
		isAffirnative = true;
	}

	// Factories
	public static <T, C extends Condition<T>> Condition<T> create(final Class<C> conditionType, final Class<T> argumentType) {
		return (Condition<T>) constructor()
			.in(conditionType)
			.newInstance();
	}

// Accessors and mutators
	public List<T> getArguments() { return arguments; }

	public boolean isAffirnative() { return isAffirnative; }

	public void negate() {
		isAffirnative = false;
	}
}
