package com.polymathiccoder.avempace.persistence.service.ddl;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import com.polymathiccoder.avempace.persistence.domain.operation.DDLOperation;

@SuppressWarnings("serial")
public final class BatchOperationExecutor extends RecursiveAction {
// Static fields
	private static final int MAX_CONCURRENT_TABLE_OPERATIONS = 10;
	private static final int MAX_REQUESTS_PER_ACTION = 1;

// Fields
	private DynamoDBDDLOperationsService dymanoDBDDLOperations;
	private DDLOperation[] ddlOperations;

	private final int from;
	private final int to;

// Life cycle
	private BatchOperationExecutor(final DynamoDBDDLOperationsService dymanoDBDDLOperations, final DDLOperation[] ddlOperations, final int from, final int to) {
		this.dymanoDBDDLOperations = dymanoDBDDLOperations;
		this.ddlOperations = ddlOperations;

		this.from = from;
		this.to = to;
	}

// Static behavior
	public static void execute(final DynamoDBDDLOperationsService dymanoDBDDLOperations, final DDLOperation[] ddlOperations) {
		final ForkJoinPool forkJoinPool = new ForkJoinPool(MAX_CONCURRENT_TABLE_OPERATIONS);
		final BatchOperationExecutor action = new BatchOperationExecutor(
				dymanoDBDDLOperations,
				ddlOperations,
				0,
				ddlOperations.length - 1);
		forkJoinPool.invoke(action);
	}

// Behavior
	@Override
	protected void compute() {
		if (to - from > MAX_REQUESTS_PER_ACTION) {
			int mid = (from + to) >>> 1;
			invokeAll(
					Arrays.asList(
							new BatchOperationExecutor(dymanoDBDDLOperations, ddlOperations, from, mid),
							new BatchOperationExecutor(dymanoDBDDLOperations, ddlOperations, mid, to)));
		} else {
			for (final DDLOperation ddlOperation : Arrays.copyOfRange(ddlOperations, from, to + 1)) {
				dymanoDBDDLOperations.execute(ddlOperation);
			}
		}
	}
}
