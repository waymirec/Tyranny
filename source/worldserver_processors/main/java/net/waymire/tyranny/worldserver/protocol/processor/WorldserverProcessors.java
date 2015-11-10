package net.waymire.tyranny.worldserver.protocol.processor;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.GUID;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.StandardMessage;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionAttributes;
import net.waymire.tyranny.common.protocol.WorldserverOpcode;
import net.waymire.tyranny.common.protocol.WorldserverPacket;
import net.waymire.tyranny.common.protocol.WorldserverProtocolProcessor;
import net.waymire.tyranny.worldserver.message.MessageProperties;
import net.waymire.tyranny.worldserver.message.MessageTopics;
import net.waymire.tyranny.worldserver.net.WorldserverSessionAttributes;

public class WorldserverProcessors 
{
	@WorldserverProtocolProcessor(opcode=WorldserverOpcode.PING)
	private static void handlePing(TcpSession session,WorldserverPacket packet)
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
	private static void handlePong(TcpSession session,WorldserverPacket packet)
	{
		//Long lastPingSeq = (Long)session.getAttribute(TcpSessionAttributes.LAST_PING_TX_SEQ);
		Long pongSeq = packet.getLong();
			
		session.setAttribute(TcpSessionAttributes.LAST_PONG_RX_SEQ,pongSeq);
		session.setAttribute(TcpSessionAttributes.LAST_PONG_RX_TIME,System.currentTimeMillis());
	}
	
	@WorldserverProtocolProcessor(opcode=WorldserverOpcode.CHAR_LIST_REQ)
	private static void handleCharacterListRequest(TcpSession session, WorldserverPacket packet)
	{
		WorldserverPacket response = new WorldserverPacket(WorldserverOpcode.CHAR_LIST);
		response.put((byte)0);
		response.prepare();
		session.send(response);
	}
	
	@WorldserverProtocolProcessor(opcode=WorldserverOpcode.CHAR_ADD)
	private static void handleCharacterAdd(TcpSession session, WorldserverPacket packet)
	{
		WorldserverPacket response = new WorldserverPacket(WorldserverOpcode.CHAR_ADD_ACK);
		response.prepare();
		session.send(response);
	}
	
	@WorldserverProtocolProcessor(opcode=WorldserverOpcode.CHAR_DEL)
	private static void handleCharacterDelete(TcpSession session, WorldserverPacket packet)
	{
		WorldserverPacket response = new WorldserverPacket(WorldserverOpcode.CHAR_DEL_ACK);
		response.prepare();
		session.send(response);
	}
	
	@WorldserverProtocolProcessor(opcode=WorldserverOpcode.ENTER_WORLD_REQ)
	private static void handleEnterWorldRequest(TcpSession session, WorldserverPacket packet)
	{
		GUID characterId = GUID.generate(packet.getLong(),packet.getLong());
		session.setAttribute(WorldserverSessionAttributes.PLAYER_CHAR_ID, characterId);
		WorldserverPacket response = new WorldserverPacket(WorldserverOpcode.ENTER_WORLD);
		response.prepare();
		session.send(response);
		
		GUID accountId = (GUID)session.getAttribute(WorldserverSessionAttributes.PLAYER_ACCT_ID);
		Message enterWorldMessage = new StandardMessage(session, MessageTopics.PLAYER_ENTER_WORLD);
		enterWorldMessage.setProperty(MessageProperties.PLAYER_ACCT_ID, accountId);
		enterWorldMessage.setProperty(MessageProperties.PLAYER_CHAR_ID, characterId);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(enterWorldMessage);
	}
}
