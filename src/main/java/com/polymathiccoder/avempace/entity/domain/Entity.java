package com.polymathiccoder.avempace.entity.domain;

import javax.inject.Inject;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.polymathiccoder.avempace.config.Region;
import com.polymathiccoder.avempace.mapping.Mapping;
import com.polymathiccoder.avempace.meta.model.MetaModel;
import com.polymathiccoder.avempace.meta.model.processing.MetaProcessor;

@AutoProperty
public class Entity<T> {
// Static fields
	@Inject
	static MetaProcessor META_PROCESSOR;

// Fields
	private final EntityCollection<T> belongsIn;

	private T pojo;

// Life cycle
	private Entity(final Builder<T> builder) {
		belongsIn = builder.belongsIn;
		pojo = builder.pojo;
	}

	// Factories
	public static <T> Entity<T> create(final T pojo, final Region region) {
		final MetaModel metaModel = META_PROCESSOR.lookup(pojo.getClass());
		final Mapping<T> mapping =  Mapping.create(metaModel);
		final EntityCollectionDefinition<T> entityCollectionDefinition = mapping.getEntityCollectionDefinition();

		final EntityCollection<T> belongsIn = EntityCollection.Builder
				.create(
						entityCollectionDefinition,
						region)
				.build();

		final Entity<T> entity = Entity.Builder.create(belongsIn, pojo).build();

		return entity;
	}

// Types
	public static class Builder<T> {
	// Required
		private EntityCollection<T> belongsIn;
		private T pojo;

	// Life cycle
		private Builder() {
			defaults();
		}

	// Factories
		public static <T> Builder<T> create(final EntityCollection<T> belongsIn, final T pojo) {
			final Builder<T> builder = new Builder<>();
			builder.belongsIn = belongsIn;
			builder.pojo = pojo;
			return builder;
		}

	// Defaults
		private void defaults() {
		}

	// Build
		public Entity<T> build() {
			return new Entity<>(this);
		}
	}

// Accessors and mutators
	public EntityCollection<T> getBelongsIn() { return belongsIn; }

	public T getPojo() { return pojo; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
