package net.waymire.tyranny.common.dynload;

/**
 * The AutoStartable interface is used with the Autoload annotation and identifies the class
 * as needing to be automatically started, and stopped, by the system after it is loaded.
 * The automatic starting takes place after initialization.
 * 
 * @author Chris Waymire <cwaymire@apriva.com>
 *
 */
public interface AutoStartable
{
	public void autoStart();
	public void autoStop();
}
