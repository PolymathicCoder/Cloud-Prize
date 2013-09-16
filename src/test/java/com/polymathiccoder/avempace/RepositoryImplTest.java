package com.polymathiccoder.avempace;

import static com.polymathiccoder.avempace.criteria.domain.Conditions.beginsWith;
import static com.polymathiccoder.avempace.criteria.domain.Conditions.contains;
import static com.polymathiccoder.avempace.criteria.domain.Conditions.equalTo;
import static com.polymathiccoder.avempace.criteria.domain.Conditions.greaterThanOrEqualTo;
import static com.polymathiccoder.avempace.entity.domain.EntityPropertyValueCriteria.matching;
import static com.polymathiccoder.avempace.entity.domain.EntityPropertyValueCriterion.$;
import static com.polymathiccoder.avempace.entity.domain.EntityPropertyValueOperation.change;
import static com.polymathiccoder.avempace.entity.domain.EntityPropertyValueOperations.apply;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.michelboudreau.alternator.AlternatorDB;
import com.polymathiccoder.avempace.config.Avempace;
import com.polymathiccoder.avempace.entity.service.Repository;

public class RepositoryImplTest {
	public static AlternatorDB MOCKED_DYNAMODB;

	private static Repository<Employee> REPOSITORY;

	@BeforeClass
	public static void setup() throws Exception {
		//MOCKED_DYNAMODB = new AlternatorDB();
		//MOCKED_DYNAMODB.start();
		REPOSITORY = Avempace.getRepositoryFactory().createRepository(Employee.class);
	}

	@Test
	public void testSave() {
		final Employee employee = new Employee(1l, "Abdelmonaim", 28, "PolymathicCoder Inc.", "USA", Lists.newArrayList(23, 43, 12, 5), Lists.newArrayList("ORANGE", "BLUE"));

		REPOSITORY.save(employee);

		List<Employee> employees = REPOSITORY.findAll();

		assertThat(employees.size(), org.hamcrest.Matchers.equalTo(1));
	}

	@Test
	public void testRemove() {
		final Employee employee = new Employee(1l, "Abdelmonaim", 28, "PolymathicCoder Inc.", "USA", Lists.newArrayList(23, 43, 12, 5), Lists.newArrayList("ORANGE", "BLUE"));

		REPOSITORY.save(employee);

		// Operation
		REPOSITORY.remove(employee);

		List<Employee> employees = REPOSITORY.findAllBy(
				matching(
						$("company").is(equalTo("PolymathicCoder Inc.")))
					.and(
						$("id").is(equalTo(0)))
		);

		assertThat(employees.size(), org.hamcrest.Matchers.equalTo(0));
	}

	@Test
	public void testFind() {
		final Employee employee = new Employee(1l, "Abdelmonaim", 28, "PolymathicCoder Inc.", "USA", Lists.newArrayList(23, 43, 12, 5), Lists.newArrayList("ORANGE", "BLUE"));

		REPOSITORY.save(employee);
		final Employee actual = REPOSITORY.find(
				matching(
						$("company").is(equalTo("PolymathicCoder Inc.")))
					.and(
						$("id").is(equalTo(1l)))
		);

		assertThat(actual.getId(), org.hamcrest.Matchers.equalTo(employee.getId()));
	}

	@Test
	public void testUpdate() {
		// Data
		final Employee employee = new Employee(1l, "Abdelmonaim", 28, "PolymathicCoder Inc.", "USA", Lists.newArrayList(23, 43, 12, 5), Lists.newArrayList("ORANGE", "BLUE"));
		REPOSITORY.save(employee);

		// Operation
		REPOSITORY.update(
				matching(
						$("company").is(equalTo("PolymathicCoder Inc.")))
					.and(
						$("id").is(equalTo(1l))),
				apply(
						change("location").withValue("United States of America"))
					.and(
						change("firstName").withValue("Abdel"))
		);

		// Test
		final Employee actual = REPOSITORY.find(
				matching(
						$("company").is(equalTo("PolymathicCoder Inc.")))
					.and(
						$("id").is(equalTo(1l))
		));
		final Employee expected = new Employee(1l, "Abdel", 28, "PolymathicCoder Inc.", "United States of America", Lists.newArrayList(23, 43, 12, 5), Lists.newArrayList("ORANGE", "BLUE"));

		assertThat(actual.getLocation(), org.hamcrest.Matchers.equalTo(expected.getLocation()));
		assertThat(actual.getFirstName(), org.hamcrest.Matchers.equalTo(expected.getFirstName()));
		assertThat(actual.getId(), org.hamcrest.Matchers.equalTo(expected.getId()));
	}

