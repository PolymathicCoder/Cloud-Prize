package com.polymathiccoder.avempace.persistence.domain.operation;

import com.polymathiccoder.avempace.persistence.domain.Table;

public abstract class DMLOperation extends Operation {
// Life cycle
	protected DMLOperation(final Table table) {
		super(table);
	}

// Behavior
	public abstract void validate();
}
