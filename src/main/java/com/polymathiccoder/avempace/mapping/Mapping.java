package com.polymathiccoder.avempace.mapping;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashSet;
import java.util.Set;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.google.common.base.Optional;
import com.polymathiccoder.avempace.entity.domain.EntityCollectionDefinition;
import com.polymathiccoder.avempace.entity.domain.PropertyName;
import com.polymathiccoder.avempace.entity.domain.PropertySchema;
import com.polymathiccoder.avempace.entity.domain.PropertyType;
import com.polymathiccoder.avempace.meta.model.FieldMapping;
import com.polymathiccoder.avempace.meta.model.MetaModel;
import com.polymathiccoder.avempace.meta.model.TypeMapping;
import com.polymathiccoder.avempace.persistence.domain.TableDefinition;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeSchema;
import com.polymathiccoder.avempace.persistence.domain.attribute.constraint.AttributeConstraint;
import com.polymathiccoder.avempace.persistence.domain.attribute.constraint.LocalSecondaryIndex;
import com.polymathiccoder.avempace.persistence.domain.attribute.constraint.PrimaryHashKey;
import com.polymathiccoder.avempace.persistence.domain.attribute.constraint.PrimaryRangeKey;
import com.polymathiccoder.avempace.persistence.domain.attribute.constraint.AttributeConstraint.AttributeConstraintType;

@AutoProperty
public class Mapping<T> {
// Fields
	private final EntityCollectionDefinition<T> entityCollectionDefinition;
	private final TableDefinition tableDefinition;
	private final Set<SchemaMappingEntry> schemaMappingEntries;

// Life cycle
	private Mapping(final EntityCollectionDefinition<T> entityCollectionDefinition, final TableDefinition tableDefinition, final Set<SchemaMappingEntry> schemaMappingEntries) {
		this.entityCollectionDefinition = entityCollectionDefinition;
		this.tableDefinition = tableDefinition;
		this.schemaMappingEntries = schemaMappingEntries;
	}

	// Factories
	public static <T> Mapping<T> create(final MetaModel metaModel) {
		final Set<AttributeSchema> attributesSchemas = new HashSet<>();
		final Set<PropertySchema> propertiesSchemas = new HashSet<>();
		final Set<SchemaMappingEntry> entries = new HashSet<>();

		final TypeMapping typeMapping = metaModel.getTypeMapping();
		for (final FieldMapping fieldMapping : metaModel.getFieldMappings()) {
			final AttributeSchema attributeSchema = constructAttributeSchema(fieldMapping);
			final PropertySchema propertySchema = constructPropertySchema(fieldMapping);

			attributesSchemas.add(attributeSchema);
			propertiesSchemas.add(propertySchema);

			entries.add(new SchemaMappingEntry(propertySchema, attributeSchema));
		}

		// Entity mapping
		@SuppressWarnings("unchecked")
		final EntityCollectionDefinition<T> entityCollectionDefinition = EntityCollectionDefinition.Builder
				.create(
						(Class<T>) typeMapping.getAnnotatedType(),
						typeMapping.getPrimaryRegion(),
						propertiesSchemas)
				.build();

		// Table mapping
		final TableDefinition tableDefinition = TableDefinition.Builder
				.create(
						typeMapping.getTableName(),
						attributesSchemas)
				.withReadCapacityUnits(typeMapping.getReadCapacityUnits())
				.withReadCapacityUnits(typeMapping.getWriteCapacityUnits())
				.build();

		return new Mapping<>(entityCollectionDefinition, tableDefinition, entries);
	}

	// Helpers
	private static PropertySchema constructPropertySchema(final FieldMapping fieldMapping) {
		final PropertySchema propertySchema = new PropertySchema(
				new PropertyName(fieldMapping.getAnnotatedField().getName()),
				new PropertyType(fieldMapping.getAnnotatedField().getType()));
		return propertySchema;
	}

	private static AttributeSchema constructAttributeSchema(final FieldMapping fieldMapping) {
		Optional<? extends AttributeConstraint> attributeConstraint = inferAttributeConstraint(fieldMapping);

		final AttributeSchema attributeSchema = AttributeSchema.create(
				fieldMapping.getAttributeName(),
				fieldMapping.getPersistAsType().getPersistentValueType(),
				attributeConstraint.isPresent() ? attributeConstraint.get() : null);
		return attributeSchema;
	}

	private static Optional<? extends AttributeConstraint> inferAttributeConstraint(final FieldMapping fieldMapping) {
		Optional<? extends AttributeConstraint> attributeConstraint = Optional.absent();

		if (fieldMapping.getConstraintType() == AttributeConstraintType.LOCAL_SECONDARY_INDEX_KEY) {
			attributeConstraint = Optional.of(LocalSecondaryIndex.create(fieldMapping.getIndexName().get(), fieldMapping.getIndexProjectedAttributes().get()));
		} else if (fieldMapping.getConstraintType() == AttributeConstraintType.PRIMARY_HASH_KEY) {
			attributeConstraint = Optional.of(PrimaryHashKey.create());
		} else if (fieldMapping.getConstraintType() == AttributeConstraintType.PRIMARY_RANGE_KEY) {
			attributeConstraint = Optional.of(PrimaryRangeKey.create());
		}

		return attributeConstraint;
	}

// Behavior
	public SchemaMappingEntry lookupByPropertySchema(final PropertySchema propertySchema) {
		return selectFirst(schemaMappingEntries, having(on(SchemaMappingEntry.class).getPropertySchema(), equalTo(propertySchema)));
	}

	public SchemaMappingEntry lookupByAttributeSchema(final AttributeSchema attributeSchema) {
		return selectFirst(schemaMappingEntries, having(on(SchemaMappingEntry.class).getAttributeSchema(), equalTo(attributeSchema)));
	}

	public SchemaMappingEntry lookupByAttributeName(final String attributeName) {
		return selectFirst(schemaMappingEntries, having(on(SchemaMappingEntry.class).getAttributeSchema().getName().get(), equalTo(attributeName)));
	}

	public SchemaMappingEntry lookupByPropertyName(final String propertyName) {
		return selectFirst(schemaMappingEntries, having(on(SchemaMappingEntry.class).getPropertySchema().getName().get(), equalTo(propertyName)));
	}

// Accessors and mutators
	public EntityCollectionDefinition<T> getEntityCollectionDefinition() { return entityCollectionDefinition; }

	public TableDefinition getTableDefinition() { return tableDefinition; }

	public Set<SchemaMappingEntry> getSchemaMappingEntries() { return schemaMappingEntries; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
