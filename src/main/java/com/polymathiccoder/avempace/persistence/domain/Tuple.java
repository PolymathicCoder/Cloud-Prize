package com.polymathiccoder.avempace.persistence.domain;

import static org.fest.reflect.core.Reflection.constructor;
import static org.fest.reflect.core.Reflection.field;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;
import org.reflections.ReflectionUtils;


import com.polymathiccoder.avempace.Employee;
import com.polymathiccoder.avempace.entity.domain.Entity;
import com.polymathiccoder.avempace.entity.domain.EntityCollection;
import com.polymathiccoder.avempace.entity.domain.EntityCollectionDefinition;
import com.polymathiccoder.avempace.entity.domain.PropertySchema;
import com.polymathiccoder.avempace.mapping.Mapping;
import com.polymathiccoder.avempace.mapping.SchemaMappingEntry;
import com.polymathiccoder.avempace.meta.model.processing.MetaProcessor;
import com.polymathiccoder.avempace.persistence.domain.attribute.Attribute;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeName;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeSchema;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeValue;

@AutoProperty
public class Tuple {
// Static fields
	@Inject
	//TODO Chnage this
	public static MetaProcessor META_PROCESSOR;

// Fields
	protected final Table belongsIn;

	// 1MB tops
	protected final Set<Attribute> attributes;

// life cycle
	public Tuple(final Table belongsIn, final Attribute... attributes) {
		this.belongsIn = belongsIn;
		this.attributes = new HashSet<>();
		this.attributes.addAll(Arrays.asList(attributes));
	}

	public Tuple(final Table belongsIn, final Set<Attribute> attributes) {
		this.belongsIn = belongsIn;
		this.attributes = attributes;
	}

	// Factories
	public static <T> Tuple create(final Entity<T> entity) {
		final EntityCollection<T> entityCollection = entity.getBelongsIn();
		final EntityCollectionDefinition<T> entityCollectionDefinition = entityCollection.getDefinition();
		final Mapping<T> mapping = Mapping.create(META_PROCESSOR.lookup(entityCollectionDefinition.getOfType()));
		final TableDefinition tableDefinition = mapping.getTableDefinition();

		final Set<Attribute> attributes = new HashSet<>();

		for (final SchemaMappingEntry schemaMappingEntry : mapping.getSchemaMappingEntries()) {
			final AttributeSchema attributeSchema = schemaMappingEntry.getAttributeSchema();
			final PropertySchema propertySchema = schemaMappingEntry.getPropertySchema();

			final Object propertyValue = field(propertySchema.getName().get())
					.ofType(propertySchema.getType().get())
					.in(entity.getPojo())
					.get();

			attributes.add(new Attribute(
					attributeSchema,
					AttributeValue.fromEntityPropertyValue(attributeSchema, propertyValue)));
		}

		return new Tuple(
				Table.Builder
						.create(
								tableDefinition,
								entityCollection.getRegion())
								.build(),
						attributes);
	}

// Behavior
	public <T> Entity<T> toEntity(final Class<T> ofType) {
		final Mapping<T> mapping =  Mapping.create(META_PROCESSOR.lookup(ofType));
		final EntityCollectionDefinition<T> entityCollectionDefinition = mapping.getEntityCollectionDefinition();

		//TODO When this fails say that a constructor is needed
		final T pojo = constructor()
				.in(ofType)
				.newInstance();

		for (final Attribute attribute : attributes) {
			final AttributeName attributeName = attribute.getSchema().getName();
			final AttributeValue attributeValue = attribute.getValue();

			try {
				final SchemaMappingEntry schemaMappingEntry = mapping.lookupByAttributeName(attributeName.get());
				final PropertySchema propertySchema = schemaMappingEntry.getPropertySchema();

				Field field = ofType.getDeclaredField(propertySchema.getName().get());
				field.setAccessible(true);
//FIXME Abdel
				//field.set(pojo, attributeValue.get().toPojo(propertySchema.getType().get()));

				field.set(pojo, attributeValue.get().toPojo(field.getGenericType()));
			} catch (final NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException exception) {
				//TODO Handle better
			}
			//ClassUtils.primitiveToWrapper(field.getType()).cast(value)
		}

		EntityCollection<T> entityCollection = EntityCollection.Builder.create(entityCollectionDefinition, belongsIn.getRegion()).build();

		final Entity<T> entity = Entity.Builder
				.create(entityCollection, pojo)
				.build();

		return entity;
	}

// Accessors and mutators
	public Table getBelongsIn() { return belongsIn; }

	public Set<Attribute> getAttributes() { return attributes; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
