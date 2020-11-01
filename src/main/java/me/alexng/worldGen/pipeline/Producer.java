package me.alexng.worldGen.pipeline;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Producer {

	/**
	 * The name of the data being produced.
	 */
	String name();

	/**
	 * Returns true if the data should be stored in memory after being produced.
	 */
	boolean stored() default false;
}
