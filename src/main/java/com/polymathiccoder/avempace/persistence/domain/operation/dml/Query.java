package com.polymathiccoder.avempace.persistence.domain.operation.dml;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashSet;
import java.util.Set;

import com.polymathiccoder.avempace.persistence.domain.Table;
import com.polymathiccoder.avempace.persistence.domain.attribute.Attribute;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeSchema;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeValueCriterion;
import com.polymathiccoder.avempace.persistence.domain.attribute.constraint.AttributeConstraint.AttributeConstraintType;
import com.polymathiccoder.avempace.persistence.domain.operation.DMLOperation;
import com.polymathiccoder.avempace.persistence.domain.operation.RequiringPrimaryKeyHash;

public abstract class Query extends DMLOperation implements RequiringPrimaryKeyHash {
// Static fields
	protected static final int DEFAULT_ITEM_LIMIT = 10;

// Fields
	// Must be specified hash at least
	protected Set<AttributeValueCriterion> conditions;

	protected Set<AttributeSchema> includedAttributesSchemas;

	protected boolean isConsistentlyRead;

	protected int limit;

// Life cycle
	public Query(final Table table, final Set<AttributeValueCriterion> conditions, final Set<AttributeSchema> includedAttributesSchemas, final boolean isConsistentlyRead, final int limit) {
		super(table);
		this.conditions = conditions;
		this.includedAttributesSchemas = includedAttributesSchemas;
		this.isConsistentlyRead = isConsistentlyRead;
		this.limit = limit;
	}

// Behavior
	@Override
	public Attribute getPrimaryKeyHashAttribute() {
		final AttributeValueCriterion attributeValueCriterion = selectFirst(
				conditions,
				having(
						on(AttributeValueCriterion.class).getOf().getConstraint().getType(),
						equalTo(AttributeConstraintType.PRIMARY_HASH_KEY)));

		return new Attribute(
				attributeValueCriterion.getOf(),
				attributeValueCriterion.getCriteria().iterator().next().getArguments().iterator().next());
	}

	public AttributeValueCriterion getHashKeyCondition() {
		return selectFirst(
				conditions,
				having(
						on(AttributeValueCriterion.class).getOf().getConstraint().getType(),
						equalTo(AttributeConstraintType.PRIMARY_HASH_KEY)));
	}

	public Set<AttributeValueCriterion> getRangeKeyConditions() {
		final Set<AttributeValueCriterion> rangeKeyConditions = new HashSet<>();
		rangeKeyConditions.addAll(
				select(
						conditions,
						having(
								on(AttributeValueCriterion.class).getOf().getConstraint().getType(),
								equalTo(AttributeConstraintType.PRIMARY_RANGE_KEY)))
		);
		return rangeKeyConditions;
	}

// Accessors and mutators
	public Set<AttributeValueCriterion> getConditions() { return conditions; }

	public Set<AttributeSchema> getIncludedAttributesSchemas() { return includedAttributesSchemas; }

	public boolean isConsistentlyRead() { return isConsistentlyRead; }

	public int getLimit() {return limit; }
}
