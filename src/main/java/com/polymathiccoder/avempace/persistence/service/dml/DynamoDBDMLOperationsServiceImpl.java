package com.polymathiccoder.avempace.persistence.service.dml;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.BatchGetItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchGetItemResult;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.amazonaws.services.dynamodbv2.model.DeleteRequest;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.KeysAndAttributes;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.polymathiccoder.avempace.config.Region;
import com.polymathiccoder.avempace.persistence.domain.Table;
import com.polymathiccoder.avempace.persistence.domain.Tuple;
import com.polymathiccoder.avempace.persistence.domain.VersionedTuple;
import com.polymathiccoder.avempace.persistence.domain.attribute.Attribute;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeSchema;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeValueCriterion;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeValueOperation;
import com.polymathiccoder.avempace.persistence.domain.operation.BatchableWrite;
import com.polymathiccoder.avempace.persistence.domain.operation.DMLOperation;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.Delete;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.Get;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.Put;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.Query;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.QueryByLocalSecondaryIndex;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.Scan;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.Update;

public class DynamoDBDMLOperationsServiceImpl implements DynamoDBDMLOperationsService {
// Static fields
	private static final Logger LOGGER = Logger.getLogger("com.polymathiccoder.nimble");

// Fields
	private final Map<Region, AmazonDynamoDB> amazonDynamoDBsIndexedByRegion;

	private final Map<Region, AmazonDynamoDBAsync> amazonDynamoDBAsyncsIndexedByRegion;

// Life cycle
	public DynamoDBDMLOperationsServiceImpl(final Map<Region, AmazonDynamoDB> amazonDynamoDBsIndexedByRegion, final Map<Region, AmazonDynamoDBAsync> amazonDynamoDBAsyncsIndexedByRegion) {
		this.amazonDynamoDBsIndexedByRegion = amazonDynamoDBsIndexedByRegion;
		this.amazonDynamoDBAsyncsIndexedByRegion = amazonDynamoDBAsyncsIndexedByRegion;
	}

// Behavior
	@Override
	public void put(final Put put) {
		final AmazonDynamoDB amazonDynamoDB = amazonDynamoDBsIndexedByRegion.get(put.getTable().getRegion());

		final Table table = put.getTable();
		final Tuple item = put.getTuple();

		Map<String, AttributeValue> dynamoDBItem = new HashMap<String, AttributeValue>();
		for (final Attribute attribute : item.getAttributes()) {
			dynamoDBItem.put(
					attribute.getSchema().getName().get(),
					attribute.getValue().get().toDynamoDBAttributeValue());
		}

		Map<String, ExpectedAttributeValue> expected = new HashMap<String, ExpectedAttributeValue>();
		for (final Attribute expectedAttribute : put.getExpectedAttributes()) {
			expected.put(expectedAttribute.getSchema().getName().get(),
					new ExpectedAttributeValue().withValue(expectedAttribute.getValue().get().toDynamoDBAttributeValue()));
		}

		if (item instanceof VersionedTuple) {
			final Attribute versionAttribute = ((VersionedTuple) item).getVersion();
			expected.put(versionAttribute.getSchema().getName().get(),
					new ExpectedAttributeValue().withValue(versionAttribute.getValue().get().toDynamoDBAttributeValue()));
		}

		// TODO Do sth with
		ReturnValue returnValue = ReturnValue.NONE;

		PutItemRequest putItemRequest = new PutItemRequest()
			.withTableName(table.getDefinition().getName())
			.withItem(dynamoDBItem)
			.withExpected(expected)
			.withReturnValues(returnValue);

		// TODO Do sth with this
		@SuppressWarnings("unused")
		PutItemResult putItemResult = amazonDynamoDB.putItem(putItemRequest);
	}

