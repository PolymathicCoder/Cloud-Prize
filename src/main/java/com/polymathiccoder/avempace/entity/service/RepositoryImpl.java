package com.polymathiccoder.avempace.entity.service;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.fest.reflect.core.Reflection.field;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.polymathiccoder.avempace.config.Region;
import com.polymathiccoder.avempace.entity.domain.Entity;
import com.polymathiccoder.avempace.entity.domain.EntityPropertyValueCriteria;
import com.polymathiccoder.avempace.entity.domain.EntityPropertyValueCriterion;
import com.polymathiccoder.avempace.entity.domain.EntityPropertyValueOperation;
import com.polymathiccoder.avempace.entity.domain.EntityPropertyValueOperations;
import com.polymathiccoder.avempace.entity.domain.PropertySchema;
import com.polymathiccoder.avempace.mapping.SchemaMappingEntry;
import com.polymathiccoder.avempace.persistence.domain.Table;
import com.polymathiccoder.avempace.persistence.domain.TableDefinition;
import com.polymathiccoder.avempace.persistence.domain.Tuple;
import com.polymathiccoder.avempace.persistence.domain.attribute.Attribute;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeSchema;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeValue;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeValueCriterion;
import com.polymathiccoder.avempace.persistence.domain.attribute.AttributeValueOperation;
import com.polymathiccoder.avempace.persistence.domain.attribute.constraint.AttributeConstraint.AttributeConstraintType;
import com.polymathiccoder.avempace.persistence.domain.attribute.constraint.LocalSecondaryIndex;
import com.polymathiccoder.avempace.persistence.domain.operation.ddl.CreateTable;
import com.polymathiccoder.avempace.persistence.domain.operation.ddl.DeleteTable;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.Delete;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.Get;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.Put;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.QueryByLocalSecondaryIndex;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.QueryByPrimaryKeyRange;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.Scan;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.Update;
import com.polymathiccoder.avempace.persistence.domain.value.StringValue;
import com.polymathiccoder.avempace.persistence.service.ddl.DynamoDBDDLOperationsService;
import com.polymathiccoder.avempace.persistence.service.dml.DynamoDBDMLOperationsService;

public class RepositoryImpl<T> implements Repository<T> {
// Fields
	private final DynamoDBDMLOperationsService dynamoDBDMLOperationsService;

	private final DynamoDBDDLOperationsService dynamoDBDDLOperationsService;

	private final Model<T> model;

// Life cycle
	public RepositoryImpl(final DynamoDBDDLOperationsService dynamoDBDDLOperationsService, final DynamoDBDMLOperationsService dynamoDBDMLOperationsService, final Model<T> model) {
		this.dynamoDBDDLOperationsService = dynamoDBDDLOperationsService;
		this.dynamoDBDMLOperationsService = dynamoDBDMLOperationsService;
		this.model = model;
	}

// Behavior
	@Override
	public T find(final EntityPropertyValueCriteria entityPropertyValueCriteria) {
		final DistribbutionDefinition distribbutionDefinition = model.getDistribbutionDefinition();

		return find(entityPropertyValueCriteria, distribbutionDefinition.lookupTheClosestRegion());
	}

	private T find(final EntityPropertyValueCriteria entityPropertyValueCriteria, final Region region) {
		final TableDefinition tableDefinition = model.getMapping().getTableDefinition();
		final Table table = Table.Builder.create(tableDefinition, region).build();

		/// should not do this here
		final Set<Attribute> keyAttributes = inferAttributes(entityPropertyValueCriteria.get());

		Get get = Get.Builder.create(table, keyAttributes).build();
		get.validate();

		Tuple tuple = dynamoDBDMLOperationsService.get(get);

		final Entity<T> entity = tuple.toEntity(model.getMapping().getEntityCollectionDefinition().getOfType());

		return entity.getPojo();
	}

	@Override
	public List<T> findAllBy(final EntityPropertyValueCriteria entityPropertyValuePropertyValueCriteria) {
		final DistribbutionDefinition distribbutionDefinition = model.getDistribbutionDefinition();

		return findAllBy(entityPropertyValuePropertyValueCriteria, distribbutionDefinition.lookupTheClosestRegion());
	}

