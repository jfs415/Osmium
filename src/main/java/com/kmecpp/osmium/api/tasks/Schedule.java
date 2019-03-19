package com.kmecpp.osmium.api.tasks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Schedule {

	int interval();

	int delay() default 0;

	boolean async() default false;

	TimeUnit unit() default TimeUnit.TICK;

}
