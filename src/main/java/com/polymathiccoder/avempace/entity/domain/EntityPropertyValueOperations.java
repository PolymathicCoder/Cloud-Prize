package com.polymathiccoder.avempace.entity.domain;

import java.util.Set;

import com.polymathiccoder.avempace.criteria.domain.Operation;
import com.polymathiccoder.avempace.criteria.domain.Operations;

public class EntityPropertyValueOperations extends Operations<Object> {
// Life cycle
	// Factories
	public static EntityPropertyValueOperations apply(final EntityPropertyValueOperation criterion) {
		final EntityPropertyValueOperations entityPropertyValueOperations = new EntityPropertyValueOperations();
		entityPropertyValueOperations.get().add(criterion);
		return entityPropertyValueOperations;
	}

	public static EntityPropertyValueOperations anything() {
		final EntityPropertyValueOperations entityPropertyValueCriteria = new EntityPropertyValueOperations();
		return entityPropertyValueCriteria;
	}

// Behavior
	@SuppressWarnings("unchecked")
	@Override
	public Set<EntityPropertyValueOperation> get() {
		return (Set<EntityPropertyValueOperation>) values;
	}

	@Override
	public EntityPropertyValueOperations and(final Operation<Object> operation) {
		get().add((EntityPropertyValueOperation) operation);
		return this;
	}
}
