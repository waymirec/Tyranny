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
	
	public static final String LOGINSERVER_CLIENT_LOGIN_REQUEST = "loginserver.client.login.request";
	
	public static final String SYSTEM_STARTING = "system.starting";
	public static final String SYSTEM_STARTED = "system.started";
	public static final String SYSTEM_STOPPING = "system.stopping";
	public static final String SYSTEM_STOPPED = "system.stopped";

	public static final String WORLDSERVER_CLIENT_IDENT_SUCCESS = "worldserver.client.ident.success";
	public static final String WORLDSERVER_CLIENT_IDENT_FAILED = " worldserver.client.ident.failed";

	public static final String WORLDSERVER_CLIENT_CONNECT = "worldserver.client.connect";
	public static final String WORLDSERVER_CLIENT_CONNECTING = "worldserver.client.connecting";
	public static final String WORLDSERVER_CLIENT_CONNECT_SUCCESS = "worldserver.client.connect.success";
	public static final String WORLDSERVER_CLIENT_CONNECT_FAILED = "worldserver.client.connect.failed";
	public static final String WORLDSERVER_CLIENT_DISCONNECTING = "worldserver.client.disconnecting";
	public static final String WORLDSERVER_CLIENT_DISCONNECTED = "worldserver.client.disconnected";
	
	public static final String WORLDSERVER_CLIENT_SESSION_IDLE = "worldserver.client.session.idle";
	
	public static final String CHARACTER_SELECTION = "character.selection";
	public static final String ENTER_WORLD = "enter.world";
	
	public static final String PLAYER_INPUT_MOVE = "player.input.move";
	public static final String PLAYER_INPUT_ACTION = "player.input.action";
}
