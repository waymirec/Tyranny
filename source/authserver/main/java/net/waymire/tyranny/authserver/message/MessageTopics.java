package net.waymire.tyranny.authserver.message;

public interface MessageTopics 
{
	public static final String AUTHCONTROL_SERVER_STARTING = "authcontrol.server.starting";
	public static final String AUTHCONTROL_SERVER_STARTED = "authcontrol.server.started";
	public static final String AUTHCONTROL_SERVER_STOPPING = "authcontrol.server.stopping";
	public static final String AUTHCONTROL_SERVER_STOPPED = "authcontrol.server.stopped";
	public static final String AUTHCONTROL_SERVER_CLIENT_CONNECTED = "authcontrol.server.client.connected";
	public static final String AUTHCONTROL_SERVER_CLIENT_DISCONNECTED = "authcontrol.server.client.disconnected";
	public static final String AUTHCONTROL_SERVER_CLIENT_AUTHENTICATED = "authcontrol.client.authenticated";

	public static final String LOGINSERVER_STARTING = "loginserver.starting";
	public static final String LOGINSERVER_STARTED = "loginserver.started";
	public static final String LOGINSERVER_STOPPING = "loginserver.stopping";
	public static final String LOGINSERVER_STOPPED = "loginserver.stopped";
	public static final String LOGINSERVER_CLIENT_CONNECTED = "loginserver.client.connected";
	public static final String LOGINSERVER_CLIENT_DISCONNECTED = "loginserver.client.disconnected";
	public static final String LOGINSERVER_CLIENT_AUTHENTICATED = "loginserver.client.authenticated";

	public static final String SYSTEM_STARTING = "system.starting";
	public static final String SYSTEM_STARTED = "system.started";
	public static final String SYSTEM_STOPPING = "system.stopping";
	public static final String SYSTEM_STOPPED = "system.stopped";

	public static final String WORLD_AVAILABLE = "world.available";
	public static final String WORLD_UNAVAILABLE = "world.unavailable";
}
