package com.polymathiccoder.avempace.persistence.service.ddl;

import java.util.List;

import com.polymathiccoder.avempace.persistence.domain.operation.DDLOperation;
import com.polymathiccoder.avempace.persistence.domain.operation.ddl.CreateTable;
import com.polymathiccoder.avempace.persistence.domain.operation.ddl.DeleteTable;

public interface DynamoDBDDLOperationsService {
	void execute(final DDLOperation ddlOperation);
	void batch(final List<? extends DDLOperation> ddlOperations);

	void createTable(final CreateTable createTable);

	void deleteTable(final DeleteTable deleteTable);
}
