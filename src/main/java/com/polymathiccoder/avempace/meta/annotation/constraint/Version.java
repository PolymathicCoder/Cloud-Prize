package com.polymathiccoder.avempace.meta.annotation.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang3.StringUtils;

import com.polymathiccoder.avempace.meta.annotation.PersistAsType;

@ConstrainedAttribute
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Version {
	String name() default StringUtils.EMPTY;
	PersistAsType persistAsType() default PersistAsType.STRING;
}