	@Override
	public Tuple get(final Get get) {
		final AmazonDynamoDB amazonDynamoDB = amazonDynamoDBsIndexedByRegion.get(get.getTable().getRegion());

		final Table table = get.getTable();
		// Construct the key
		final HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();

		final Attribute hashKeyAttribute = get.getPrimaryKeyHashAttribute();
		key.put(hashKeyAttribute.getSchema().getName().get(), hashKeyAttribute.getValue().get().toDynamoDBAttributeValue());

		if (get.getPrimaryKeyRangeAttribute().isPresent()) {
			final Attribute rangeKeyAttribute = get.getPrimaryKeyRangeAttribute().get();
			key.put(rangeKeyAttribute.getSchema().getName().get(), rangeKeyAttribute.getValue().get().toDynamoDBAttributeValue());
		}

		// Prepare the request
		GetItemRequest getItemRequest = new GetItemRequest()
		    .withTableName(table.getDefinition().getName())
		    .withKey(key)
		    .withConsistentRead(get.isConsistentlyRead());

		// Include all attributes unless specified
		final Set<AttributeSchema> includedAttributesSchemas = get.getNonConstrainedAttributesSchemas();
		if (get.getVersionAttributeSchema().isPresent()) {
			includedAttributesSchemas.add(get.getVersionAttributeSchema().get());
		}
		if (! includedAttributesSchemas.isEmpty()) {
			String[] includedAttributesNames = extract(table.getDefinition().getAttributesSchemas(), on(AttributeSchema.class).getName().get()).toArray(new String[includedAttributesSchemas.size()]);
			getItemRequest = getItemRequest.withAttributesToGet(Arrays.asList(includedAttributesNames));
		}

		// Fire
		GetItemResult getItemResult = amazonDynamoDB.getItem(getItemRequest);

		//TODO if null throw notfound

		// Process results
		Map<String, AttributeValue> dynamoDBItem = getItemResult.getItem();
		final Set<Attribute> attributes = new HashSet<>();
		for (final AttributeSchema attributeSchema : table.getDefinition().getAttributesSchemas()) {
			if (dynamoDBItem.containsKey(attributeSchema.getName().get())) {
				final Attribute attribute = new Attribute(
						attributeSchema,
						com.polymathiccoder.avempace.persistence.domain.attribute.AttributeValue.fromDynamoDBAttributeValue(
								attributeSchema,
								dynamoDBItem.get(attributeSchema.getName().get())));
				attributes.add(attribute);
			}
		}

		return new Tuple(table, attributes);
	}

	@Override
	public void delete(final Delete delete) {
		final AmazonDynamoDB amazonDynamoDB = amazonDynamoDBsIndexedByRegion.get(delete.getTable().getRegion());

		final Table table = delete.getTable();

		// Construct the key
		final HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();

		final Attribute hashKeyAttribute = delete.getPrimaryKeyHashAttribute();
		key.put(hashKeyAttribute.getSchema().getName().get(), hashKeyAttribute.getValue().get().toDynamoDBAttributeValue());

		if (table.getDefinition().getRangeKeySchema().isPresent()) {
			final Attribute rangeKeyAttribute = delete.getPrimaryKeyRangeAttribute().get();
			key.put(rangeKeyAttribute.getSchema().getName().get(), rangeKeyAttribute.getValue().get().toDynamoDBAttributeValue());
		}

		// Set expectations
		Map<String, ExpectedAttributeValue> expected = new HashMap<String, ExpectedAttributeValue>();

		if (delete.getVersionAttribute().isPresent()) {
			Attribute versionAttribute = delete.getVersionAttribute().get();
			expected.put(versionAttribute.getSchema().getName().get(),
					new ExpectedAttributeValue().withValue(versionAttribute.getValue().get().toDynamoDBAttributeValue()));
		}

		for (final Attribute expectedAttribute : delete.getNonConstrainedAttributes()) {
			expected.put(expectedAttribute.getSchema().getName().get(),
					new ExpectedAttributeValue().withValue(expectedAttribute.getValue().get().toDynamoDBAttributeValue()));
		}

	    DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
	        .withTableName(table.getDefinition().getName())
	        .withKey(key)
	        .withExpected(expected)
	        //TODO do sth with this
	        .withReturnValues(ReturnValue.ALL_OLD);

	    // TODO Do sth with this
	 	@SuppressWarnings("unused")
	    DeleteItemResult deleteItemResult = amazonDynamoDB.deleteItem(deleteItemRequest);
	}

