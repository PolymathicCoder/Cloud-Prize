package com.polymathiccoder.avempace.persistence.service.dml;

import java.util.List;
import java.util.Set;

import com.polymathiccoder.avempace.persistence.domain.Tuple;
import com.polymathiccoder.avempace.persistence.domain.operation.BatchableWrite;
import com.polymathiccoder.avempace.persistence.domain.operation.DMLOperation;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.Delete;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.Get;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.Put;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.Query;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.Scan;
import com.polymathiccoder.avempace.persistence.domain.operation.dml.Update;

public interface DynamoDBDMLOperationsService {
// Write
	void put(final Put put);
	void update(final Update update);
	void delete(final Delete delete);

	<T extends DMLOperation & BatchableWrite> void batchWrite(final Set<T> writes);

// Read
	Tuple get(final Get get);

	List<Tuple> batchRead(final Set<Get> get);

	List<Tuple> query(final Query query);
	List<Tuple> scan(final Scan scan);
}