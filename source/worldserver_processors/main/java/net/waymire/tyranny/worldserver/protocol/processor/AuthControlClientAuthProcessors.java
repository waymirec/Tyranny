package net.waymire.tyranny.worldserver.protocol.processor;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.StandardMessage;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.protocol.AuthControlIdentResponseCode;
import net.waymire.tyranny.common.protocol.AuthControlOpcode;
import net.waymire.tyranny.common.protocol.AuthControlPacket;
import net.waymire.tyranny.common.protocol.AuthControlProtocolProcessor;
import net.waymire.tyranny.worldserver.message.MessageTopics;
import net.waymire.tyranny.worldserver.net.AuthControlClientSessionAttributes;
import net.waymire.tyranny.worldserver.net.AuthControlClientSessionState;

public class AuthControlClientAuthProcessors 
{
	private AuthControlClientAuthProcessors() { }
	
	@AuthControlProtocolProcessor(opcode=AuthControlOpcode.AUTH_RESULT)
	private static void handleAuth(TcpSession session, AuthControlPacket packet)
	{
		byte responseValue = packet.get();
		AuthControlIdentResponseCode responseCode = AuthControlIdentResponseCode.valueOf(responseValue);
		
		switch(responseCode)
		{
			case SUCCESS:
			{
				session.setAttribute(AuthControlClientSessionAttributes.CONTROLCLIENT_SESSION_STATE, AuthControlClientSessionState.AUTHENTICATED);
				Message authMessage = new StandardMessage(session, MessageTopics.AUTHCONTROL_CLIENT_AUTH_SUCCESS);
				AppRegistry.getInstance().retrieve(MessageManager.class).publish(authMessage);
				LogHelper.info(AuthControlClientAuthProcessors.class, "Successfully authenticated to auth control server.");
				break;
			}
			case INVALID_KEY:
			{
				session.setAttribute(AuthControlClientSessionAttributes.CONTROLCLIENT_SESSION_STATE, AuthControlClientSessionState.AUTH_FAILED);
				Message authfailed = new StandardMessage(session, MessageTopics.AUTHCONTROL_CLIENT_AUTH_FAILED);
				AppRegistry.getInstance().retrieve(MessageManager.class).publish(authfailed);
				LogHelper.severe(AuthControlClientAuthProcessors.class, "Failed to authenticate to auth control server due to an invalid key.");
				break;
			}
			case VERSION_MISMATCH:
			{
				session.setAttribute(AuthControlClientSessionAttributes.CONTROLCLIENT_SESSION_STATE, AuthControlClientSessionState.AUTH_FAILED);
				Message authfailed = new StandardMessage(session, MessageTopics.AUTHCONTROL_CLIENT_AUTH_FAILED);
				AppRegistry.getInstance().retrieve(MessageManager.class).publish(authfailed);
				LogHelper.severe(AuthControlClientAuthProcessors.class, "Failed to authenticate to auth control server due to version mismatch.");
				break;
			}
			default:
			{
				session.setAttribute(AuthControlClientSessionAttributes.CONTROLCLIENT_SESSION_STATE, AuthControlClientSessionState.AUTH_FAILED);
				Message authfailed = new StandardMessage(session, MessageTopics.AUTHCONTROL_CLIENT_AUTH_FAILED);
				AppRegistry.getInstance().retrieve(MessageManager.class).publish(authfailed);
				LogHelper.severe(AuthControlClientAuthProcessors.class, "Failed to authenticate to auth control server. REASON={0}.", responseCode.toString());
			}
		}
	}
}
