package com.polymathiccoder.avempace.entity.domain;

import com.polymathiccoder.avempace.criteria.domain.Condition;
import com.polymathiccoder.avempace.criteria.domain.Criterion;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeSchema;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeValue;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeValueCriterion;

public class EntityPropertyValueCriterion extends Criterion<Object> {
// Life cycle
	// Constructor
	private EntityPropertyValueCriterion(final String entityPropertyName) {
		super(entityPropertyName);
	}

	// Factories
	public static EntityPropertyValueCriterion $(final String entityPropertyName) {
		return new EntityPropertyValueCriterion(entityPropertyName);
	}

// Behavior
	@Override
	public String getOf() {
		return (String) of;
	}

	@Override
	public EntityPropertyValueCriterion is(final Condition<? extends Object> condition) {
		criteria.add(condition);
		return this;
	}

	@Override
	public EntityPropertyValueCriterion does(final Condition<? extends Object> condition) {
		criteria.add(condition);
		return this;
	}

	@Override
	public EntityPropertyValueCriterion and(final Condition<? extends Object> condition) {
		criteria.add(condition);
		return this;
	}

	public AttributeValueCriterion toAttributeValueCriterion(final AttributeSchema attributeSchema) {
		final AttributeValueCriterion attributeValueCriterion = AttributeValueCriterion.$(attributeSchema);

		for (final Condition<? extends Object> entityPropertyCriteria : criteria) {
			@SuppressWarnings("unchecked")
			final Condition<AttributeValue> attributeCondition = Condition.create(
					entityPropertyCriteria.getClass(),
					AttributeValue.class);

			for (final Object entityConditionArgument : entityPropertyCriteria.getArguments()) {
				attributeCondition.getArguments().add(AttributeValue.fromEntityPropertyValue(attributeSchema, entityConditionArgument));
			}
			if(! entityPropertyCriteria.isAffirnative()) {
				attributeCondition.negate();
			}

			attributeValueCriterion.getCriteria().add(attributeCondition);
		}

		return attributeValueCriterion;
	}
}
