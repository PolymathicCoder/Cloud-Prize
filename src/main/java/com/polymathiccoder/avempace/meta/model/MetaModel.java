package com.polymathiccoder.avempace.meta.model;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.polymathiccoder.avempace.meta.model.validation.MetaModelValidationException;

@AutoProperty
public final class MetaModel {
// Field
	private TypeMapping typeMapping;
	private Set<FieldMapping> fieldMappings;

// Life cycle
	private MetaModel() {
		this.fieldMappings = new HashSet<>();
	}

	// Factories
	public static MetaModel create(Class<?> typeAnnotatedWithDynamoDBEntity) {
		final MetaModel metaModel = new MetaModel();

		metaModel.typeMapping = TypeMapping.generateForType(typeAnnotatedWithDynamoDBEntity);
		for (final Field field : typeAnnotatedWithDynamoDBEntity.getDeclaredFields()) {
			metaModel.fieldMappings.add(FieldMapping.generateForField(field));
		}

		// Validate
		try {
			validate(metaModel);
		} catch (MetaModelValidationException metaModelValidationException) {
			//TODO Handle better
			throw new IllegalStateException();
		}

		return metaModel;
	}

	private static void validate(final MetaModel metaModel) throws MetaModelValidationException {
		//TODO Implement
	}

// Accessors and mutators
	public TypeMapping getTypeMapping() { return typeMapping; }

	public Set<FieldMapping> getFieldMappings() { return fieldMappings; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
