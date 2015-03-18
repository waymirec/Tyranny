package net.waymire.tyranny.worldserver.message;

public interface MessageTopics 
{
	public static final String AUTHCONTROL_CLIENT_AUTHENTICATING = "authcontrol.client.authenticating";
	public static final String AUTHCONTROL_CLIENT_AUTH_SUCCESS = "authcontrol.client.auth.success";
	public static final String AUTHCONTROL_CLIENT_AUTH_FAILED = "authcontrol.client.auth.failed";
	
	public static final String AUTHCONTROL_CLIENT_CONNECTING = "authcontrol.client.connecting";
	public static final String AUTHCONTROL_CLIENT_DISCONNECTING = "authcontrol.client.disconnecting";
	public static final String AUTHCONTROL_CLIENT_CONNECTED = "authcontrol.client.connected";
	public static final String AUTHCONTROL_CLIENT_CONNECTION_FAILED = "authcontrol.client.connection.failed";
	public static final String AUTHCONTROL_CLIENT_DISCONNECTED = "authcontrol.client.disconnected";
	public static final String AUTHCONTROL_CLIENT_SESSION_IDLE = "authcontrol.client.session.idle";

	public static final String WORLDSERVER_STARTING = "worldserver.starting";
	public static final String WORLDSERVER_STARTED = "worldserver.started";
	public static final String WORLDSERVER_STOPPING = "worldserver.stopping";
	public static final String WORLDSERVER_STOPPED = "worldserver.stopped";
	public static final String WORLDSERVER_CLIENT_CONNECTED = "worldserver.client.connected";
	public static final String WORLDSERVER_CLIENT_DISCONNECTED = "worldserver.client.disconnected";
	
	public static final String SYSTEM_STARTING = "system.starting";
	public static final String SYSTEM_STARTED = "system.started";
	public static final String SYSTEM_STOPPING = "system.stopping";
	public static final String SYSTEM_STOPPED = "system.stopped";

}
