package net.waymire.tyranny.common.delegate;

import java.lang.reflect.Method;

/**
 * Interface defining a contract for a general purpose delegate
 * @author Chris Waymire
 *
 */
public interface Delegate {
	/**
	 * General purpose delegate callback method.
	 */
	public Object invoke(Object... args);
	public Object getTarget();
	public Method getMethod();
}
