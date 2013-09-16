package com.polymathiccoder.avempace.meta.model.processing;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.polymathiccoder.avempace.meta.annotation.Entity;
import com.polymathiccoder.avempace.meta.model.MetaModel;

public final class MetaProcessor {
// Static fields
	private static final Logger LOGGER = LoggerFactory.getLogger("com.polymathiccoder.nimble");

	private static final Cache<Class<?>, MetaModel> CACHE;

	static {
		CACHE = CacheBuilder.newBuilder()
				.maximumSize(1000)
				.build(new CacheLoader<Class<?>, MetaModel>() {
					public MetaModel load(final Class<?> typeAnnotatedWithDynamoDBEntity) {
						return MetaModel.create(typeAnnotatedWithDynamoDBEntity);
					}
				});
	}

// Behavior
	public MetaModel lookup(Class<?> typeAnnotatedWithDynamoDBEntity) {
		try {
			return CACHE.get(typeAnnotatedWithDynamoDBEntity);
		} catch (final ExecutionException executionException) {
			LOGGER.warn("Problem caching meta model data");
			return MetaModel.create(typeAnnotatedWithDynamoDBEntity);
		}
	}

	public Set<MetaModel> discover(final String... includedPackages) {
		final Set<MetaModel> dynamoDBEntitiesMetaModels = new HashSet<>();

		final Set<Class<?>> typesAnnotatedWithDynamoDBEntity = discoverClassesAnnotatedWithDynamoDBEntity(includedPackages);

		for (Class<?> typeAnnotatedWithDynamoDBEntity : typesAnnotatedWithDynamoDBEntity) {
			dynamoDBEntitiesMetaModels.add(lookup(typeAnnotatedWithDynamoDBEntity));
		}

		return dynamoDBEntitiesMetaModels;
	}

	// Helpers
	private static Set<Class<?>> discoverClassesAnnotatedWithDynamoDBEntity(final String... includedPackages) {
		final Set<Object> params = new HashSet<>();
		params.add(ClasspathHelper.forPackage(StringUtils.EMPTY));
		for (final String includedPackage : includedPackages) {
			params.add(new FilterBuilder().includePackage(includedPackage));
		}

		final Reflections reflections = new Reflections(params.toArray());

		return reflections.getTypesAnnotatedWith(Entity.class);
	}
}