	private List<T> findAllBy(final EntityPropertyValueCriteria entityPropertyValueCriteria, final Region region) {
		final TableDefinition tableDefinition = model.getMapping().getTableDefinition();
		final Table table = Table.Builder
				.create(tableDefinition, region)
				.build();

		final Set<AttributeValueCriterion> attributeValueCriteria = new HashSet<>();

		for (final EntityPropertyValueCriterion entityPropertyValueCriterion : entityPropertyValueCriteria.get()) {
			final AttributeSchema attributeSchema = model.getMapping().lookupByPropertyName(entityPropertyValueCriterion.getOf()).getAttributeSchema();

			final AttributeValueCriterion attributeValueCriterion = entityPropertyValueCriterion.toAttributeValueCriterion(attributeSchema);
			attributeValueCriteria.add(attributeValueCriterion);
		}

		List<Tuple> tuples = new ArrayList<>();

		// TODO Refactor out
		// Determine the best way to query
		final AttributeValueCriterion rangeAttributeValueCriterion = selectFirst(
				attributeValueCriteria,
				having(
						on(AttributeSchema.class).getConstraint().getType(),
						equalTo(AttributeConstraintType.PRIMARY_RANGE_KEY)));
		final AttributeValueCriterion lsiAttributeValueCriterion = selectFirst(
				attributeValueCriteria,
				having(
						on(AttributeSchema.class).getConstraint().getType(),
						equalTo(AttributeConstraintType.LOCAL_SECONDARY_INDEX_KEY)));

		if (rangeAttributeValueCriterion != null) {
			final QueryByPrimaryKeyRange primaryKeyQuery = QueryByPrimaryKeyRange.Builder.create(table, attributeValueCriteria).build();
			primaryKeyQuery.validate();
			tuples = dynamoDBDMLOperationsService.query(primaryKeyQuery);
		} else if (lsiAttributeValueCriterion != null) {
			LocalSecondaryIndex localSecondaryIndex = (LocalSecondaryIndex) lsiAttributeValueCriterion.getOf().getConstraint();
			final QueryByLocalSecondaryIndex indexQuery = QueryByLocalSecondaryIndex.Builder.create(table, localSecondaryIndex.getIndexName(), attributeValueCriteria).build();
			indexQuery.validate();
			tuples = dynamoDBDMLOperationsService.query(indexQuery);
		} else {
			final Scan scan = Scan.Builder.create(table, attributeValueCriteria).build();
			scan.validate();
			tuples = dynamoDBDMLOperationsService.scan(scan);
		}


		final List<T> pojos = new ArrayList<>();
		for (final Tuple tuple : tuples) {
			final Entity<T> entity = tuple.toEntity(model.getMapping().getEntityCollectionDefinition().getOfType());
			pojos.add(entity.getPojo());
		}

		return pojos;
	}

	@Override
	public List<T> findAll() {
		final DistribbutionDefinition distribbutionDefinition = model.getDistribbutionDefinition();

		return findAll(distribbutionDefinition.lookupTheClosestRegion());
	}

	private List<T> findAll(final Region region) {
		return findAllBy(EntityPropertyValueCriteria.anything(), region);
	}

	@Override
	public void save(final T pojo) {
		final DistribbutionDefinition distribbutionDefinition = model.getDistribbutionDefinition();

		final Region primaryRegion = distribbutionDefinition.getPrimaryRegion();
		save(pojo, primaryRegion);

		if (distribbutionDefinition.isPropagatedAcrossAllRegions() && ! distribbutionDefinition.getSecondaryRegions().isEmpty()) {
			final ExecutorService executor = Executors.newFixedThreadPool(distribbutionDefinition.getSecondaryRegions().size());
			for (final Region secondaryRegion : distribbutionDefinition.getSecondaryRegions()) {
				executor.execute(
						new Runnable() {
							@Override
							public void run() {
								save(pojo, secondaryRegion);
							}
						});
			}
			executor.shutdown();
		}
	}

