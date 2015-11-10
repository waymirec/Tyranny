package net.waymire.tyranny.worldserver.protocol.processor;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.GUID;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.StandardMessage;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionAttributes;
import net.waymire.tyranny.common.protocol.WorldserverIdentResult;
import net.waymire.tyranny.common.protocol.WorldserverOpcode;
import net.waymire.tyranny.common.protocol.WorldserverPacket;
import net.waymire.tyranny.common.protocol.WorldserverProtocolProcessor;
import net.waymire.tyranny.common.task.TaskFuture;
import net.waymire.tyranny.common.task.TaskManager;
import net.waymire.tyranny.worldserver.PlayerIdentManager;
import net.waymire.tyranny.worldserver.PlayerIdentToken;
import net.waymire.tyranny.worldserver.message.MessageProperties;
import net.waymire.tyranny.worldserver.message.MessageTopics;
import net.waymire.tyranny.worldserver.net.WorldserverSessionAttributes;

public class WorldserverAuthProcessors 
{
	@WorldserverProtocolProcessor(opcode=WorldserverOpcode.IDENT)
	private static void handlePlayerIdent(TcpSession session, WorldserverPacket packet)
	{
		// The identification process is complete. Cancel the pending disconnect task.
		TaskFuture disconnectFuture = (TaskFuture)session.getAttribute(TcpSessionAttributes.DISCONNECT_FUTURE);
		AppRegistry.getInstance().retrieve(TaskManager.class).cancel(disconnectFuture);
					
		GUID tokenId = GUID.generate(packet.getLong(),packet.getLong());
		InetAddress remoteAddress = ((InetSocketAddress)session.getRemoteAddress()).getAddress();
		PlayerIdentToken token = AppRegistry.getInstance().retrieve(PlayerIdentManager.class).getPlayerIdentToken(tokenId);

		if(token == null)
		{			
			WorldserverPacket response = new WorldserverPacket(WorldserverOpcode.IDENT_RESULT);
			response.putShort(WorldserverIdentResult.INVALID_TOKEN.shortValue());
			response.prepare();
			session.send(packet);
			session.close();
			
			Message identFailed = new StandardMessage(session, MessageTopics.WORLDSERVER_CLIENT_IDENT_FAILED);
			identFailed.setProperty(MessageProperties.CLIENT_TOKEN, token);
			identFailed.setProperty(MessageProperties.CLIENT_ADDRESS, remoteAddress);
			AppRegistry.getInstance().retrieve(MessageManager.class).publish(identFailed);
		}
		
		session.setAuthenticated(true);
		session.setAttribute(WorldserverSessionAttributes.PLAYER_ACCT_ID, token.getAccountId());
		session.setAttribute(WorldserverSessionAttributes.PLAYER_AUTH_TOKEN, token);

		WorldserverPacket response = new WorldserverPacket(WorldserverOpcode.IDENT_RESULT);
		response.putShort(WorldserverIdentResult.VALID_TOKEN.shortValue());
		response.prepare();
		session.send(response);
		
		Message identSuccess = new StandardMessage(session, MessageTopics.WORLDSERVER_CLIENT_IDENT_SUCCESS);
		identSuccess.setProperty(MessageProperties.CLIENT_TOKEN, token);
		identSuccess.setProperty(MessageProperties.CLIENT_ADDRESS, remoteAddress);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(identSuccess);
	}
}
