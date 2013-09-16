package com.polymathiccoder.avempace.persistence.domain.operation;

import com.polymathiccoder.avempace.persistence.domain.Table;

public abstract class DDLOperation extends Operation {
	protected DDLOperation(final Table table) {
		super(table);
	}
}
