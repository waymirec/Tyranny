package net.waymire.tyranny.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

/**
 * Marker annotation to indicate that calls to a method require and update of the hashCode value
 */
public @interface HashModifier {

}
