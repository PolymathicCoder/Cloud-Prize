package com.polymathiccoder.avempace.meta.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.google.common.base.Optional;
import com.polymathiccoder.avempace.meta.annotation.Attribute;
import com.polymathiccoder.avempace.meta.annotation.PersistAsType;
import com.polymathiccoder.avempace.meta.annotation.constraint.LSI;
import com.polymathiccoder.avempace.meta.annotation.constraint.PrimaryHashKey;
import com.polymathiccoder.avempace.meta.annotation.constraint.PrimaryRangeKey;
import com.polymathiccoder.avempace.meta.model.validation.MetaModelValidationException;
import com.polymathiccoder.avempace.persistence.domain.attribute.constraint.AttributeConstraint.AttributeConstraintType;

@AutoProperty
public class FieldMapping {
	// Statics
	private static final String DEFAULT_INDEX_SUFFIX = "-index";

	// Fields
	private final Field annotatedField;

	private String attributeName;
	private PersistAsType persistAsType;
	private AttributeConstraintType constraintType;
	private Optional<String> indexName;
	private Optional<Set<String>> indexProjectedAttributes;

	// Life cycle
	private FieldMapping(final Field annotatedField) {
		this.annotatedField = annotatedField;
		constraintType = AttributeConstraintType.NONE;
		indexName = Optional.absent();
		indexProjectedAttributes = Optional.absent();
	}

	// Behavior
	public static FieldMapping generateForField(final Field field) {
		FieldMapping fieldMapping = new FieldMapping(field);

		// Construct
		for (final Annotation annotation : fieldMapping.annotatedField.getAnnotations()) {
			processPrimaryHashKeyAnnotation(fieldMapping, annotation);
			processPrimaryRangeKeyAnnotation(fieldMapping, annotation);
			processLSIAnnotation(fieldMapping, annotation);
			processAttributeAnnotation(fieldMapping, annotation);
		}

		// Validate
		try {
			validate(fieldMapping);
		} catch (MetaModelValidationException metaModelValidationException) {
			//TODO Handle better
			throw new IllegalStateException();
		}

		return fieldMapping;
	}

	// Helpers
	private static boolean processPrimaryHashKeyAnnotation(final FieldMapping fieldMapping, final Annotation annotation) {
		boolean processed = false;
		if (annotation instanceof PrimaryHashKey) {
			final PrimaryHashKey primaryHashKeyAnnotation = (PrimaryHashKey) annotation;
			fieldMapping.attributeName = primaryHashKeyAnnotation.attributeName();
			if (StringUtils.isEmpty(primaryHashKeyAnnotation.attributeName())) {
				fieldMapping.attributeName = fieldMapping.annotatedField.getName();
			}
			fieldMapping.constraintType = AttributeConstraintType.PRIMARY_HASH_KEY;
			fieldMapping.persistAsType = primaryHashKeyAnnotation.persistAsType();
			processed = true;
		}
		return processed;
	}

	private static boolean processPrimaryRangeKeyAnnotation(final FieldMapping fieldMapping, final Annotation annotation) {
		boolean processed = false;
		if (annotation instanceof PrimaryRangeKey) {
			final PrimaryRangeKey primaryRangeKeyAnnotation = (PrimaryRangeKey) annotation;
			fieldMapping.attributeName = primaryRangeKeyAnnotation.attributeName();
			if (StringUtils.isEmpty(primaryRangeKeyAnnotation.attributeName())) {
				fieldMapping.attributeName = fieldMapping.annotatedField.getName();
			}
			fieldMapping.constraintType = AttributeConstraintType.PRIMARY_RANGE_KEY;
			fieldMapping.persistAsType = primaryRangeKeyAnnotation.persistAsType();
			processed = true;
		}
		return processed;
	}

	private static boolean processLSIAnnotation(final FieldMapping fieldMapping, final Annotation annotation) {
		boolean processed = false;
		if (annotation instanceof LSI) {
			final LSI lsiAnnotation = (LSI) annotation;
			fieldMapping.attributeName = lsiAnnotation.attributeName();
			if (StringUtils.isEmpty(lsiAnnotation.attributeName())) {
				fieldMapping.attributeName = fieldMapping.annotatedField.getName();
			}
			fieldMapping.constraintType = AttributeConstraintType.LOCAL_SECONDARY_INDEX_KEY;
			fieldMapping.persistAsType = lsiAnnotation.persistAsType();
			if (! StringUtils.isEmpty(lsiAnnotation.indexName())) {
				fieldMapping.indexName = Optional.of(fieldMapping.annotatedField.getName() + DEFAULT_INDEX_SUFFIX);
			} else {
				fieldMapping.indexName = Optional.of(lsiAnnotation.indexName());
			}
			fieldMapping.indexProjectedAttributes = Optional.of((Set<String>) new HashSet<>(Arrays.asList(lsiAnnotation.projectedAttributes())));

			processed = true;
		}
		return processed;
	}

	private static boolean processAttributeAnnotation(final FieldMapping fieldMapping, final Annotation annotation) {
		boolean processed = false;
		if (annotation instanceof Attribute) {
			final Attribute attributeAnnotation = (Attribute) annotation;
			fieldMapping.attributeName = attributeAnnotation.name();
			if (StringUtils.isEmpty(attributeAnnotation.name())) {
				fieldMapping.attributeName = fieldMapping.annotatedField.getName();
			}
			fieldMapping.persistAsType = attributeAnnotation.persistAsType();
			processed = true;
		}
		return processed;
	}

	private static void validate(final FieldMapping fieldMapping) throws MetaModelValidationException {
		//TODO Implement
	}

	// Accessors and mutators
	public Field getAnnotatedField() { return annotatedField; }

	public String getAttributeName() { return attributeName; }

	public PersistAsType getPersistAsType() { return persistAsType; }

	public AttributeConstraintType getConstraintType() { return constraintType; }

	public Optional<String> getIndexName() { return indexName; }

	public Optional<Set<String>> getIndexProjectedAttributes() { return indexProjectedAttributes; }

	// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
