package com.polymathiccoder.avempace.persistence.service.ddl;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.retryer.IRetryableTask;
import org.retryer.RetryerException;
import org.retryer.dsl.Backoff;
import org.retryer.impl.Retryer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.autoscaling.model.ResourceInUseException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;
import com.google.common.base.Predicate;
import com.polymathiccoder.avempace.persistence.domain.Table;

public final class TableStatusCheckerRetryableTask implements IRetryableTask<String, Throwable> {
// Static fields
	private static final Logger LOGGER = LoggerFactory.getLogger("com.polymathiccoder.nimble");
	private static final String LOGGER_MESSAGE = "%s - %s: Retrying until the condition: '%s' is fulfilled for the table named '%s'";

	private static final int INITIAL_DELAY_IN_SECONDS = 10;
	private static final int MAXIMUM_DELAY_IN_MINUTES = 5;

	private static final String NO_STATUS__TABLE_NOT_FOUND = "TABLE_NOT_FOUND";
	private static final String NO_STATUS__TABLE_IN_USE = "TABLE_IN_USE";

// Fields
	private final AmazonDynamoDB amazonDynamoDB;

	private final String tableName;
	private final TableStatusCheckerCondition condition;

// Life cycle
	private TableStatusCheckerRetryableTask(final AmazonDynamoDB amazonDynamoDB, final String tableName, final TableStatusCheckerCondition condition) {
		this.amazonDynamoDB = amazonDynamoDB;
		this.tableName = tableName;
		this.condition = condition;
	}

// Static behavior
	public static void retryUntil(final AmazonDynamoDB amazonDynamoDB, final Table table, final TableStatusCheckerCondition condition) {
		LOGGER.debug(String.format(
				LOGGER_MESSAGE,
				"INITIALIZED",
				"Backing-off exponentially with an init delay of " + INITIAL_DELAY_IN_SECONDS + " seconds for a max of " + MAXIMUM_DELAY_IN_MINUTES + " minutes",
				condition.affirmative(),
				table.getDefinition().getName()));

		final DateTime start = new DateTime();
		try {
			final Retryer retryer = new Retryer();
			retryer.doRetryable(
					new TableStatusCheckerRetryableTask(
							amazonDynamoDB,
							table.getDefinition().getName(),
							condition),
		            Backoff.withExponentialGrowingDelay()
		            		.startingWithDelay(INITIAL_DELAY_IN_SECONDS, TimeUnit.SECONDS)
		                    .maxDelay(MAXIMUM_DELAY_IN_MINUTES, TimeUnit.MINUTES)
		                    .build());
		} catch (final RetryerException retryerException) {
			LOGGER.warn(String.format(
					LOGGER_MESSAGE,
					"PROBLEM    ",
					condition.affirmative(),
					table.getDefinition().getName()));
		} catch (final InterruptedException interruptedException) {
			LOGGER.warn(String.format(
					LOGGER_MESSAGE,
					"PROBLEM    ",
					condition.affirmative(),
					table.getDefinition().getName()));
		}

		LOGGER.debug(String.format(
				LOGGER_MESSAGE,
				"FINISHED   ",
				"Elapsed time " + DurationFormatUtils.formatDurationWords(new Duration(start, new DateTime()).getMillis(), true, true),
				condition.affirmative(),
				table.getDefinition().getName()));
	}

// Behavior
	@Override
	public String execute(final int tryNumber) throws Throwable {
		LOGGER.trace(String.format(
				LOGGER_MESSAGE,
				"IN PROGRESS",
				"Attempt #" + (tryNumber + 1),
				condition.affirmative(),
				tableName));

		final DescribeTableRequest describeTableRequest = new DescribeTableRequest()
				.withTableName(tableName);

		String currentTableStatus;
		try {
			final TableDescription tableDescription = amazonDynamoDB.describeTable(describeTableRequest).getTable();
			currentTableStatus = tableDescription.getTableStatus();
		} catch (final  ResourceNotFoundException resourceNotFoundException) {
			currentTableStatus = NO_STATUS__TABLE_NOT_FOUND;
		} catch (final ResourceInUseException resourceNotFoundException) {
			currentTableStatus = NO_STATUS__TABLE_IN_USE;
		}

		if (! condition.apply(currentTableStatus)) {
			throw new Exception();
		}

		return tableName;
	}

	@Override
	public boolean isFatalReason(int tryNumber, Throwable throwable) {
		if (throwable instanceof AmazonClientException || throwable instanceof AmazonServiceException) {
			return true;
		}
		return false;
	}

// Types
	// Conditions
	public static interface TableStatusCheckerCondition extends Predicate<String> {
		String affirmative();
		String negative();
	}

	public static class TableStatusIsActiveCondition implements TableStatusCheckerCondition {
	// Static fields
		private static final String CONDITION_MESSAGE = "The table status %s " + TableStatus.ACTIVE.name().toLowerCase();

	// Behavior
		@Override
		public boolean apply(@Nullable final String tablestatus) {
			final boolean condition = TableStatus.ACTIVE.name().equalsIgnoreCase(tablestatus);
			return condition;
		}

		@Override
		public String affirmative() {
			return String.format(CONDITION_MESSAGE, "is");
		}

		@Override
		public String negative() {
			return String.format(CONDITION_MESSAGE, "is not");
		}
	}

	public static class TableDoesNotExistCondition implements TableStatusCheckerCondition {
	// Static fields
		private static final String CONDITION_MESSAGE = "The table %s";

	// Behavior
		@Override
		public boolean apply(@Nullable final String tablestatus) {
			final boolean condition = NO_STATUS__TABLE_NOT_FOUND.equalsIgnoreCase(tablestatus);
			return condition;
		}

		@Override
		public String affirmative() {
			return String.format(CONDITION_MESSAGE, "does not exist");
		}

		@Override
		public String negative() {
			return String.format(CONDITION_MESSAGE, "exists");
		}
	}

	public static class TableIsNotAvailableCondition implements TableStatusCheckerCondition {
	// Static fields
		private static final String CONDITION_MESSAGE = "The table %s available";

	// Behavior
		@Override
		public boolean apply(@Nullable final String tablestatus) {
			final boolean condition = NO_STATUS__TABLE_IN_USE.equalsIgnoreCase(tablestatus);
			return condition;
		}

		@Override
		public String affirmative() {
			return String.format(CONDITION_MESSAGE, "is not");
		}

		@Override
		public String negative() {
			return String.format(CONDITION_MESSAGE, "is");
		}
	}
}
