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

@Entity(primaryRegion = Region.SA_EAST_1, secondaryRegions = {Region.AP_NORTHEAST_1, Region.US_WEST_2})
@Table(name = "tbl_employee", readCapacityUnits = 10)
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

	public long getId() { return id; }

	public String getCompany() {
		return company;
	}

	public String getFirstName() {
		return firstName;
	}

	public int getAge() {
		return age;
	}

	public String getLocation() {
		return location;
	}

	public List<Integer> getFavoriteNumbers() {
		return favoriteNumbers;
	}

	public List<String> getFavoriteColors() {
		return favoriteColors;
	}
}