	@Test
	public void testFindAllByOnRange() {
		// Data
		final Employee employee1 = new Employee(1l, "Abdelmonaim", 28, "PolymathicCoder Inc.", "USA", Lists.newArrayList(23, 43, 12, 5), Lists.newArrayList("ORANGE", "BLUE"));
		final Employee employee2 = new Employee(2l, "Maximino", 32, "PolymathicCoder Inc.", "USA", Lists.newArrayList(5, 1, 66, 5), Lists.newArrayList("PINK"));
		final Employee employee3 = new Employee(3l, "Tomasz", 22, "PolymathicCoder Inc.", "Poland", Lists.newArrayList(9, 7, 666, 5), Lists.newArrayList("BLACK"));
		final Employee employee4 = new Employee(4l, "Cindy", 18, "PolymathicCoder Inc.", "UK", Lists.newArrayList(3, 1, 23, 7), Lists.newArrayList("GREEN", "CYAN"));
		final Employee employee5 = new Employee(5l, "Abdelbadia", 23, "PolymathicCoder Inc.", "Canada", Lists.newArrayList(7, 8, 67, 5), Lists.newArrayList("RED", "WHITE"));
		final Employee employee6 = new Employee(6l, "Fouad", 18, "PolymathicCoder Inc.", "Morocco", Lists.newArrayList(1, 2, 46, 90), Lists.newArrayList("GREEN", "RED"));
		final Employee employee7 = new Employee(7l, "George", 23, "PolymathicCoder Inc.", "Canada", Lists.newArrayList(9, 88, 36, 59), Lists.newArrayList("TURQUOISE", "PURPLE"));
		final Employee employee8 = new Employee(8l, "Berta", 23, "PolymathicCoder Inc.", "Germany", Lists.newArrayList(57, 9, 96, 11), Lists.newArrayList("YELLOW"));

		for(final Employee employee : Sets.newHashSet(employee1, employee2, employee3, employee4, employee5, employee6, employee7, employee8)) {
			REPOSITORY.save(employee);
		}

		// Operation
		List<Employee> actualQueryByRangeEmployees = REPOSITORY.findAllBy(
				matching(
						$("company").is(equalTo("PolymathicCoder Inc.")))
					.and(
						$("id").is(greaterThanOrEqualTo(3)))
		);

		// Test
		assertThat(actualQueryByRangeEmployees.size(), org.hamcrest.Matchers.equalTo(6));
	}

