package com.polymathiccoder.avempace.persistence.domain.operation;

import java.util.List;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class OperationValidationResult {
// Static fields
	public static final String MISSING_REQUIRED_PARAMETER_VALUE = "Persistence: The attribute named '%s' must be specified";
	public static final String INVALID_PARAMETER_VALUE = "Persistence: The attribute named '%s' has an invalid value";

// Fields
	private final Operation operation;
	private final Verdict verdict;

// Life cycle
	// Constructors
	public OperationValidationResult(final Operation operation, final Verdict verdict) {
		this.operation = operation;
		this.verdict = verdict;
	}

	// Factories
	public static OperationValidationResult create(final Operation operation, final List<String> errors) {
		return new OperationValidationResult(
				operation,
				new Verdict(errors.isEmpty(), errors));
	}

// Types
	public static class Verdict {
	// Fields
		private final boolean isValid;
		private final List<String> errors;

	// Life cycle
		// Constructors
		public Verdict(final boolean isValid, final List<String> errors) {
			this.isValid = isValid;
			this.errors = errors;
		}

	// Accessors and mutators
		public boolean isValid() { return isValid; }

		public List<String> getErrors() { return errors; }

	// Common methods
		@Override
		public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
		@Override
		public int hashCode() { return Pojomatic.hashCode(this); }
		@Override
		public String toString() { return Pojomatic.toString(this); }
	}

// Accessors and mutators
	public Operation getOperation() { return operation; }

	public Verdict getVerdict() { return verdict; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
