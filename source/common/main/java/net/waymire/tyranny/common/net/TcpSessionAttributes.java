package net.waymire.tyranny.common.net;

public interface TcpSessionAttributes
{
	public static final String GUID = "GUID";
	
	public static final String SESSION_STATE = "sessionState";
	public static final String CLIENT_INFO = "clientInformation";
	public static final String CHAP = "chap";
	
	public static final String LAST_PING_TX_SEQ = "lastPingTxSeq";
	public static final String LAST_PING_TX_TIME = "lastPingTxTime";
	public static final String LAST_PONG_RX_SEQ = "lastPongRxSeq";
	public static final String LAST_PONG_RX_TIME = "lastPongRxTime";
		
	public static final String LAST_PING_RX_SEQ = "lastPingRxSeq";
	public static final String LAST_PING_RX_TIME = "lastPingRxTime";
	public static final String LAST_PONG_TX_SEQ = "lastPongTxSeq";
	public static final String LAST_PONG_TX_TIME = "lastPongTxTime";
	
	public static final String DISCONNECT_TASK = "disconnectTask";
	public static final String DISCONNECT_FUTURE = "disconnect.future";
	public static final String PING_FUTURE = "ping.future";
	public static final String CLOCK_SYNC_FUTURE = "clock.sync.future";
}
