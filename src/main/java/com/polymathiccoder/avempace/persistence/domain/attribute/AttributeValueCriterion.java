package com.polymathiccoder.avempace.persistence.domain.attribute;

import java.util.ArrayList;
import java.util.List;

import com.polymathiccoder.avempace.criteria.domain.Condition;
import com.polymathiccoder.avempace.criteria.domain.Criterion;
import com.polymathiccoder.avempace.criteria.domain.Conditions.BeginsWith;
import com.polymathiccoder.avempace.criteria.domain.Conditions.Contains;
import com.polymathiccoder.avempace.criteria.domain.Conditions.IsBetween;
import com.polymathiccoder.avempace.criteria.domain.Conditions.IsEqualTo;
import com.polymathiccoder.avempace.criteria.domain.Conditions.IsGreaterThan;
import com.polymathiccoder.avempace.criteria.domain.Conditions.IsGreaterThanOrEqualTo;
import com.polymathiccoder.avempace.criteria.domain.Conditions.IsIn;
import com.polymathiccoder.avempace.criteria.domain.Conditions.IsLessThan;
import com.polymathiccoder.avempace.criteria.domain.Conditions.IsLessThanOrEqualTo;
import com.polymathiccoder.avempace.criteria.domain.Conditions.IsNull;

public class AttributeValueCriterion extends Criterion<AttributeValue> {
// Life cycle
	// Constructor
	private AttributeValueCriterion(final AttributeSchema attributeSchemaToBeFiltered) {
		super(attributeSchemaToBeFiltered);
	}

	// Factories
	public static AttributeValueCriterion $(final AttributeSchema attributeSchemaToBeFiltered) {
		return new AttributeValueCriterion(attributeSchemaToBeFiltered);
	}

// Behavior
	@Override
	public AttributeSchema getOf() {
		return (AttributeSchema) of;
	}

	@Override
	public AttributeValueCriterion is(final Condition<? extends AttributeValue> condition) {
		criteria.add(condition);
		return this;
	}

	@Override
	public AttributeValueCriterion does(final Condition<? extends AttributeValue> condition) {
		criteria.add(condition);
		return this;
	}

	@Override
	public AttributeValueCriterion and(final Condition<? extends AttributeValue> condition) {
		criteria.add(condition);
		return this;
	}

	public com.amazonaws.services.dynamodbv2.model.Condition toDynamoDBCondition() {
		final com.amazonaws.services.dynamodbv2.model.Condition dynamoDBCondition = new com.amazonaws.services.dynamodbv2.model.Condition();

		for (final Condition<? extends AttributeValue> condition : getCriteria()) {
			if (condition instanceof IsEqualTo && condition.isAffirnative()) {
				dynamoDBCondition.withComparisonOperator(com.amazonaws.services.dynamodbv2.model.ComparisonOperator.EQ);
			} else if (condition instanceof IsEqualTo &&
					! condition.isAffirnative()) {
				dynamoDBCondition.withComparisonOperator(com.amazonaws.services.dynamodbv2.model.ComparisonOperator.NE);
			} else if (condition instanceof IsLessThanOrEqualTo &&
					condition.isAffirnative()) {
				dynamoDBCondition.withComparisonOperator(com.amazonaws.services.dynamodbv2.model.ComparisonOperator.LE);
			} else if (condition instanceof IsLessThanOrEqualTo &&
					! condition.isAffirnative()) {
				dynamoDBCondition.withComparisonOperator(com.amazonaws.services.dynamodbv2.model.ComparisonOperator.GT);
			} else if (condition instanceof IsLessThan &&
					condition.isAffirnative()) {
				dynamoDBCondition.withComparisonOperator(com.amazonaws.services.dynamodbv2.model.ComparisonOperator.LT);
			} else if (condition instanceof IsLessThan &&
					! condition.isAffirnative()) {
				dynamoDBCondition.withComparisonOperator(com.amazonaws.services.dynamodbv2.model.ComparisonOperator.GE);
			} else if (condition instanceof IsGreaterThanOrEqualTo &&
					condition.isAffirnative()) {
				dynamoDBCondition.withComparisonOperator(com.amazonaws.services.dynamodbv2.model.ComparisonOperator.GE);
			} else if (condition instanceof IsGreaterThanOrEqualTo &&
					! condition.isAffirnative()) {
				dynamoDBCondition.withComparisonOperator(com.amazonaws.services.dynamodbv2.model.ComparisonOperator.LT);
			} else if (condition instanceof IsGreaterThan &&
					condition.isAffirnative()) {
				dynamoDBCondition.withComparisonOperator(com.amazonaws.services.dynamodbv2.model.ComparisonOperator.GT);
			} else if (condition instanceof IsGreaterThan &&
					! condition.isAffirnative()) {
				dynamoDBCondition.withComparisonOperator(com.amazonaws.services.dynamodbv2.model.ComparisonOperator.LE);
			} else if (condition instanceof IsNull &&
					condition.isAffirnative()) {
				dynamoDBCondition.withComparisonOperator(com.amazonaws.services.dynamodbv2.model.ComparisonOperator.NULL);
			} else if (condition instanceof IsNull &&
					! condition.isAffirnative()) {
				dynamoDBCondition.withComparisonOperator(com.amazonaws.services.dynamodbv2.model.ComparisonOperator.NOT_NULL);
			} else if (condition instanceof Contains &&
					condition.isAffirnative()) {
				dynamoDBCondition.withComparisonOperator(com.amazonaws.services.dynamodbv2.model.ComparisonOperator.CONTAINS);
			} else if (condition instanceof Contains &&
					! condition.isAffirnative()) {
				dynamoDBCondition.withComparisonOperator(com.amazonaws.services.dynamodbv2.model.ComparisonOperator.NOT_CONTAINS);
			} else if (condition instanceof BeginsWith &&
					condition.isAffirnative()) {
				dynamoDBCondition.withComparisonOperator(com.amazonaws.services.dynamodbv2.model.ComparisonOperator.BEGINS_WITH);
			} else if (condition instanceof BeginsWith &&
					! condition.isAffirnative()) {
				throw new RuntimeException("Not supported!");
			} else if (condition instanceof IsIn &&
					condition.isAffirnative()) {
				dynamoDBCondition.withComparisonOperator(com.amazonaws.services.dynamodbv2.model.ComparisonOperator.IN);
			} else if (condition instanceof IsIn &&
					! condition.isAffirnative()) {
				throw new RuntimeException("Not supported!");
			} else if (condition instanceof IsBetween &&
					condition.isAffirnative()) {
				dynamoDBCondition.withComparisonOperator(com.amazonaws.services.dynamodbv2.model.ComparisonOperator.BETWEEN);
			} else if (condition instanceof IsBetween &&
					! condition.isAffirnative()) {
				throw new RuntimeException("Not supported!");
			}

			final List<com.amazonaws.services.dynamodbv2.model.AttributeValue> dynamoDBAttributeValueList = new ArrayList<>();
			for (final AttributeValue argument : condition.getArguments()) {
				dynamoDBAttributeValueList.add(argument.get().toDynamoDBAttributeValue());
			}
			dynamoDBCondition.withAttributeValueList(dynamoDBAttributeValueList);
		}
		return dynamoDBCondition;
	}
}