	@Override
	public void update(final Update update) {
		final AmazonDynamoDB amazonDynamoDB = amazonDynamoDBsIndexedByRegion.get(update.getTable().getRegion());

		final Table table = update.getTable();
		// Construct the key
		final HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();

		final Attribute hashKeyAttribute = update.getPrimaryKeyHashAttribute();
		key.put(hashKeyAttribute.getSchema().getName().get(), hashKeyAttribute.getValue().get().toDynamoDBAttributeValue());

		if (table.getDefinition().getRangeKeySchema().isPresent()) {
			final Attribute rangeKeyAttribute = update.getPrimaryKeyRangeAttribute().get();
			key.put(rangeKeyAttribute.getSchema().getName().get(), rangeKeyAttribute.getValue().get().toDynamoDBAttributeValue());
		}

		Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();


		for (final AttributeValueOperation attributeValueOperation : update.getOperations()) {
			updateItems.put(
					attributeValueOperation.getOn().getName().get(),
					attributeValueOperation.toDynamoDBAttributeValueUpdate());
		}

		// Prepare request
		UpdateItemRequest updateItemRequest = new UpdateItemRequest()
				.withTableName(table.getDefinition().getName())
				//TODO do sth with this
				.withKey(key).withReturnValues(ReturnValue.UPDATED_NEW)
				.withAttributeUpdates(updateItems);

		// TODO Do sth with this
		@SuppressWarnings("unused")
		UpdateItemResult result = amazonDynamoDB.updateItem(updateItemRequest);
	}

	@Override
	public List<Tuple> query(final Query query) {
		final AmazonDynamoDB amazonDynamoDB = amazonDynamoDBsIndexedByRegion.get(query.getTable().getRegion());

		final Table table = query.getTable();

		final Map<String, Condition> keyConditions = new HashMap<>();

		// Hash key conditions
		final AttributeValueCriterion hashKeyCondition = query.getHashKeyCondition();
		keyConditions.put(
				hashKeyCondition.getOf().getName().get(),
				hashKeyCondition.toDynamoDBCondition());

		// Range key conditions
		for (final AttributeValueCriterion rangeKeyCondition : query.getRangeKeyConditions()) {
			keyConditions.put(
					rangeKeyCondition.getOf().getName().get(),
					rangeKeyCondition.toDynamoDBCondition());
		}

		// Local secondary index key conditions
		if (query instanceof QueryByLocalSecondaryIndex) {
			for (final AttributeValueCriterion localSecondaryIndexCondition : ((QueryByLocalSecondaryIndex) query).getLocalSecondaryIndexConditions()) {
				keyConditions.put(
						localSecondaryIndexCondition.getOf().getName().get(),
						localSecondaryIndexCondition.toDynamoDBCondition());
			}
		}

		// Prepare query
		QueryRequest queryRequest = new QueryRequest()
				.withTableName(table.getDefinition().getName())
				.withKeyConditions(keyConditions)
				.withConsistentRead(query.isConsistentlyRead())
				//TODO scanindexforward
				.withLimit(query.getLimit());

		// Include all attributes unless specified
		if (query.getIncludedAttributesSchemas().isEmpty()) {
			String[] includedAttributesNames = extract(table.getDefinition().getAttributesSchemas(), on(AttributeSchema.class).getName().get()).toArray(new String[query.getIncludedAttributesSchemas().size()]);
			queryRequest = queryRequest
					.withAttributesToGet(Arrays.asList(includedAttributesNames));
		}

		if (query instanceof QueryByLocalSecondaryIndex) {
			queryRequest = queryRequest
					.withIndexName(((QueryByLocalSecondaryIndex) query).getIndexName());
					// TODO Do sth with this
					//.withSelect(Select.ALL_PROJECTED_ATTRIBUTES);
		}

		List<Tuple> items = new ArrayList<>();

		Map<String, AttributeValue> lastEvaluatedKey = null;
		do {
			queryRequest = queryRequest.withExclusiveStartKey(lastEvaluatedKey);

			// Fire
			// TODO Do sth with this
			QueryResult queryResult = amazonDynamoDB.query(queryRequest);

			// Process results
			for (Map<String, AttributeValue> dynamoDBItem : queryResult.getItems()) {
				final Set<Attribute> attributes = new HashSet<>();
				for (final AttributeSchema attributeSchema : table.getDefinition().getAttributesSchemas()) {
					if (dynamoDBItem.containsKey(attributeSchema.getName().get())) {
						final Attribute attribute = new Attribute(
								attributeSchema,
								com.polymathiccoder.avempace.persistence.domain.attribute.AttributeValue.fromDynamoDBAttributeValue(
										attributeSchema,
										dynamoDBItem.get(attributeSchema.getName().get())));

						attributes.add(attribute);
					}
				}
				items.add(new Tuple(table, attributes));
			}
			lastEvaluatedKey = queryResult.getLastEvaluatedKey();
		} while (lastEvaluatedKey != null);

		return items;
	}

