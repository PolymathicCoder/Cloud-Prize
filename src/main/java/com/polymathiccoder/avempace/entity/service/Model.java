package com.polymathiccoder.avempace.entity.service;
import javax.inject.Inject;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.polymathiccoder.avempace.mapping.Mapping;
import com.polymathiccoder.avempace.meta.model.MetaModel;
import com.polymathiccoder.avempace.meta.model.processing.MetaProcessor;


@AutoProperty
public class Model<T> {
// Fields
	@Inject
	static MetaProcessor META_PROCESSOR;

	private final Class<T> pojoType;
	private final Mapping<T> mapping;
	private final DistribbutionDefinition distribbutionDefinition;

// Life cycle
	// Constructors
	public Model(final Class<T> pojoType) {
		final MetaModel metaModel = META_PROCESSOR.lookup(pojoType);
		this.pojoType = pojoType;
		mapping = Mapping.create(metaModel);
		distribbutionDefinition = new DistribbutionDefinition(
				metaModel.getTypeMapping().getPrimaryRegion(),
				metaModel.getTypeMapping().getSecondaryRegions(),
				metaModel.getTypeMapping().isPropagatedAcrossAllRegions());
	}

// Accessors and mutators
	public Class<T> getPojoType() { return pojoType; }

	public Mapping<T> getMapping() { return mapping; }

	public DistribbutionDefinition getDistribbutionDefinition() { return distribbutionDefinition; }

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
