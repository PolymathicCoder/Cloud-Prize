package com.polymathiccoder.avempace.persistence.domain.attribute.constraint;

import java.util.HashSet;
import java.util.Set;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class LocalSecondaryIndex extends AttributeConstraint {
	private final String indexName;

	private final Set<String> projectedAttributes;

// Life cycle
	// Constructors
	private LocalSecondaryIndex(final String indexName, final Set<String> projectedAttributes) {
		super(AttributeConstraintType.LOCAL_SECONDARY_INDEX_KEY);
		this.indexName = indexName;
		this.projectedAttributes = projectedAttributes;
	}

	// Factories
	public static LocalSecondaryIndex create(final String indexName) {
		return new LocalSecondaryIndex(indexName, new HashSet<String>());
	}

	public static LocalSecondaryIndex create(final String indexName, final Set<String> projectedAttributes) {
		return new LocalSecondaryIndex(indexName, projectedAttributes);
	}

	// Accessors and mutators
	public String getIndexName() { return indexName; }

	public Set<String> getProjectedAttributes() { return projectedAttributes; }

	// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
