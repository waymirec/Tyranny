package net.waymire.tyranny.client.protocol.processor;

import net.waymire.tyranny.client.message.MessageTopics;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.MessagePriority;
import net.waymire.tyranny.common.message.StandardMessage;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionAttributes;
import net.waymire.tyranny.common.protocol.WorldserverOpcode;
import net.waymire.tyranny.common.protocol.WorldserverPacket;
import net.waymire.tyranny.common.protocol.WorldserverProtocolProcessor;

public class WorldserverProcessors 
{
	@WorldserverProtocolProcessor(opcode=WorldserverOpcode.PING)
	private static void handlePing(TcpSession session, WorldserverPacket packet)
	{
		Long pingSeq = packet.getLong();
		session.setAttribute(TcpSessionAttributes.LAST_PING_RX_SEQ,pingSeq);
		session.setAttribute(TcpSessionAttributes.LAST_PING_RX_TIME,System.currentTimeMillis());

		WorldserverPacket pong = new WorldserverPacket(WorldserverOpcode.PONG);
		pong.putLong(pingSeq);
		pong.prepare();
		session.send(pong);
		session.setAttribute(TcpSessionAttributes.LAST_PONG_TX_SEQ,pingSeq);
		session.setAttribute(TcpSessionAttributes.LAST_PONG_TX_TIME,System.currentTimeMillis());
	}
	
	@WorldserverProtocolProcessor(opcode=WorldserverOpcode.PONG)
	private static void handlePong(TcpSession session, WorldserverPacket packet)
	{
		//Long lastPingSeq = (Long)session.getAttribute(TcpSessionAttributes.LAST_PING_TX_SEQ);
		Long pongSeq = packet.getLong();
		session.setAttribute(TcpSessionAttributes.LAST_PONG_RX_SEQ,pongSeq);
		session.setAttribute(TcpSessionAttributes.LAST_PONG_RX_TIME,System.currentTimeMillis());
	}
	
	@WorldserverProtocolProcessor(opcode=WorldserverOpcode.ENTER_WORLD)
	private static void handleEnterWorld(TcpSession session, WorldserverPacket packet)
	{
		Message enterWorld = new StandardMessage(session, MessageTopics.ENTER_WORLD);
		enterWorld.setPriority(MessagePriority.URGENT);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(enterWorld);
	}
}
