package net.waymire.tyranny.client.protocol.processor;

import net.waymire.tyranny.client.message.MessageTopics;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.StandardMessage;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.protocol.WorldserverIdentResult;
import net.waymire.tyranny.common.protocol.WorldserverOpcode;
import net.waymire.tyranny.common.protocol.WorldserverPacket;
import net.waymire.tyranny.common.protocol.WorldserverProtocolProcessor;

public class WorldserverAuthProcessors 
{

	@WorldserverProtocolProcessor(opcode=WorldserverOpcode.IDENT_RESULT)
	private static void handleIdentResult(TcpSession session, WorldserverPacket packet)
	{
		WorldserverIdentResult result = WorldserverIdentResult.valueOf(packet.getShort());
		switch(result)
		{
			case VALID_TOKEN:
			{
				Message success = new StandardMessage(session, MessageTopics.WORLDSERVER_CLIENT_IDENT_SUCCESS);
				AppRegistry.getInstance().retrieve(MessageManager.class).publish(success);
				break;
			}
			case INVALID_TOKEN:
			default:
			{
				Message failed = new StandardMessage(session, MessageTopics.WORLDSERVER_CLIENT_IDENT_FAILED);
				AppRegistry.getInstance().retrieve(MessageManager.class).publish(failed);
				break;
			}
		}		
	}
}
