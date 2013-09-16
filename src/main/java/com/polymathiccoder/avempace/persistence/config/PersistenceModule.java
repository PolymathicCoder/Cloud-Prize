package com.polymathiccoder.avempace.persistence.config;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.michelboudreau.alternator.AlternatorDB;
import com.michelboudreau.alternatorv2.AlternatorDBClientV2;
import com.polymathiccoder.avempace.config.AvempaceConfiguration;
import com.polymathiccoder.avempace.config.Region;
import com.polymathiccoder.avempace.persistence.service.ddl.DynamoDBDDLOperationsService;
import com.polymathiccoder.avempace.persistence.service.ddl.DynamoDBDDLOperationsServiceImpl;
import com.polymathiccoder.avempace.persistence.service.dml.DynamoDBDMLOperationsService;
import com.polymathiccoder.avempace.persistence.service.dml.DynamoDBDMLOperationsServiceImpl;

import dagger.Module;
import dagger.Provides;

@Module(
		complete = false,
		injects = {
				DynamoDBDDLOperationsService.class,
				DynamoDBDMLOperationsService.class
		}
)
public class PersistenceModule {
	@Provides @Singleton
	public Map<Region, AmazonDynamoDB> provideAmazonDynamoDBsIndexedByRegion(final AvempaceConfiguration nimbleConfiguration) {
		final Map<Region, AmazonDynamoDB> amazonDynamoDBsIndexedByRegion = new HashMap<>();
		for (final Region region : Region.values()) {
			final String accessKey = nimbleConfiguration.getAccessKey();
			final String secretKey = nimbleConfiguration.getSecretKey();

			final AmazonDynamoDB amazonDynamoDB;
			if (accessKey.isEmpty() || secretKey.isEmpty()) {
				amazonDynamoDB = new AlternatorDBClientV2();
		        amazonDynamoDB.setEndpoint("http://localhost:9090");
			} else {
				final BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		        amazonDynamoDB = new AmazonDynamoDBClient(credentials);
		        amazonDynamoDB.setEndpoint(region.getEndpoint());
			}

	        amazonDynamoDBsIndexedByRegion.put(region, amazonDynamoDB);
		}
		return amazonDynamoDBsIndexedByRegion;
    }

	@Provides @Singleton
	public Map<Region, AmazonDynamoDBAsync> provideAmazonDynamoDBAsyncsIndexedByRegion(final AvempaceConfiguration nimbleConfiguration) {
		final Map<Region, AmazonDynamoDBAsync> amazonDynamoDBAsyncsIndexedByRegion = new HashMap<>();
		for (final Region region : Region.values()) {
			final String accessKey = nimbleConfiguration.getAccessKey();
			final String secretKey = nimbleConfiguration.getSecretKey();

			final AmazonDynamoDBAsync amazonDynamoDBAsync;
			if (accessKey.isEmpty() || secretKey.isEmpty()) {
				//FIXME
				amazonDynamoDBAsync = new AmazonDynamoDBAsyncClient(new BasicAWSCredentials("AKIAIQOONXLTVCMQWXZA", "Gi9Ip0hUHiRvfh06rLvS3lsKj28q1PGaqzhJOB2E"));
			} else {
				final BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
				amazonDynamoDBAsync = new AmazonDynamoDBAsyncClient(credentials);
			}

	        amazonDynamoDBAsync.setEndpoint(region.getEndpoint());
	        amazonDynamoDBAsyncsIndexedByRegion.put(region, amazonDynamoDBAsync);
		}
		return amazonDynamoDBAsyncsIndexedByRegion;
    }

	@Provides @Singleton
	public DynamoDBDMLOperationsService provideAynamoDBDMLOperationsService(final Map<Region, AmazonDynamoDB> amazonDynamoDBsIndexedByRegion, final Map<Region, AmazonDynamoDBAsync> amazonDynamoDBAsyncsIndexedByRegion) {
		return new DynamoDBDMLOperationsServiceImpl(amazonDynamoDBsIndexedByRegion, amazonDynamoDBAsyncsIndexedByRegion);
	}

	@Provides @Singleton
	public DynamoDBDDLOperationsService dynamoDBDDLOperationsService(final Map<Region, AmazonDynamoDB> amazonDynamoDBsIndexedByRegion, final Map<Region, AmazonDynamoDBAsync> amazonDynamoDBAsyncsIndexedByRegion) {
		return new DynamoDBDDLOperationsServiceImpl(amazonDynamoDBsIndexedByRegion, amazonDynamoDBAsyncsIndexedByRegion);
	}
}
