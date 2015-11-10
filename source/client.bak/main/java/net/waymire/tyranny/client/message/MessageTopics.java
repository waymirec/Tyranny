package net.waymire.tyranny.client.message;

public interface MessageTopics 
{
	public static final String LOGINSERVER_AUTH_CHALLENGE = "loginserver.auth.challenge";
	public static final String LOGINSERVER_AUTH_RESPONSE = "loginserver.auth.response";
	
	public static final String LOGINSERVER_CLIENT_AUTHENTICATING = "loginserver.client.authenticating";
	public static final String LOGINSERVER_CLIENT_AUTH_SUCCESS = "loginserver.client.auth.success";
	public static final String LOGINSERVER_CLIENT_AUTH_FAILED = "loginserver.client.auth.failed";
	
	public static final String LOGINSERVER_CLIENT_CONNECTING = "loginserver.client.connecting";
	public static final String LOGINSERVER_CLIENT_CONNECT_SUCCESS = "loginserver.client.connect.success";
	public static final String LOGINSERVER_CLIENT_CONNECT_FAILED = "loginserver.client.connect.failed";
	public static final String LOGINSERVER_CLIENT_DISCONNECTING = "loginserver.client.disconnecting";
	public static final String LOGINSERVER_CLIENT_DISCONNECTED = "loginserver.client.disconnected";
	
	public static final String LOGINSERVER_CLIENT_SESSION_IDLE = "loginserver.client.session.idle";
	
	public static final String LOGINSERVER_CLIENT_STARTING = "loginserver.client.starting";
	public static final String LOGINSERVER_CLIENT_STARTED = "loginserver.client.started";
	public static final String LOGINSERVER_CLIENT_STOPPING = "loginserver.client.stopping";
	public static final String LOGINSERVER_CLIENT_STOPPED = "loginserver.client.stopped";

	public static final String LOGINSERVER_CLIENT_LOGIN_REQUEST = "loginserver.client.login.request";
	
	public static final String SYSTEM_STARTING = "system.starting";
	public static final String SYSTEM_STARTED = "system.started";
	public static final String SYSTEM_STOPPING = "system.stopping";
	public static final String SYSTEM_STOPPED = "system.stopped";

}
