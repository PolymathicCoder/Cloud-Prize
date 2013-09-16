package com.polymathiccoder.avempace.persistence.domain.operation;

import org.pojomatic.annotations.AutoProperty;

import com.google.common.base.Optional;
import com.polymathiccoder.avempace.persistence.domain.attribute.Attribute;

@AutoProperty
public interface RequiringPrimaryKeyRange {
	Optional<? extends Attribute> getPrimaryKeyRangeAttribute();
}
