package com.polymathiccoder.avempace.config;

import javax.inject.Singleton;

import com.polymathiccoder.avempace.config.AvempaceConfiguration.SchemaGenerationStrategy;
import com.polymathiccoder.avempace.entity.service.RepositoryFactory;
import com.polymathiccoder.avempace.meta.config.MetaModule;
import com.polymathiccoder.avempace.persistence.config.PersistenceModule;

import dagger.Module;
import dagger.Provides;

@Module(
		includes = {
				MetaModule.class,
				PersistenceModule.class
		},
		injects = {
			RepositoryFactory.class
		}
)
public class AvempaceModule {
	@Provides @Singleton
	public AvempaceConfiguration provideAvempaceConfiguration() {
		return new AvempaceConfiguration(
				"",
				"",
				//StringUtils.EMPTY, StringUtils.EMPTY,
				SchemaGenerationStrategy.CLEAN_SLATE);
	}
}
