package com.polymathiccoder.avempace.entity.domain;

import java.util.Set;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.polymathiccoder.avempace.config.Region;

@AutoProperty
public class CrossRegionEntityCollection<T> extends EntityCollection<T> {
// Fields
	private final Set<Region> secondaryRegions;

// Life cycle
	private CrossRegionEntityCollection(final Builder<T> builder) {
		super(EntityCollection.Builder.create(builder.definition, builder.region));
		secondaryRegions = builder.secondaryRegions;
	}

// Types
	public static class Builder<T> {
	// Required
		private EntityCollectionDefinition<T> definition;
		private Region region;
		private Set<Region> secondaryRegions;

	// Life cycle
		private Builder() {
			defaults();
		}

	// Factories
		public static <T> Builder<T> create(final EntityCollectionDefinition<T> definition, final Region region, final Set<Region> secondaryRegions) {
			final Builder<T> builder = new Builder<>();
			builder.definition = definition;
			builder.region = region;
			builder.secondaryRegions = secondaryRegions;
			return builder;
		}

	// Defaults
		private void defaults() {
		}

	// Build
		public CrossRegionEntityCollection<T> build() {
			return new CrossRegionEntityCollection<>(this);
		}
	}

// Accessors and mutators
	public EntityCollectionDefinition<T> getDefinition() { return definition; }

	public Region getRegion() { return region; }

	public Set<Region> getSecondaryRegions() { return secondaryRegions; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