	private void save(final T pojo, final Region region) {
		final Entity<T> entity = Entity.create(pojo, region);
		final Tuple tuple = Tuple.create(entity);
		final Table table = tuple.getBelongsIn();

		final Put put = Put.Builder
				.create(table, tuple)
				.build();

		put.validate();
		dynamoDBDMLOperationsService.put(put);
	}

	@Override
	public void update(final EntityPropertyValueCriteria entityPropertyValueCriteria, final EntityPropertyValueOperations entityPropertyValueOperations) {
		final DistribbutionDefinition distribbutionDefinition = model.getDistribbutionDefinition();

		final Region primaryRegion = distribbutionDefinition.getPrimaryRegion();
		update(entityPropertyValueCriteria, entityPropertyValueOperations, primaryRegion);

		if (distribbutionDefinition.isPropagatedAcrossAllRegions() && ! distribbutionDefinition.getSecondaryRegions().isEmpty()) {
			final ExecutorService executor = Executors.newFixedThreadPool(distribbutionDefinition.getSecondaryRegions().size());
			for (final Region secondaryRegion : distribbutionDefinition.getSecondaryRegions()) {
				executor.execute(
						new Runnable() {
							@Override
							public void run() {
								update(entityPropertyValueCriteria, entityPropertyValueOperations, secondaryRegion);
							}
						});
			}
			executor.shutdown();
		}
	}

	private void update(final EntityPropertyValueCriteria entityPropertyValueCriteria, final EntityPropertyValueOperations entityPropertyValueOperations, final Region region) {
		final TableDefinition tableDefinition = model.getMapping().getTableDefinition();
		final Table table = Table.Builder
				.create(tableDefinition, region)
				.build();

		final Set<Attribute> keyAttributes = inferAttributes(entityPropertyValueCriteria.get());
		final Update.Builder updateBuilder = Update.Builder
				.create(table, keyAttributes);

		final Set<AttributeValueOperation> attributeValueOperations = new HashSet<>();

		for (final EntityPropertyValueOperation entityPropertyValueOperation : entityPropertyValueOperations.get()) {

			final SchemaMappingEntry schemaMappingEntry = model.getMapping().lookupByPropertyName(entityPropertyValueOperation.getOn());
			final AttributeSchema attributeSchema;
			if (schemaMappingEntry != null) {
				attributeSchema = schemaMappingEntry.getAttributeSchema();
			} else {
				attributeSchema = AttributeSchema.create(
						entityPropertyValueOperation.getOn(),
						StringValue.class,
						null);
			}

			attributeValueOperations.add(entityPropertyValueOperation.toAttributeValueOpetration(attributeSchema));
		}

		updateBuilder.withOperations(attributeValueOperations);

		final Update update = updateBuilder.build();
		update.validate();
		dynamoDBDMLOperationsService.update(update);
	}

	@Override
	public void remove(final T pojo) {
		final DistribbutionDefinition distribbutionDefinition = model.getDistribbutionDefinition();

		final Region primaryRegion = distribbutionDefinition.getPrimaryRegion();
		remove(pojo, primaryRegion);

		if (distribbutionDefinition.isPropagatedAcrossAllRegions() && ! distribbutionDefinition.getSecondaryRegions().isEmpty()) {
			final ExecutorService executor = Executors.newFixedThreadPool(distribbutionDefinition.getSecondaryRegions().size());
			for (final Region secondaryRegion : distribbutionDefinition.getSecondaryRegions()) {
				executor.execute(
						new Runnable() {
							@Override
							public void run() {
								remove(pojo, secondaryRegion);
							}
						});
			}
			executor.shutdown();
		}
	}

	private void remove(final T pojo, final Region region) {
		final  Entity<T> entity = Entity.create(pojo, region);
		final Tuple tuple = Tuple.create(entity);

		final Set<Attribute> attributes = constructKeyAttributes(pojo);
		final Table table = tuple.getBelongsIn();

		final Delete delete = Delete.Builder
				.create(table, attributes)
				.build();

		delete.validate();
		dynamoDBDMLOperationsService.delete(delete);
	}

