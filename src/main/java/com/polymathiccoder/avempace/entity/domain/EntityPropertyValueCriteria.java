package com.polymathiccoder.avempace.entity.domain;

import java.util.Set;

import com.polymathiccoder.avempace.criteria.domain.Criteria;

public class EntityPropertyValueCriteria extends Criteria<Object> {
// Life cycle
	// Factories
	public static EntityPropertyValueCriteria matching(final EntityPropertyValueCriterion criterion) {
		final EntityPropertyValueCriteria entityPropertyValueCriteria = new EntityPropertyValueCriteria();
		entityPropertyValueCriteria.get().add(criterion);
		return entityPropertyValueCriteria;
	}

	public static EntityPropertyValueCriteria anything() {
		final EntityPropertyValueCriteria entityPropertyValueCriteria = new EntityPropertyValueCriteria();
		return entityPropertyValueCriteria;
	}

// Behavior
	@SuppressWarnings("unchecked")
	@Override
	public Set<EntityPropertyValueCriterion> get() {
		return (Set<EntityPropertyValueCriterion>) values;
	}

	public EntityPropertyValueCriteria and(final EntityPropertyValueCriterion criterion) {
		get().add(criterion);
		return this;
	}
}
