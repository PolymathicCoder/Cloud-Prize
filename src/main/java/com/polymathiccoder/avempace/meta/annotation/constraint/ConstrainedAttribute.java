package com.polymathiccoder.avempace.meta.annotation.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.polymathiccoder.avempace.meta.annotation.Attribute;

@Attribute
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface ConstrainedAttribute {
}