	@Override
	public List<Tuple> scan(final Scan scan) {
		final AmazonDynamoDB amazonDynamoDB = amazonDynamoDBsIndexedByRegion.get(scan.getTable().getRegion());

		final Table table = scan.getTable();

		Map<String, Condition> scanFilter = new HashMap<>();

		// ConditionsAttributeScanCondition
		for (final AttributeValueCriterion condition : scan.getCriteria()) {
			scanFilter.put(condition.getOf().getName().get(), condition.toDynamoDBCondition());
		}

		// Prepare scan
		ScanRequest scanRequest = new ScanRequest()
			    .withTableName(table.getDefinition().getName())
			    // TODO Do sth with this
			    .withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
			    .withScanFilter(scanFilter)
			    .withLimit(scan.getLimit());

		// Include all attributes unless specified
		if (scan.getIncludedAttributesSchemas().isEmpty()) {
			String[] includedAttributesNames = extract(table.getDefinition().getAttributesSchemas(), on(AttributeSchema.class).getName().get()).toArray(new String[scan.getIncludedAttributesSchemas().size()]);
			scanRequest = scanRequest.withAttributesToGet(Arrays.asList(includedAttributesNames));
		}

		List<Tuple> items = new ArrayList<>();

		Map<String, AttributeValue> lastKeyEvaluated = null;
		do {
		    scanRequest = scanRequest.withExclusiveStartKey(lastKeyEvaluated);

		    // Fire
		 	// TODO Do sth with this
		    ScanResult result = amazonDynamoDB.scan(scanRequest);

			// Process results
		    for (Map<String, AttributeValue> dynamoDBItem : result.getItems()){
		    	final Set<Attribute> attributes = new HashSet<>();
				for (final AttributeSchema attributeSchema : table.getDefinition().getAttributesSchemas()) {
					if (dynamoDBItem.containsKey(attributeSchema.getName().get())) {
						final Attribute attribute = new Attribute(
								attributeSchema,
								com.polymathiccoder.avempace.persistence.domain.attribute.AttributeValue.fromDynamoDBAttributeValue(
										attributeSchema,
										dynamoDBItem.get(attributeSchema.getName().get())));

						attributes.add(attribute);
					}
				}
				items.add(new Tuple(table, attributes));
		    }
		    lastKeyEvaluated = result.getLastEvaluatedKey();
		} while (lastKeyEvaluated != null);

		return items;
	}

	@Override
	public List<Tuple> batchRead(final Set<Get> gets) {
		//TODO Make sure all gets are on the same region
		final AmazonDynamoDB amazonDynamoDB = amazonDynamoDBsIndexedByRegion.get(gets.iterator().next().getTable().getRegion());

		final Map<String, List<Map<String, AttributeValue>>> keysIndexedByTableNames = new HashMap<>();

		for (final Get get : gets) {
			final Table table = get.getTable();

			final Map<String, AttributeValue> key = new HashMap<>();

			final Attribute hashKeyAttribute = get.getPrimaryKeyHashAttribute();
			key.put(hashKeyAttribute.getSchema().getName().get(), hashKeyAttribute.getValue().get().toDynamoDBAttributeValue());

			if (table.getDefinition().getRangeKeySchema().isPresent()) {
				final Attribute rangeKeyAttribute = get.getPrimaryKeyRangeAttribute().get();
				key.put(rangeKeyAttribute.getSchema().getName().get(), rangeKeyAttribute.getValue().get().toDynamoDBAttributeValue());
			}

			if (keysIndexedByTableNames.containsKey(table.getDefinition().getName())) {
				keysIndexedByTableNames.get(table.getDefinition().getName()).add(key);
			} else {
				List<Map<String, AttributeValue>> keys = new ArrayList<>();
				keys.add(key);
				keysIndexedByTableNames.put(table.getDefinition().getName(), keys);
			}
		}

		Map<String, KeysAndAttributes> requestItems = new HashMap<>();
		for (final Entry<String, List<Map<String, AttributeValue>>> tableNameAndKeys : keysIndexedByTableNames.entrySet()) {
			requestItems.put(
					tableNameAndKeys.getKey(),
					new KeysAndAttributes().withKeys(tableNameAndKeys.getValue()));
		}

		BatchGetItemResult batchGetItemResult = null;
		final BatchGetItemRequest batchGetItemRequest = new BatchGetItemRequest();

		do {
			batchGetItemRequest.withRequestItems(requestItems);
            batchGetItemResult = amazonDynamoDB.batchGetItem(batchGetItemRequest);
            requestItems = batchGetItemResult.getUnprocessedKeys();
		} while (batchGetItemResult.getUnprocessedKeys().size() > 0);

		if(batchGetItemResult.getUnprocessedKeys().size() > 0){
			throw new RuntimeException("unprocessed keys: " + batchGetItemResult.getUnprocessedKeys().size());
		}

		final Map<String, List<Map<String, AttributeValue>>> batchGetItemResultResponses = batchGetItemResult.getResponses();

		final List<Tuple> items = new ArrayList<>();

		for (final Get get : gets) {
			final Table table = get.getTable();
			final List<Map<String, AttributeValue>> dynamoDBItems = batchGetItemResultResponses.get(get.getTable().getDefinition().getName());
			for (Map<String, AttributeValue> dynamoDBItem : dynamoDBItems) {
				final Set<Attribute> attributes = new HashSet<>();
				for (final AttributeSchema attributeSchema : table.getDefinition().getAttributesSchemas()) {
					if (dynamoDBItem.containsKey(attributeSchema.getName().get())) {
						final Attribute attribute = new Attribute(
								attributeSchema,
								com.polymathiccoder.avempace.persistence.domain.attribute.AttributeValue.fromDynamoDBAttributeValue(
										attributeSchema,
										dynamoDBItem.get(attributeSchema.getName().get())));

						attributes.add(attribute);
					}
				}
				items.add(new Tuple(table, attributes));
			}
		}

		return items;
	}

