package net.waymire.tyranny.common.dynload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
/**
 * Classes that bear this annotation will be automatically loaded
 * by the system at context startup. The provided <code>priority</code>
 * is used to determine the order that classes are loaded.
 * 
 * In addition to loading, or instantiating, classes can be automatically
 * initialized through the application of the AutoInitializable interface 
 * as well as automatically started and stopped with the AutoStartable
 * interface.
 * 
 * @author Chris Waymire <cwaymire@apriva.com>
 *
 */
public @interface Autoload
{
	public int priority() default Integer.MAX_VALUE;
}
