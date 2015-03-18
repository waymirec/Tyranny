package net.waymire.tyranny.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.FIELD})

/**
 * Marker annotation to indicate that a member/method should be used for hashCode generation
 */
public @interface HashContributor {

}
