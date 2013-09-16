package com.polymathiccoder.avempace.criteria.domain;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.polymathiccoder.avempace.entity.domain.EntityPropertyValueOperation;

public abstract class Operation<T> {
// Fields
	protected final Object on;

	protected final Operator operator;

	protected Optional<T> argument;

	protected final List<? extends Criterion<T>> criteria;

// Life cycle
	// Constructor
	protected Operation(final Object on, final Operator operator) {
		this.on = on;
		this.operator = operator;
		argument = null;
		criteria = new ArrayList<>();
	}

	public static EntityPropertyValueOperation add(final String entityPropertyName) {
		throw new UnsupportedOperationException();
	}

	public static EntityPropertyValueOperation remove(final String entityPropertyName) {
		throw new UnsupportedOperationException();
	}

	public static EntityPropertyValueOperation change(final String entityPropertyName) {
		throw new UnsupportedOperationException();
	}

// Behavior
	public abstract Object getOn();

	public abstract List<? extends Criterion<T>> getCriteria();

	public abstract Operation<T> withValue(final T argument);

	public abstract Operation<T> onlyIf(final Criterion<T> criterion);

// Types
	public enum Operator {
		ADD,
		REMOVE,
		UPDATE
	}

// Accessors and mutators
	public Operator getOperator() { return operator; }

	public Optional<T> getArgument() { return argument; }
	public void setArgument(final Optional<T> argument) { this.argument = argument; }

}
