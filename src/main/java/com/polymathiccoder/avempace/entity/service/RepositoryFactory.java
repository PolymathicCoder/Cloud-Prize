package com.polymathiccoder.avempace.entity.service;

import javax.inject.Inject;

import com.polymathiccoder.avempace.config.AvempaceConfiguration;
import com.polymathiccoder.avempace.config.AvempaceConfiguration.SchemaGenerationStrategy;
import com.polymathiccoder.avempace.persistence.service.ddl.DynamoDBDDLOperationsService;
import com.polymathiccoder.avempace.persistence.service.dml.DynamoDBDMLOperationsService;

public final class RepositoryFactory {
// Fields
	@Inject
	DynamoDBDMLOperationsService dynamoDBDMLOperationsService;

	@Inject
	DynamoDBDDLOperationsService dynamoDBDDLOperationsService;

	@Inject
	AvempaceConfiguration avempaceConfiguration;

// Static behavior
	public <T> Repository<T> createRepository(final Class<T> pojoType) {
		final Model<T> model = new Model<>(pojoType);
		final RepositoryImpl<T> repositoryImpl = new RepositoryImpl<>(dynamoDBDDLOperationsService, dynamoDBDMLOperationsService, model);

		if (avempaceConfiguration.getSchemaGenerationStrategy() == SchemaGenerationStrategy.CLEAN_SLATE) {
			repositoryImpl.removeAll();
		}

		return CrossRegionRepository.newInstance(repositoryImpl);
	}
}
