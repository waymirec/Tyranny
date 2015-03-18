package net.waymire.tyranny.common.net;

public interface IpConstants {
	public static final int DEFAULT_RCV_BUFFER_SIZE = 4096;
	public static final int DEFAULT_SEND_BUFFER_SIZE = 4096;
	public static final int DEFAULT_KEEPALIVE_REQUEST_INTERVAL = 2;
	public static final int DEFAULT_KEEPALIVE_REQUEST_TIMEOUT = 1;
	public static final int DEFAULT_IDLE_TIME = 6;
	public static final int DEFAULT_CONNECT_TIMEOUT = 10 * 1000;
	
	public static final String PROP_SOCKET_LISTEN_ADDRESS = "socket.listen.address";
	public static final String PROP_SOCKET_LISTEN_PORT = "socket.listen.port";
	public static final String PROP_SOCKET_CONNECT_ADDRESS = "socket.connect.address";
	public static final String PROP_SOCKET_CONNECT_PORT = "socket.connect.port";
	public static final String PROP_SOCKET_RCV_BUFFER="socket.buffer.receive";
	public static final String PROP_SOCKET_SEND_BUFFER = "socket.buffer.send";
	public static final String PROP_SOCKET_TCPNODELAY = "socket.tcpnodelay";
	public static final String PROP_SOCKET_IDLETIME = "socket.idletime";
	public static final String PROP_SOCKET_CODEC = "socket.codec";
	
	public static final String PROP_SSL_DIR = "ssl.dir";
	public static final String PROP_SSL_KEYSTORE = "ssl.keystore";
	public static final String PROP_SSL_TRUSTSTORE = "ssl.truststore";
	public static final String PROP_SSL_PASSWORD = "ssl.password";
	
	public static final String SSL_KEYSTORE_FILENAME = "tyranny_keystore.jks";
	public static final String SSL_TRUSTSTORE_FILENAME = "tyranny_truststore.jks";
	public static final String SSL_STORE_PASSWORD = "password";
	
	public static final String SESSION_ID = "SessionID";
}
