package com.polymathiccoder.avempace.entity.domain;

import java.util.List;

import com.google.common.base.Optional;
import com.polymathiccoder.avempace.criteria.domain.Criterion;
import com.polymathiccoder.avempace.criteria.domain.Operation;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeSchema;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeValue;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeValueOperation;

public class EntityPropertyValueOperation extends Operation<Object> {
// Life cycle
	// Constructor
	private EntityPropertyValueOperation(final String entityPropertyName, final Operator operator) {
		super(entityPropertyName, operator);
	}

	// Factories
	public static EntityPropertyValueOperation add(final String entityPropertyName) {
		return new EntityPropertyValueOperation(entityPropertyName, Operator.ADD);
	}

	public static EntityPropertyValueOperation remove(final String entityPropertyName) {
		return new EntityPropertyValueOperation(entityPropertyName, Operator.REMOVE);
	}

	public static EntityPropertyValueOperation change(final String entityPropertyName) {
		return new EntityPropertyValueOperation(entityPropertyName, Operator.UPDATE);
	}

// Behavior
	@Override
	public String getOn() {
		return (String) on;
	}

	//@Override
	@SuppressWarnings("unchecked")
	public List<EntityPropertyValueCriterion> getCriteria() {
		return (List<EntityPropertyValueCriterion>) criteria;
	}

	@Override
	public EntityPropertyValueOperation withValue(final Object argument) {
		this.argument = Optional.of(argument);
		return this;
	}

	@Override
	public EntityPropertyValueOperation onlyIf(final Criterion<Object> criterion) {
		getCriteria().add((EntityPropertyValueCriterion) criterion);
		return this;
	}

	public AttributeValueOperation toAttributeValueOpetration(final AttributeSchema attributeSchema) {
		final AttributeValueOperation attributeValueOperation = new AttributeValueOperation(attributeSchema, operator);

		attributeValueOperation.setArgument(Optional.of(AttributeValue.fromEntityPropertyValue(attributeSchema, argument.get())));

		for (final EntityPropertyValueCriterion criterion : getCriteria()) {
			attributeValueOperation.getCriteria().add(criterion.toAttributeValueCriterion(attributeSchema));
		}

		return attributeValueOperation;
	}
}
