package net.waymire.tyranny.common.net;

public interface IpSessionAttributes {
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
	
}
