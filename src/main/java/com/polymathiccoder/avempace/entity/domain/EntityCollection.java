package com.polymathiccoder.avempace.entity.domain;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.polymathiccoder.avempace.config.Region;

@AutoProperty
public class EntityCollection<T> {
// Fields
	protected final EntityCollectionDefinition<T> definition;

	protected final Region region;
// Life cycle
	protected EntityCollection(final Builder<T> builder) {
		definition = builder.definition;
		region = builder.region;
	}

// Types
	public static class Builder<T> {
	// Required
		private EntityCollectionDefinition<T> definition;
		private Region region;

	// Life cycle
		private Builder() {
			defaults();
		}

	// Factories
		public static <T> Builder<T> create(final EntityCollectionDefinition<T> definition, final Region region) {
			final Builder<T> builder = new Builder<>();
			builder.definition = definition;
			builder.region = region;
			return builder;
		}

	// Defaults
		private void defaults() {
		}

	// Build
		public EntityCollection<T> build() {
			return new EntityCollection<>(this);
		}
	}

// Accessors and mutators
	public EntityCollectionDefinition<T> getDefinition() { return definition; }

	public Region getRegion() { return region; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
