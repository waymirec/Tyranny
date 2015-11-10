package net.waymire.tyranny.client.protocol.processor;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import net.waymire.tyranny.client.message.MessageProperties;
import net.waymire.tyranny.client.message.MessageTopics;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.GUID;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.StandardMessage;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionAttributes;
import net.waymire.tyranny.common.protocol.LoginserverOpcode;
import net.waymire.tyranny.common.protocol.LoginserverPacket;
import net.waymire.tyranny.common.protocol.LoginserverProtocolProcessor;
import net.waymire.tyranny.common.util.InetAddressUtil;

public class LoginserverProcessors 
{
	@LoginserverProtocolProcessor(opcode=LoginserverOpcode.PING)
	private static void handlePing(TcpSession session, LoginserverPacket packet)
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
	private static void handlePong(TcpSession session, LoginserverPacket packet)
	{
		//Long lastPingSeq = (Long)session.getAttribute(TcpSessionAttributes.LAST_PING_TX_SEQ);
		Long pongSeq = packet.getLong();
		session.setAttribute(TcpSessionAttributes.LAST_PONG_RX_SEQ,pongSeq);
		session.setAttribute(TcpSessionAttributes.LAST_PONG_RX_TIME,System.currentTimeMillis());
	}
	
	@LoginserverProtocolProcessor(opcode=LoginserverOpcode.WORLD_INFO)
	private static void handleSelectWorld(TcpSession session, LoginserverPacket packet)
	{
		GUID token = GUID.generate(packet.getLong(),packet.getLong());
		InetAddress address = InetAddressUtil.long2Inet(packet.getLong());
		int port = packet.getInt();
		
		Message connectWorld = new StandardMessage(session, MessageTopics.WORLDSERVER_CLIENT_CONNECT);
		connectWorld.setProperty(MessageProperties.WORLDSERVER_AUTH_TOKEN, token);
		connectWorld.setProperty(MessageProperties.WORLDSERVER_SERVER_ADDRESS, new InetSocketAddress(address,port));
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(connectWorld);
	}
	
	private LoginserverProcessors() { }
}
