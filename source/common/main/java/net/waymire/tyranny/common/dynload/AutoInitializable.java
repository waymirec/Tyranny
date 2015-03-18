package net.waymire.tyranny.common.dynload;

/**
 * The AutoInitializable interface is a marker interface that identifies a class as needing
 * to be initialized. This is used in conjunction with the Autoload annotation to initialize
 * classes prior to starting them.
 * 
 * @author Chris Waymire <cwaymire@apriva.com>
 *
 */
public interface AutoInitializable
{
	/**
	 * Perform initialization of this instance.
	 */
	public void autoInitialize();
	/**
	 * De-initialize this instance.
	 */
	public void autoDeinitialize();
}
