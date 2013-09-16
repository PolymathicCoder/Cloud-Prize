package com.polymathiccoder.avempace.persistence.domain.operation;

import com.polymathiccoder.avempace.persistence.domain.Table;

public abstract class Operation {
// Fields
	protected final Table table;

// Life cycle
	protected Operation(final Table table) {
		this.table = table;
	}

// Accessors and mutators
	public Table getTable() { return table; }
}
