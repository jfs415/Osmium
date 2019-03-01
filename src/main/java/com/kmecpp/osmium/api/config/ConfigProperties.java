package com.kmecpp.osmium.api.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigProperties {

	String path();

	//	String extension() default "";

	String header() default "";

	boolean allowKeyRemoval() default false;

	boolean loadLate() default false;

}