	@Override
	public <T extends DMLOperation & BatchableWrite> void batchWrite(Set<T> batchableWrites) {
		//TODO Make sure all gets are on the same region
		final AmazonDynamoDB amazonDynamoDB = amazonDynamoDBsIndexedByRegion.get(batchableWrites.iterator().next().getTable().getRegion());

		Map<String, List<WriteRequest>> requestItems = new HashMap<>();

		for (final DMLOperation write : batchableWrites) {
			final Table table = write.getTable();

			final HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();

			WriteRequest writeRequest = null;
			if (write instanceof Delete) {
				final Delete delete = (Delete) write;

				final Attribute hashKeyAttribute = delete.getPrimaryKeyHashAttribute();
				key.put(hashKeyAttribute.getSchema().getName().get(), hashKeyAttribute.getValue().get().toDynamoDBAttributeValue());

				if (table.getDefinition().getRangeKeySchema().isPresent()) {
					final Attribute rangeKeyAttribute = delete.getPrimaryKeyRangeAttribute().get();
					key.put(rangeKeyAttribute.getSchema().getName().get(), rangeKeyAttribute.getValue().get().toDynamoDBAttributeValue());
				}

				writeRequest = new WriteRequest()
						.withDeleteRequest(new DeleteRequest().withKey(key));
			} else if (write instanceof Put) {
				Put put = (Put) write;

				Map<String, AttributeValue> dynamoDBItem = new HashMap<String, AttributeValue>();
				for (final Attribute attribute : put.getTuple().getAttributes()) {
					dynamoDBItem.put(attribute.getSchema().getName().get(), attribute.getValue().get().toDynamoDBAttributeValue());
				}

				writeRequest = new WriteRequest()
						.withPutRequest(new PutRequest().withItem(dynamoDBItem));
			}

			if (requestItems.containsKey(table.getDefinition().getName())) {
				requestItems.get(table.getDefinition().getName()).add(writeRequest);
			} else {
				List<WriteRequest> writeRequests = new ArrayList<>();
				writeRequests.add(writeRequest);
				requestItems.put(table.getDefinition().getName(), writeRequests);
			}
		}

        BatchWriteItemResult batchWriteItemResult;
        BatchWriteItemRequest batchWriteItemRequest = new BatchWriteItemRequest();
        do {
        	batchWriteItemRequest.withRequestItems(requestItems);
        	//TODO do sth with it
        	batchWriteItemResult = amazonDynamoDB.batchWriteItem(batchWriteItemRequest);
        	requestItems = batchWriteItemResult.getUnprocessedItems();
        } while (batchWriteItemResult.getUnprocessedItems().size() > 0);
	}
}
