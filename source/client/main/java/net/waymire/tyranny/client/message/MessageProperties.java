package net.waymire.tyranny.client.message;

public interface MessageProperties 
{
	public static final String ACTION = "action";

	public static final String LOGINSERVER_AUTH_CHALLENGE_BYTES = "loginserver.auth.challenge.bytes";
	public static final String LOGINSERVER_AUTH_FAIL_REASON = "loginserver.auth.fail.reason";
	public static final String LOGINSERVER_AUTH_FAIL_DETAILS = "loginserver.auth.fail.details";
	public static final String LOGINSERVER_AUTH_RESPONSE = "loginserver.auth.response";
	public static final String LOGINSERVER_SERVER_ADDRESS = "loginserver.server.address";
	
	public static final String LOGINSERVER_LOGIN_USERNAME = "loginserver.login.username";
	public static final String LOGINSERVER_LOGIN_PASSWORD = "loginserver.login.password";
	
	public static final String REQUEST = "request";	
	
	public static final String STATUS = "status";
	
	public static final String UUID = "uuid";
	
	public static final String WORLDSERVER_SERVER_ADDRESS = "worldserver.server.address";
	public static final String WORLDSERVER_AUTH_TOKEN = "worldserver.client.auth.token";
}
