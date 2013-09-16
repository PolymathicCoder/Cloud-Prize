package com.polymathiccoder.avempace.persistence.domain.attribute;

import java.util.Set;

import com.polymathiccoder.avempace.criteria.domain.Criteria;

public class AttributeValueCriteria extends Criteria<AttributeValue> {
// Life cycle
	// Factories
	public static AttributeValueCriteria criteria(final AttributeValueCriterion criterion) {
		final AttributeValueCriteria attributeValueCriteria = new AttributeValueCriteria();
		attributeValueCriteria.get().add(criterion);
		return attributeValueCriteria;
	}

// Behavior
	@SuppressWarnings("unchecked")
	@Override
	public Set<AttributeValueCriterion> get() {
		return (Set<AttributeValueCriterion>) values;
	}

	public AttributeValueCriteria and(final AttributeValueCriterion criterion) {
		get().add(criterion);
		return this;
	}
}
