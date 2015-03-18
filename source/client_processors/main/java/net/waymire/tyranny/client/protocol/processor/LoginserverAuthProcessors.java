package net.waymire.tyranny.client.protocol.processor;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import net.waymire.tyranny.client.message.MessageProperties;
import net.waymire.tyranny.client.message.MessageTopics;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.delegate.DelegateImpl;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.StandardMessage;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.protocol.LoginserverAuthOpcode;
import net.waymire.tyranny.common.protocol.LoginserverAuthResult;
import net.waymire.tyranny.common.protocol.LoginserverOpcode;
import net.waymire.tyranny.common.protocol.LoginserverPacket;
import net.waymire.tyranny.common.protocol.LoginserverPacketProcessorDelegate;
import net.waymire.tyranny.common.protocol.LoginserverProtocolProcessor;
import net.waymire.tyranny.common.util.ExceptionUtil;

public class LoginserverAuthProcessors 
{
	private static final Map<LoginserverAuthOpcode,DelegateImpl> delegates = new HashMap<>();
	
	static
	{
		delegates.put(LoginserverAuthOpcode.CHAP_CHALLENGE, new LoginserverPacketProcessorDelegate(LoginserverAuthProcessors.class, "handleChapChallenge"));
		delegates.put(LoginserverAuthOpcode.CHAP_RESPONSE,  new LoginserverPacketProcessorDelegate(LoginserverAuthProcessors.class, "handleChapResponse"));
	}
	
	@LoginserverProtocolProcessor(opcode=LoginserverOpcode.AUTH)
	private static void handleAuth(TcpSession session, LoginserverPacket packet)
	{
		LoginserverAuthOpcode opcode = LoginserverAuthOpcode.valueOf(packet.getInt());
		if(delegates.containsKey(opcode))
		{
			if(LogHelper.isDebugEnabled(LoginserverAuthProcessors.class))
			{
				LogHelper.debug(LoginserverAuthProcessors.class, "AUTH Opcode [{0}] Received From Server [{1}].", opcode, ((InetSocketAddress)session.getRemoteAddress()).getHostString());
			}
			delegates.get(opcode).invoke(session,packet);
		}
		else
		{
			LogHelper.warning(LoginserverAuthProcessors.class, "Unknown AUTH Opcode [{0}] Received From Server [{1}].", opcode, ((InetSocketAddress)session.getRemoteAddress()).getHostString());
		}
	}

	@SuppressWarnings("unused")
	private static void handleChapChallenge(TcpSession session,LoginserverPacket packet)
	{
		try
		{
			byte[] challenge = new byte[20];
			packet.get(challenge);
			Message message = new StandardMessage(session,MessageTopics.LOGINSERVER_AUTH_CHALLENGE);
			message.setProperty(MessageProperties.LOGINSERVER_AUTH_CHALLENGE_BYTES, challenge);
			AppRegistry.getInstance().retrieve(MessageManager.class).publish(message);
		}
		catch (Exception e)
		{
			Message authenticated = new StandardMessage(session, MessageTopics.LOGINSERVER_CLIENT_AUTH_FAILED);
			authenticated.setProperty(MessageProperties.LOGINSERVER_AUTH_FAIL_REASON, LoginserverAuthResult.UNEXPECTED_ERROR);
			authenticated.setProperty(MessageProperties.LOGINSERVER_AUTH_FAIL_DETAILS, ExceptionUtil.getStackTrace(e));
			AppRegistry.getInstance().retrieve(MessageManager.class).publish(authenticated);
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private static void handleChapResponse(TcpSession session,LoginserverPacket packet)
	{
		try 
		{
			boolean response = packet.getBoolean();			
			Message message = new StandardMessage(session,MessageTopics.LOGINSERVER_AUTH_RESPONSE);
			message.setProperty(MessageProperties.LOGINSERVER_AUTH_RESPONSE,response);
			AppRegistry.getInstance().retrieve(MessageManager.class).publish(message);
		} 
		catch (Exception e) 
		{
			Message authenticated = new StandardMessage(session, MessageTopics.LOGINSERVER_CLIENT_AUTH_FAILED);
			authenticated.setProperty(MessageProperties.LOGINSERVER_AUTH_FAIL_REASON, LoginserverAuthResult.UNEXPECTED_ERROR);
			authenticated.setProperty(MessageProperties.LOGINSERVER_AUTH_FAIL_DETAILS, ExceptionUtil.getStackTrace(e));
			AppRegistry.getInstance().retrieve(MessageManager.class).publish(authenticated);
			e.printStackTrace();
		}
	}

	@LoginserverProtocolProcessor(opcode=LoginserverOpcode.AUTH_RESULT)
	private static void handleAuthResult(TcpSession session, LoginserverPacket packet)
	{
		LoginserverAuthResult result = LoginserverAuthResult.valueOf(packet.getShort());
		if(result == LoginserverAuthResult.READY)
		{
			Message authenticated = new StandardMessage(session, MessageTopics.LOGINSERVER_CLIENT_AUTH_SUCCESS);
			AppRegistry.getInstance().retrieve(MessageManager.class).publish(authenticated);
		}
		else
		{
			Message authenticated = new StandardMessage(session, MessageTopics.LOGINSERVER_CLIENT_AUTH_FAILED);
			authenticated.setProperty(MessageProperties.LOGINSERVER_AUTH_FAIL_REASON, result);
			AppRegistry.getInstance().retrieve(MessageManager.class).publish(authenticated);
		}
	}

	private LoginserverAuthProcessors() { }
}