	@Test
	public void testFindAllByOnIndex() {
		// Data
		final Employee employee1 = new Employee(1l, "Abdelmonaim", 28, "PolymathicCoder Inc.", "USA", Lists.newArrayList(23, 43, 12, 5), Lists.newArrayList("ORANGE", "BLUE"));
		final Employee employee2 = new Employee(2l, "Maximino", 32, "PolymathicCoder Inc.", "USA", Lists.newArrayList(5, 1, 66, 5), Lists.newArrayList("PINK"));
		final Employee employee3 = new Employee(3l, "Tomasz", 22, "PolymathicCoder Inc.", "Poland", Lists.newArrayList(9, 7, 666, 5), Lists.newArrayList("BLACK"));
		final Employee employee4 = new Employee(4l, "Cindy", 18, "PolymathicCoder Inc.", "UK", Lists.newArrayList(3, 1, 23, 7), Lists.newArrayList("GREEN", "CYAN"));
		final Employee employee5 = new Employee(5l, "Abdelbadia", 23, "PolymathicCoder Inc.", "Canada", Lists.newArrayList(7, 8, 67, 5), Lists.newArrayList("RED", "WHITE"));
		final Employee employee6 = new Employee(6l, "Fouad", 18, "PolymathicCoder Inc.", "Morocco", Lists.newArrayList(1, 2, 46, 90), Lists.newArrayList("GREEN", "RED"));
		final Employee employee7 = new Employee(7l, "George", 23, "PolymathicCoder Inc.", "Canada", Lists.newArrayList(9, 88, 36, 59), Lists.newArrayList("TURQUOISE", "PURPLE"));
		final Employee employee8 = new Employee(8l, "Berta", 23, "PolymathicCoder Inc.", "Germany", Lists.newArrayList(57, 9, 96, 11), Lists.newArrayList("YELLOW"));

		for(final Employee employee : Sets.newHashSet(employee1, employee2, employee3, employee4, employee5, employee6, employee7, employee8)) {
			REPOSITORY.save(employee);
		}

		// Operation
		List<Employee> actualQueryByIndexResult = REPOSITORY.findAllBy(
				matching(
						$("company").is(equalTo("PolymathicCoder Inc.")))
					.and(
						$("location").is(equalTo("Canada")))
		);

		// Test
		assertThat(actualQueryByIndexResult.size(), org.hamcrest.Matchers.equalTo(2));
	}

	@Test
	public void testFindAllByOnAttribute() {
		// Data
		final Employee employee1 = new Employee(1l, "Abdelmonaim", 28, "PolymathicCoder Inc.", "USA", Lists.newArrayList(23, 43, 12, 5), Lists.newArrayList("ORANGE", "BLUE"));
		final Employee employee2 = new Employee(2l, "Maximino", 32, "PolymathicCoder Inc.", "USA", Lists.newArrayList(5, 1, 66, 5), Lists.newArrayList("PINK"));
		final Employee employee3 = new Employee(3l, "Tomasz", 22, "PolymathicCoder Inc.", "Poland", Lists.newArrayList(9, 7, 666, 5), Lists.newArrayList("BLACK"));
		final Employee employee4 = new Employee(4l, "Cindy", 18, "PolymathicCoder Inc.", "UK", Lists.newArrayList(3, 1, 23, 7), Lists.newArrayList("GREEN", "CYAN"));
		final Employee employee5 = new Employee(5l, "Abdelbadia", 23, "PolymathicCoder Inc.", "Canada", Lists.newArrayList(7, 8, 67, 5), Lists.newArrayList("RED", "WHITE"));
		final Employee employee6 = new Employee(6l, "Fouad", 18, "PolymathicCoder Inc.", "Morocco", Lists.newArrayList(1, 2, 46, 90), Lists.newArrayList("GREEN", "RED"));
		final Employee employee7 = new Employee(7l, "George", 23, "PolymathicCoder Inc.", "Canada", Lists.newArrayList(9, 88, 36, 59), Lists.newArrayList("TURQUOISE", "PURPLE"));
		final Employee employee8 = new Employee(8l, "Berta", 23, "PolymathicCoder Inc.", "Germany", Lists.newArrayList(57, 9, 96, 11), Lists.newArrayList("YELLOW"));

		for(final Employee employee : Sets.newHashSet(employee1, employee2, employee3, employee4, employee5, employee6, employee7, employee8)) {
			REPOSITORY.save(employee);
		}

		// Operation
		List<Employee> actualScanByIndexResult = REPOSITORY.findAllBy(
				matching(
						$("firstName")
								.is(beginsWith("Max"))
								.is(contains("mino")))
		);

		// Test
		assertThat(actualScanByIndexResult.size(), org.hamcrest.Matchers.equalTo(1));
	}

	@AfterClass
	public static void teardown() throws Exception {
		//MOCKED_DYNAMODB.stop();
	}
}
