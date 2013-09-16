package com.polymathiccoder.avempace.persistence.domain;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.polymathiccoder.avempace.config.Region;

@AutoProperty
public class Table {
// Fields
	private final TableDefinition definition;

	private final Region region;

// Life cycle
	private Table(final Builder builder) {
		definition = builder.definition;
		region = builder.region;
	}

// Types
	public static class Builder {
	// Required
		private TableDefinition definition;
		private Region region;

	// Life cycle
		private Builder() {
			defaults();
		}

	// Factories
		public static Builder create(final TableDefinition definition, final Region region) {
			final Builder builder = new Builder();
			builder.definition = definition;
			builder.region = region;
			return builder;
		}

	// Defaults
		private void defaults() {
		}

	// Build
		public Table build() {
			return new Table(this);
		}
	}

// Accessors and mutators
	public TableDefinition getDefinition() { return definition; }

	public Region getRegion() { return region; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
