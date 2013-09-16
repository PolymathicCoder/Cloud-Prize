package com.polymathiccoder.avempace.config;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class AvempaceConfiguration {
	// Fields
	private final String accessKey;
	private final String secretKey;
	private final SchemaGenerationStrategy schemaGenerationStrategy;

	// Life cycle
	public AvempaceConfiguration(final String accessKey, final String secretKey, final SchemaGenerationStrategy schemaGenerationStrategy) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.schemaGenerationStrategy = schemaGenerationStrategy;
	}

	// Accessors and mutators
	public String getAccessKey() { return accessKey; }

	public String getSecretKey() { return secretKey; }

	public SchemaGenerationStrategy getSchemaGenerationStrategy() { return schemaGenerationStrategy; }

	// Types
	public static enum SchemaGenerationStrategy {
		CLEAN_SLATE, VALIDATE, DO_NOTHING
	}

	// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
