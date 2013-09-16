package com.polymathiccoder.avempace.meta.config;

import javax.inject.Singleton;

import com.polymathiccoder.avempace.entity.domain.Entity;
import com.polymathiccoder.avempace.entity.service.Model;
import com.polymathiccoder.avempace.meta.model.processing.MetaProcessor;
import com.polymathiccoder.avempace.persistence.domain.Tuple;

import dagger.Module;
import dagger.Provides;

@Module(
		complete = false,
		staticInjections = {
				Entity.class,
				Tuple.class,
				Model.class,
		}
)
public class MetaModule {
	@Provides @Singleton
	public MetaProcessor provideMetaProcessor() {
		MetaProcessor metaProcessor = new MetaProcessor();
		metaProcessor.discover();
		return metaProcessor;
	}
}
