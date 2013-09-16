package com.polymathiccoder.avempace.meta.model;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.polymathiccoder.avempace.config.Region;
import com.polymathiccoder.avempace.meta.annotation.Entity;
import com.polymathiccoder.avempace.meta.annotation.Table;
import com.polymathiccoder.avempace.meta.model.validation.MetaModelValidationException;

@AutoProperty
public final class TypeMapping {
// Fields
	private final Class<?> annotatedType;

	private Region primaryRegion;
	private Set<Region> secondaryRegions;
	private boolean propagatedAcrossAllRegions;

	private String tableName;
	private long readCapacityUnits;
	private long writeCapacityUnits;

// Life cycle
	private TypeMapping(final Class<?> annotatedType) {
		this.annotatedType = annotatedType;
	}

	// Factories
	public static TypeMapping generateForType(final Class<?> type) {
		TypeMapping typeMapping = new TypeMapping(type);

		// Construct
		for (final Annotation annotation : type.getAnnotations()) {
			processTableAnnotation(typeMapping, annotation);
			processEntityAnnotation(typeMapping, annotation);
		}

		// Validate
		try {
			validate(typeMapping);
		} catch (MetaModelValidationException metaModelValidationException) {
			//TODO Handle better
			throw new IllegalStateException();
		}

		return typeMapping;
	}

	// Helpers
	private static boolean processTableAnnotation(final TypeMapping typeMapping, final Annotation annotation) {
		boolean processed = false;
		if (annotation instanceof Table) {
			final Table tableAnnotation = (Table) annotation;

			typeMapping.tableName = tableAnnotation.name();
			typeMapping.readCapacityUnits = tableAnnotation.readCapacityUnits();
			typeMapping.writeCapacityUnits = tableAnnotation.writeCapacityUnits();

			processed = true;
		}
		return processed;
	}

	private static boolean processEntityAnnotation(final TypeMapping typeMapping, final Annotation annotation) {
		boolean processed = false;
		if (annotation instanceof Entity) {
			final Entity entityAnnotation = (Entity) annotation;

			typeMapping.primaryRegion = entityAnnotation.primaryRegion();
			typeMapping.secondaryRegions = new HashSet<>(Arrays.asList(entityAnnotation.secondaryRegions()));
			typeMapping.propagatedAcrossAllRegions = entityAnnotation.propagatedAcrossAllRegions();

			processed = true;
		}
		return processed;
	}

	private static void validate(final TypeMapping typeMapping) throws MetaModelValidationException {
		//TODO Implement
	}

	// Accessors and mutators
	public Class<?> getAnnotatedType() { return annotatedType; }

	public Region getPrimaryRegion() { return primaryRegion; }

	public Set<Region> getSecondaryRegions() { return secondaryRegions; }

	public boolean isPropagatedAcrossAllRegions() { return propagatedAcrossAllRegions; }

	public String getTableName() { return tableName; }

	public long getReadCapacityUnits() { return readCapacityUnits; }

	public long getWriteCapacityUnits() { return writeCapacityUnits; }

	// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}