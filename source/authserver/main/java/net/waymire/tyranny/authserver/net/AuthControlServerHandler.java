package net.waymire.tyranny.authserver.net;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.authserver.message.MessageTopics;
import net.waymire.tyranny.authserver.message.MessageProperties;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.GUID;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.StandardMessage;
import net.waymire.tyranny.common.net.BaseTcpServerHandler;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionAttributes;
import net.waymire.tyranny.common.protocol.AuthControlOpcode;
import net.waymire.tyranny.common.protocol.AuthControlPacket;
import net.waymire.tyranny.common.protocol.ProtocolHandler;
import net.waymire.tyranny.common.task.Task;
import net.waymire.tyranny.common.task.TaskFuture;
import net.waymire.tyranny.common.task.TaskManager;
import net.waymire.tyranny.common.task.TcpSessionDisconnectTask;

public class AuthControlServerHandler extends BaseTcpServerHandler<AuthControlPacket> 
{
	public AuthControlServerHandler(ProtocolHandler<TcpSession,AuthControlPacket> protocolHandler)
	{
		super(protocolHandler);
		
		MessageManager messageManager = AppRegistry.getInstance().retrieve(MessageManager.class);
		messageManager.load(this);
	}
	
	@Override
	public void onSessionOpened(TcpSession session)
	{
		super.onSessionOpened(session);
		LogHelper.info(this, "Client connected: [{0}].", ((InetSocketAddress)session.getRemoteAddress()).getHostString());
		synchronized(session)
		{
			session.setAttribute(AuthControlServerSessionAttributes.CONTROLSERVER_SESSION_STATE, AuthControlServerSessionState.PENDING_IDENT);
			
			Task disconnect = new TcpSessionDisconnectTask(session);
			session.setAttribute(TcpSessionAttributes.DISCONNECT_TASK,  disconnect);
			
			TaskFuture disconnectFuture = AppRegistry.getInstance().retrieve(TaskManager.class).schedule(disconnect,  5,  TimeUnit.SECONDS);
			session.setAttribute(TcpSessionAttributes.DISCONNECT_FUTURE, disconnectFuture);
		}
		
		Message connected = new StandardMessage(session,MessageTopics.AUTHCONTROL_SERVER_CLIENT_CONNECTED);
		connected.setProperty(MessageProperties.CLIENT_ADDRESS, ((InetSocketAddress)session.getRemoteAddress()).getHostString());
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(connected);
	}

	@Override
	public void onSessionIdle(TcpSession session)
	{
		super.onSessionIdle(session);
		LogHelper.info(this,"Client has gone idle: [{0}].", ((InetSocketAddress)session.getRemoteAddress()).getHostString());
		session.close();
	}
	
	@Override
	public void onSessionClosed(TcpSession session)
	{
		super.onSessionClosed(session);
		LogHelper.info(this,"Client has disconnected: [{0}].", ((InetSocketAddress)session.getRemoteAddress()).getHostString());
		synchronized(session)
		{
			session.setAttribute(AuthControlServerSessionAttributes.CONTROLSERVER_SESSION_STATE, AuthControlServerSessionState.NULL);
		}

		Message disconnected = new StandardMessage(session, MessageTopics.AUTHCONTROL_SERVER_CLIENT_DISCONNECTED);
		disconnected.setProperty(MessageProperties.CLIENT_ADDRESS, ((InetSocketAddress)session.getRemoteAddress()).getHostString());
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(disconnected);
		
		deRegisterRealm(session);
	}

	@Override
	public void onMessageReceived(TcpSession session, AuthControlPacket packet) 
	{
		// If the session is not authenticated yet then we make sure that we are receiving the 
		// appropriate packet. Any deviation from the expected behavior will result in terminating
		// the session. This is done to prevent any sort of attempt to inject malformed/exploitative 
		// data into the network.
		AuthControlOpcode opcode = packet.opcode();
		if(!session.getAuthenticated() && !AuthControlOpcode.AUTH.equals(opcode))
		{
			LogHelper.warning(this, "Received a NON-AUTH Packet [{0}] from unauthorized client [{1}]. Disconnecting session.", opcode, ((InetSocketAddress)session.getRemoteAddress()).getHostString());
			session.close();
			return;
		}
		super.onMessageReceived(session, packet);
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
	
	@Override
	protected void finalize()
	{
		MessageManager messageManager = AppRegistry.getInstance().retrieve(MessageManager.class);
		messageManager.unload(this);
	}

	private void deRegisterRealm(TcpSession session)
	{
		GUID realmId = (GUID)session.getAttribute(AuthControlServerSessionAttributes.WORLD_ID);
		if(realmId != null)
		{
			//AppRegistry.getInstance().retrieve(RealmManager.class).remove(realmId);
		}
	}
}
