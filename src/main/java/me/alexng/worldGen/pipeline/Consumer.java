package me.alexng.worldGen.pipeline;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Consumer {

	/**
	 * The name of the data being consumed
	 */
	String name();

	/**
	 * Returns true if the data must all be precalculated for this consumer.
	 */
	boolean blocked() default false;
}
