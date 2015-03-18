package net.waymire.tyranny.authserver.protocol.processor;

import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionAttributes;
import net.waymire.tyranny.common.protocol.LoginserverOpcode;
import net.waymire.tyranny.common.protocol.LoginserverPacket;
import net.waymire.tyranny.common.protocol.LoginserverProtocolProcessor;

public class LoginserverProcessors 
{
	private LoginserverProcessors() { }
	
		
	@LoginserverProtocolProcessor(opcode=LoginserverOpcode.PING)
	private static void handlePing(TcpSession session,LoginserverPacket packet)
	{
		Long pingSeq = packet.getLong();
		session.setAttribute(TcpSessionAttributes.LAST_PING_RX_SEQ,pingSeq);
		session.setAttribute(TcpSessionAttributes.LAST_PING_RX_TIME,System.currentTimeMillis());
			
		LoginserverPacket pong = new LoginserverPacket(LoginserverOpcode.PONG);
		pong.putLong(pingSeq);
		pong.prepare();
		session.send(pong);
		session.setAttribute(TcpSessionAttributes.LAST_PONG_TX_SEQ,pingSeq);
		session.setAttribute(TcpSessionAttributes.LAST_PONG_TX_TIME,System.currentTimeMillis());
	}
	
	@LoginserverProtocolProcessor(opcode=LoginserverOpcode.PONG)
	private static void handlePong(TcpSession session,LoginserverPacket packet)
	{
		//Long lastPingSeq = (Long)session.getAttribute(TcpSessionAttributes.LAST_PING_TX_SEQ);
		Long pongSeq = packet.getLong();
			
		session.setAttribute(TcpSessionAttributes.LAST_PONG_RX_SEQ,pongSeq);
		session.setAttribute(TcpSessionAttributes.LAST_PONG_RX_TIME,System.currentTimeMillis());
	}
}