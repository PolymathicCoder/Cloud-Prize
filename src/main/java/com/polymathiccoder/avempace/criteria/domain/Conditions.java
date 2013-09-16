package com.polymathiccoder.avempace.criteria.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

public final class Conditions {
// Static behavior
	// Negation
	public static <T> Condition<T> not(Condition<T> condition) {
		condition.isAffirnative = false;
		return condition;
	}

	// Conditions
	public static <T> Condition<T> beginsWith(final T argument) {
		final Condition<T> condition = new BeginsWith<T>(argument);
		return condition;
	}

	public static <T> Condition<T> between(final T argument1, final T argument2) {
		final Condition<T> condition = new IsBetween<>(Pair.of(argument1, argument2));
		return condition;
	}

	public static <T> Condition<T> contains(final T argument) {
		final Condition<T> condition = new Contains<T>(argument);
		return condition;
	}

	public static <T> Condition<T> equalTo(final T argument) {
		final Condition<T> condition = new IsEqualTo<T>(argument);
		return condition;
	}

	public static <T> Condition<T> greaterThanOrEqualTo(final T argument) {
		final Condition<T> condition = new IsGreaterThanOrEqualTo<T>(argument);
		return condition;
	}

	public static <T> Condition<T> greaterThan(final T argument) {
		final Condition<T> condition = new IsGreaterThan<T>(argument);
		return condition;
	}

	@SuppressWarnings("unchecked")
	public static <T> Condition<T> in(final T... arguments) {
		final Set<T> argumentSet = new HashSet<>(Arrays.asList(arguments));
		final Condition<T> condition = new IsIn<>(argumentSet);
		return condition;
	}

	public static <T> Condition<T> lessThan(final T argument) {
		final Condition<T> condition = new IsLessThan<T>(argument);
		return condition;
	}

	public static <T> Condition<T> lessThanOrEqualTo(final T argument) {
		final Condition<T> condition = new IsLessThanOrEqualTo<T>(argument);
		return condition;
	}

	public static <T> Condition<T> isNull() {
		final Condition<T> condition = new IsNull<T>();
		return condition;
	}

// Types
	// Condition classes
	public static class BeginsWith<T> extends Condition<T> {
		public BeginsWith() {
		}

		public BeginsWith(final T argument) {
			arguments.add(argument);
		}
	}

	public static class IsBetween<T> extends Condition<T> {
		public IsBetween() {
		}

		public IsBetween(final Pair<T, T> argumentPair) {
			arguments.add(argumentPair.getLeft());
			arguments.add(argumentPair.getRight());
		}
	}

	public static class Contains<T> extends Condition<T> {
		public Contains() {
		}

		public Contains(final T argument) {
			arguments.add(argument);
		}
	}

	public static class IsEqualTo<T> extends Condition<T> {
		public IsEqualTo() {
		}

		public IsEqualTo(final T argument) {
			arguments.add(argument);
		}
	}

	public static class IsGreaterThan<T> extends Condition<T> {
		public IsGreaterThan() {
		}

		public IsGreaterThan(final T argument) {
			arguments.add(argument);
		}
	}

	public static class IsGreaterThanOrEqualTo<T> extends Condition<T> {
		public IsGreaterThanOrEqualTo() {
		}

		public IsGreaterThanOrEqualTo(final T argument) {
			arguments.add(argument);
		}
	}

	public static class IsIn<T> extends Condition<T> {
		public IsIn() {
		}

		public IsIn(final Set<T> argumentSet) {
			arguments.addAll(argumentSet);
		}
	}

	public static class IsLessThan<T> extends Condition<T> {
		public IsLessThan() {
		}

		public IsLessThan(final T argument) {
			arguments.add(argument);
		}
	}

	public static class IsLessThanOrEqualTo<T> extends Condition<T> {
		public IsLessThanOrEqualTo() {
		}

		public IsLessThanOrEqualTo(final T argument) {
			arguments.add(argument);
		}
	}

	public static class IsNull<T> extends Condition<T> {
	}
}
