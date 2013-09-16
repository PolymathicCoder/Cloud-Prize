package com.polymathiccoder.avempace.meta.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE })
@Documented
public @interface Table {
	static final long DEFAULT_READ_CAPACITY_UNITS = 5l;
	static final long DEFAULT_WRITE_CAPACITY_UNITS = 5l;

	String name();
	long readCapacityUnits() default Table.DEFAULT_READ_CAPACITY_UNITS;
	long writeCapacityUnits()  default Table.DEFAULT_WRITE_CAPACITY_UNITS;
}
