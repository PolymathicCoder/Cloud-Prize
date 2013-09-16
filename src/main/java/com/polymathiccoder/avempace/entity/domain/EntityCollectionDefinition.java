package com.polymathiccoder.avempace.entity.domain;

import java.util.Set;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.polymathiccoder.avempace.config.Region;

@AutoProperty
public class EntityCollectionDefinition<T> {
// Fields
	private final Class<T> ofType;

	private final Set<PropertySchema> propertiesSchemas;

// Life cycle
	protected EntityCollectionDefinition(final Builder<T> builder) {
		ofType = builder.ofType;
		propertiesSchemas = builder.propertiesSchemas;
	}

// Types
	public static class Builder<T> {
	// Fields
		// Required
		private Class<T> ofType;
		private Set<PropertySchema> propertiesSchemas;

	// Life cycle
		private Builder() {
			defaults();
		}

		// Defaults
		private void defaults() {
		}

	// Factories
		public static <T> Builder<T> create(final Class<T> ofType, final Region primaryRegion, final Set<PropertySchema> propertiesSchemas) {
			final Builder<T> builder = new Builder<T>();
			builder.ofType = ofType;
			builder.propertiesSchemas = propertiesSchemas;
			return builder;
		}

	// Build
		public EntityCollectionDefinition<T> build() {
			return new EntityCollectionDefinition<>(this);
		}
	}

// Accessors and mutators
	public Class<T> getOfType() { return ofType; }

	public Set<PropertySchema> getPropertiesSchemas() { return propertiesSchemas; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
