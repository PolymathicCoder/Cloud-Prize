package com.polymathiccoder.avempace.persistence.service.ddl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.polymathiccoder.avempace.config.Region;
import com.polymathiccoder.avempace.persistence.domain.Table;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeSchema;
import com.polymathiccoder.avempace.persistence.domain.attribute.constraint.LocalSecondaryIndex;
import com.polymathiccoder.avempace.persistence.domain.operation.DDLOperation;
import com.polymathiccoder.avempace.persistence.domain.operation.ddl.CreateTable;
import com.polymathiccoder.avempace.persistence.domain.operation.ddl.DeleteTable;

public class DynamoDBDDLOperationsServiceImpl implements DynamoDBDDLOperationsService {
// Static fields
	private static final Logger LOGGER = LoggerFactory.getLogger("com.polymathiccoder.nimble");

// Fields
	private final Map<Region, AmazonDynamoDB> amazonDynamoDBsIndexedByRegion;

	private final Map<Region, AmazonDynamoDBAsync> amazonDynamoDBAsyncsIndexedByRegion;

// Life cycle
	public DynamoDBDDLOperationsServiceImpl(final Map<Region, AmazonDynamoDB> amazonDynamoDBsIndexedByRegion, final Map<Region, AmazonDynamoDBAsync> amazonDynamoDBAsyncsIndexedByRegion) {
		this.amazonDynamoDBsIndexedByRegion = amazonDynamoDBsIndexedByRegion;
		this.amazonDynamoDBAsyncsIndexedByRegion = amazonDynamoDBAsyncsIndexedByRegion;
	}

// Behavior
	@Override
	public void execute(final DDLOperation ddlOperation) {
		if (ddlOperation instanceof CreateTable) {
			createTable((CreateTable) ddlOperation);
		} else if (ddlOperation instanceof DeleteTable) {
			deleteTable((DeleteTable) ddlOperation);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public void batch(final List<? extends DDLOperation> ddlOperations) {
		BatchOperationExecutor.execute(this, ddlOperations.toArray(new DDLOperation[ddlOperations.size()]));
	}

	@Override
	public void createTable(final CreateTable createTable) {
		final AmazonDynamoDB amazonDynamoDB = amazonDynamoDBsIndexedByRegion.get(createTable.getTable().getRegion());

		final Table table = createTable.getTable();

		final CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(table.getDefinition().getName());

		// Provisioned throughput
		createTableRequest.setProvisionedThroughput(
				new ProvisionedThroughput()
						.withReadCapacityUnits(table.getDefinition().getReadCapacityUnits())
						.withWriteCapacityUnits(table.getDefinition().getWriteCapacityUnits()));

		// Attribute definitions
		final ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();

		// Key schema definition
		final AttributeSchema hashKeySchema = table.getDefinition().getHashKeySchema();
		attributeDefinitions.add(hashKeySchema.toDynamoDBAttributeDefinition());
		if (table.getDefinition().getRangeKeySchema().isPresent()) {
			final AttributeSchema rangeKey = table.getDefinition().getRangeKeySchema().get();
			attributeDefinitions.add(rangeKey.toDynamoDBAttributeDefinition());
		}

		// Index key schema definition
		final Iterable<AttributeSchema> indexedAttributes = table.getDefinition().getLocalSecondaryIndexes();
		for (final AttributeSchema attributeSchema : indexedAttributes) {
			attributeDefinitions.add(attributeSchema.toDynamoDBAttributeDefinition());
		}
		createTableRequest.setAttributeDefinitions(attributeDefinitions);

		// Key schema
		final ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<KeySchemaElement>();
		tableKeySchema.add(new KeySchemaElement().withAttributeName(hashKeySchema.getName().get()).withKeyType(KeyType.HASH));
		if (table.getDefinition().getRangeKeySchema().isPresent()) {
			final AttributeSchema rangeKey = table.getDefinition().getRangeKeySchema().get();
			tableKeySchema.add(new KeySchemaElement().withAttributeName(rangeKey.getName().get()).withKeyType(KeyType.RANGE));
		}

		createTableRequest.setKeySchema(tableKeySchema);

		// Indexes
		final ArrayList<com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex> localSecondaryIndexes = new ArrayList<>();
		for (final AttributeSchema attributeSchema : table.getDefinition().getLocalSecondaryIndexes()) {
			final LocalSecondaryIndex localSecondaryIndex = (LocalSecondaryIndex) attributeSchema.getConstraint();
			// Key schema
			final ArrayList<KeySchemaElement> indexKeySchema = new ArrayList<KeySchemaElement>();
			indexKeySchema.add(new KeySchemaElement().withAttributeName(hashKeySchema.getName().get()).withKeyType(KeyType.HASH));
			indexKeySchema.add(new KeySchemaElement().withAttributeName(attributeSchema.getName().get()).withKeyType(KeyType.RANGE));

			// Projection
			final Projection projection = new Projection().withProjectionType(ProjectionType.INCLUDE);
			final ArrayList<String> nonKeyAttributes = new ArrayList<String>();
			if (! localSecondaryIndex.getProjectedAttributes().isEmpty()) {
				nonKeyAttributes.addAll(localSecondaryIndex.getProjectedAttributes());
			} else {
				for (final AttributeSchema attribute : table.getDefinition().getAttributesSchemas()) {
					nonKeyAttributes.add(attribute.getName().get());
				}
			}
			projection.setNonKeyAttributes(nonKeyAttributes);

			// LSI
			localSecondaryIndexes.add(new com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex().withIndexName(localSecondaryIndex.getIndexName()).withKeySchema(indexKeySchema).withProjection(projection));
		}
		createTableRequest.setLocalSecondaryIndexes(localSecondaryIndexes);

		// Create
		try {
			amazonDynamoDB.createTable(createTableRequest);
		} catch (final AmazonClientException amazonClientException) {
			// TODO Handle better
			throw new RuntimeException(amazonClientException);
		}

		// TODO Add support for alarms

		TableStatusCheckerRetryableTask.retryUntil(amazonDynamoDB, table, new TableStatusCheckerRetryableTask.TableStatusIsActiveCondition());
	}

	@Override
	public void deleteTable(final DeleteTable deleteTable) {
		final AmazonDynamoDB amazonDynamoDB = amazonDynamoDBsIndexedByRegion.get(deleteTable.getTable().getRegion());

		final Table table = deleteTable.getTable();
		final DeleteTableRequest deleteTableRequest = new DeleteTableRequest().withTableName(table.getDefinition().getName());
		try {
			amazonDynamoDB.deleteTable(deleteTableRequest);
		} catch (final AmazonClientException amazonClientException) {
			// TODO Handle better
			throw new RuntimeException(amazonClientException);
		}

		TableStatusCheckerRetryableTask.retryUntil(amazonDynamoDB, table, new TableStatusCheckerRetryableTask.TableDoesNotExistCondition());
	}
}
