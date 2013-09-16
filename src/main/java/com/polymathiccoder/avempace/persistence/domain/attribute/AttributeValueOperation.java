package com.polymathiccoder.avempace.persistence.domain.attribute;

import java.util.List;

import com.google.common.base.Optional;
import com.polymathiccoder.avempace.criteria.domain.Criterion;
import com.polymathiccoder.avempace.criteria.domain.Operation;

public class AttributeValueOperation extends Operation<AttributeValue> {
// Life cycle
	// Constructor
	public AttributeValueOperation(final AttributeSchema attributeSchema, final Operator operator) {
		super(attributeSchema, operator);
	}

	// Factories
	public static AttributeValueOperation add(final AttributeSchema attributeSchema) {
		return new AttributeValueOperation(attributeSchema, Operator.ADD);
	}

	public static AttributeValueOperation remove(final AttributeSchema attributeSchema) {
		return new AttributeValueOperation(attributeSchema, Operator.REMOVE);
	}

	public static AttributeValueOperation change(final AttributeSchema attributeSchema) {
		return new AttributeValueOperation(attributeSchema, Operator.UPDATE);
	}

// Behavior
	@Override
	public AttributeSchema getOn() {
		return (AttributeSchema) on;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AttributeValueCriterion> getCriteria() {
		return (List<AttributeValueCriterion>) criteria;
	}

	@Override
	public AttributeValueOperation withValue(final AttributeValue argument) {
		this.argument = Optional.of(argument);
		return this;
	}

	@Override
	public AttributeValueOperation onlyIf(Criterion<AttributeValue> criterion) {
		getCriteria().add((AttributeValueCriterion) criterion);
		return this;
	}

	public com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate toDynamoDBAttributeValueUpdate() {
		final com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate attributeValueUpdate = new com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate();

		switch (operator) {
			case ADD: {
				attributeValueUpdate.withAction(com.amazonaws.services.dynamodbv2.model.AttributeAction.ADD);
				break;
			} case REMOVE: {
				attributeValueUpdate.withAction(com.amazonaws.services.dynamodbv2.model.AttributeAction.DELETE);
				break;
			} case UPDATE: {
				attributeValueUpdate.withAction(com.amazonaws.services.dynamodbv2.model.AttributeAction.PUT);
				break;
			} default: {
				// TODO Handle better
				throw new RuntimeException();
			}
		}

		attributeValueUpdate.withValue(argument.get().get().toDynamoDBAttributeValue());

		return attributeValueUpdate;
	}
}