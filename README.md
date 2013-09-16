Avempace
========
### This project is only a little over a week old and still has lots of rough-edges. It still needs lots of test coverage. Use at your own risk.

__Avempace__ is POJO-based Java framework that extends and hides the complexity the AWS DynamoDB API. The motivation behind it is to enable and trivilize advanced cloud features like cross-region support, distributed transaction supporting, batching, etc…

## Features

* __Annotation-Driven Mapping:__ Easily define mapping between PJOs and DynamoDB tables through a set of annotations.
* __Automatic schema generation__
* __Repository Interface:__ Providing a simple and statically-typed interface on your POJOs to support CRUD operations.
* __Qureying Crireria API:__ A simple and concise DSL.
* __Intelligent Qureying:__ No need to know what DynamoDB operation you should use. Let the framework decide the best way for you. It will query by range, by LSI, or fall back to a simple scan.
* __Multi-Region Support:__ cross-region propagation and location-aware querying. the framework will persist your entities across tables in multiple-regeion and will always query data from the closest region to your location using your public IP address.
* __Automatic Data Serialization:__ Complex type are automatically serialized/deserialized to and from JSON and Avro binary.

### Upcomming Features (In order & real soon)

* Much-needed error checking and better validation
* Pagination
* Conditional Writes
* Optimistic locking support
* Intelligent batching
* Plugable Caching
* Transaction Support
* A SQL-like Query Language
* Advice and Performnce Recommendations



#Usage and Sample Program
->__PLEASE HARD-CODE VALID AWS CREDENTIALS__<-

->__IN THE FILE__<-

->__com.polymathiccoder.avempace.config.AvempaceModule.java__<-

->__BEFORE COMPILING__<-


__Considered the following annotated POJO:__

The entity of type 'Employee' will be stored in 3 tables in 3 differet regions named 'tbl_employee' and will be assigned a read capacity of 10 units. All writes will affect all 3 tables and the reads will be from the table in closest region to the resolved location of the users public IP. Other details can be easily infered from the following code snippet.


```
package com.polymathiccoder.avempace;

import java.util.List;

import com.polymathiccoder.avempace.config.Region;
import com.polymathiccoder.avempace.meta.annotation.Attribute;
import com.polymathiccoder.avempace.meta.annotation.Entity;
import com.polymathiccoder.avempace.meta.annotation.PersistAsType;
import com.polymathiccoder.avempace.meta.annotation.Table;
import com.polymathiccoder.avempace.meta.annotation.constraint.LSI;
import com.polymathiccoder.avempace.meta.annotation.constraint.PrimaryHashKey;
import com.polymathiccoder.avempace.meta.annotation.constraint.PrimaryRangeKey;

@Entity(
	primaryRegion = Region.SA_EAST_1, 
	secondaryRegions = {Region.AP_NORTHEAST_1, Region.US_WEST_2})
@Table(
	name = "tbl_employee", 
	readCapacityUnits = 10)
public class Employee {

	@PrimaryRangeKey(persistAsType = PersistAsType.NUMBER)
	private long id;

	@PrimaryHashKey
	private String company;

	@Attribute(name = "first_name")
	private String firstName;

	@Attribute(name = "age", persistAsType = PersistAsType.NUMBER)
	private int age;

	@LSI(indexName = "idx_location")
	private String location;

	@Attribute(persistAsType = PersistAsType.NUMBER_SET)
	private List<Integer> favoriteNumbers;

	@Attribute(persistAsType = PersistAsType.STRING_SET)
	private List<String> favoriteColors;

	public Employee() {
	}

	public Employee(final long id, final String firstName, final int age, final String company, final String location, final List<Integer> favoriteNumbers, final List<String> favoriteColors) {
		this.id = id;
		this.firstName = firstName;
		this.age = age;
		this.company = company;
		this.location = location;
		this.favoriteNumbers = favoriteNumbers;
		this.favoriteColors = favoriteColors;
	}
}
```

__Creating a reopository and unlocking the functionality is a one-liner:__

```
Repository<Employee> repository = Avempace.getRepositoryFactory().createRepository(Employee.class);
```
__Saving a new employee:__

```
Employee employee = new Employee(1l, "Abdelmonaim", 28, "PolymathicCoder Inc.", "USA", Lists.newArrayList(23, 43, 12, 5), Lists.newArrayList("ORANGE", "BLUE");

repository.save(employee);
```
__Deleting a specific employee:__

```
repository.remove(employee);
```
__Finding a specific employee using the Criteria API:__

To find the employee whose 'company' is equal to "PolymathicCoder Inc." and 'id' is equal to 3

```
repository.find(
				matching(
						$("company").is(equalTo("PolymathicCoder Inc.")))
					.and(
						$("id").is(equalTo(3)))
);
```
__Updating a specific employee using the Criteria/Operations API:__

On the employee whose 'company' is equal to "PolymathicCoder Inc." and 'id' is equal to 3 change the 'location' to "United States of America" and 'firstName' to "Abdel"

```
repository.update(
				matching(
						$("company").is(equalTo("PolymathicCoder Inc.")))
					.and(
						$("id").is(equalTo(1l))),
				apply(
						change("location").withValue("United States of America"))
					.and(
						change("firstName").withValue("Abdel"))
```
__Finding employees using the Criteria API:__
The call to 'findAllBy' is executed differently depending on the selected criteria.

Since 'id' is a primary key range. The following will be exexcuted by the framework as a query.

```
List<Employee> employees1 = repository.findAllBy(
				matching(
						$("location").is(equalTo("PolymathicCoder Inc.")))
					.and(
						$("id").is(greaterThanOrEqualTo(3))));
```
Since 'location' is indexed. The following will be exexcuted by the framework as a query to the index named 'idx_location' (Refer to the mapping).

```
List<Employee> employees2 = repository.findAllBy(
				matching(
						$("company").is(equalTo("PolymathicCoder Inc.")))
					.and(
						$("location").is(equalTo("Canada"))));
```
The following will be exexcuted as a regluar full table scan.

```
List<Employee> employees3 = repository.findAllBy(
				matching(
						$("firstName")
								.is(beginsWith("Max"))
								.is(contains("mino")))
		);
```
