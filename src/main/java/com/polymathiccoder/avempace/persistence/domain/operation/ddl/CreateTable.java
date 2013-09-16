package com.polymathiccoder.avempace.persistence.domain.operation.ddl;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import com.polymathiccoder.avempace.persistence.domain.Table;
import com.polymathiccoder.avempace.persistence.domain.operation.DDLOperation;

@AutoProperty
public final class CreateTable extends DDLOperation {
// Life cycle
	private CreateTable(final Builder builder) {
		super(builder.table);
	}

// Types
	public static class Builder {
		// Required
		private Table table;

		// Optional

		private Builder() {
			defaults();
		}

		public static Builder create(final Table table) {
			final Builder builder = new Builder();
			builder.table = table;
			return builder;
		}

		private void defaults() {
		}

		public CreateTable build() {
			return new CreateTable(this);
		}
	}

// Common methods
	@Override
	public boolean equals(final Object other) { return Pojomatic.equals(this, other); }
	@Override
	public int hashCode() { return Pojomatic.hashCode(this); }
	@Override
	public String toString() { return Pojomatic.toString(this); }
}
