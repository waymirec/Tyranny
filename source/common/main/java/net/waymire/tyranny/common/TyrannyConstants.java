package net.waymire.tyranny.common;

import java.text.MessageFormat;

final public class TyrannyConstants 
{
	private TyrannyConstants() { }
	
	public static final int AUTH_LOGINSERVER_VERSION_MAJOR = 0;
	public static final int AUTH_LOGINSERVER_VERSION_MINOR = 0;
	public static final int AUTH_LOGINSERVER_VERSION_MAINT = 1;	
	public static final String AUTH_LOGINSERVER_VERSION = MessageFormat.format("{0}.{1}.{2}",AUTH_LOGINSERVER_VERSION_MAJOR,AUTH_LOGINSERVER_VERSION_MINOR,AUTH_LOGINSERVER_VERSION_MAINT);	
	
	public static final int AUTH_CONTROLSERVER_VERSION_MAJOR = 0;
	public static final int AUTH_CONTROLSERVER_VERSION_MINOR = 0;
	public static final int AUTH_CONTROLSERVER_VERSION_MAINT = 0;
	public static final String AUTH_CONTROLSERVER_VERSION = MessageFormat.format("{0}.{1}.{2}",AUTH_CONTROLSERVER_VERSION_MAJOR,AUTH_CONTROLSERVER_VERSION_MINOR,AUTH_CONTROLSERVER_VERSION_MAINT);

	public static final String AUTH_SERVER_CONFIG = "authserver.cfg";
	public static final String WORLD_SERVER_CONFIG = "worldserver.cfg";
}