	@Override
	public void removeAll() {
		final DistribbutionDefinition distribbutionDefinition = model.getDistribbutionDefinition();

		final Region primaryRegion = distribbutionDefinition.getPrimaryRegion();
		removeAll(primaryRegion);

		if (distribbutionDefinition.isPropagatedAcrossAllRegions() && ! distribbutionDefinition.getSecondaryRegions().isEmpty()) {
			final ExecutorService executor = Executors.newFixedThreadPool(distribbutionDefinition.getSecondaryRegions().size());
			for (final Region secondaryRegion : distribbutionDefinition.getSecondaryRegions()) {
				executor.execute(
						new Runnable() {
							@Override
							public void run() {
								removeAll(secondaryRegion);
							}
						});
			}
			while(executor.isTerminated()) {}
			executor.shutdown();
		}
	}

	private void removeAll(final Region region) {
		final TableDefinition tableDefinition =model.getMapping().getTableDefinition();
		final Table table = Table.Builder.create(tableDefinition, region).build();

		final DeleteTable deleteTable = DeleteTable.Builder.create(table).build();
		//FIXME
		try {
			dynamoDBDDLOperationsService.deleteTable(deleteTable);
		} catch (final Exception exception) {
		}

		final CreateTable createTable = CreateTable.Builder.create(table).build();
		dynamoDBDDLOperationsService.createTable(createTable);
	}

	// Helpers
	private Set<Attribute> constructKeyAttributes(final T pojo) {
		final TableDefinition tableDefinition = model.getMapping().getTableDefinition();

		final Set<Attribute> keyAttributes = new HashSet<>();

		// Hash key
		final AttributeSchema hashKeyAttributeSchema = tableDefinition.getHashKeySchema();
		final PropertySchema hashKeyPropertySchema =  model.getMapping().lookupByAttributeSchema(hashKeyAttributeSchema).getPropertySchema();
		final Object hashKeyValue = field(hashKeyPropertySchema.getName().get())
				.ofType(hashKeyPropertySchema.getType().get())
				.in(pojo)
				.get();

		keyAttributes.add(
				new Attribute(
						hashKeyAttributeSchema,
						AttributeValue.fromEntityPropertyValue(
								hashKeyAttributeSchema,
								hashKeyValue)));

		// Range key
		if (tableDefinition.getRangeKeySchema().isPresent()) {
			final AttributeSchema rangeKeyAttributeSchema = tableDefinition.getRangeKeySchema().get();
			final PropertySchema rangeKeyPropertySchema =  model.getMapping().lookupByAttributeSchema(rangeKeyAttributeSchema).getPropertySchema();

			final Object rangeKeyValue = field(rangeKeyPropertySchema.getName().get())
					.ofType(rangeKeyPropertySchema.getType().get())
					.in(pojo)
					.get();

			keyAttributes.add(
					new Attribute(
							rangeKeyAttributeSchema,
							AttributeValue.fromEntityPropertyValue(
									rangeKeyAttributeSchema,
									rangeKeyValue)));
		}

		return keyAttributes;
	}

	private Set<Attribute> inferAttributes(final Set<EntityPropertyValueCriterion> entityPropertyValueCriteria) {
		// Keys
		final Set<Attribute> attributes = new HashSet<>();

		final Set<AttributeValueCriterion> attributeValueCriteria = new HashSet<>();
		for (final EntityPropertyValueCriterion pojoPropertyValueCriterion : entityPropertyValueCriteria) {
			final AttributeSchema attributeSchema = model.getMapping().lookupByPropertyName(pojoPropertyValueCriterion.getOf()).getAttributeSchema();

			final AttributeValueCriterion attributeValueCriterion = pojoPropertyValueCriterion.toAttributeValueCriterion(attributeSchema);
			attributeValueCriteria.add(attributeValueCriterion);
		}

		///
		for (final AttributeValueCriterion attributeValueCriterion : attributeValueCriteria) {
			final AttributeSchema attributeSchema = attributeValueCriterion.getOf();
			final AttributeValue attributeValue = attributeValueCriterion.getCriteria().iterator().next().getArguments().iterator().next();

			attributes.add(new Attribute(attributeSchema, attributeValue));
		}

		return attributes;
	}
}
