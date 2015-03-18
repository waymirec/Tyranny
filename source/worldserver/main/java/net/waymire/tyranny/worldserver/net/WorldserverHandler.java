package net.waymire.tyranny.worldserver.net;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.StandardMessage;
import net.waymire.tyranny.common.net.BaseTcpServerHandler;
import net.waymire.tyranny.common.net.IpSessionMonitor;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionAttributes;
import net.waymire.tyranny.common.protocol.ProtocolHandler;
import net.waymire.tyranny.common.protocol.WorldserverOpcode;
import net.waymire.tyranny.common.protocol.WorldserverPacket;
import net.waymire.tyranny.common.task.Task;
import net.waymire.tyranny.common.task.TaskFuture;
import net.waymire.tyranny.common.task.TaskManager;
import net.waymire.tyranny.common.task.TcpSessionDisconnectTask;
import net.waymire.tyranny.worldserver.message.MessageProperties;
import net.waymire.tyranny.worldserver.message.MessageTopics;

public class WorldserverHandler extends BaseTcpServerHandler<WorldserverPacket> 
{
	private final Worldserver worldserver;
	
	public WorldserverHandler(ProtocolHandler<TcpSession,WorldserverPacket> protocolHandler,Worldserver worldserver)
	{
		super(protocolHandler);
		this.worldserver = worldserver;
	}
	
	@Override
	public void onSessionOpened(TcpSession session)
	{
		super.onSessionOpened(session);
		LogHelper.info(this, "Client connected: [{0}].", ((InetSocketAddress)session.getRemoteAddress()).getHostString());

		synchronized(session)
		{
			session.setAttribute(WorldserverSessionAttributes.WORLDSERVER_SESSION_STATE, WorldserverSessionState.PENDING_IDENT);
			
			Task disconnect = new TcpSessionDisconnectTask(session);
			session.setAttribute(TcpSessionAttributes.DISCONNECT_TASK,  disconnect);
			
			TaskFuture disconnectFuture = AppRegistry.getInstance().retrieve(TaskManager.class).schedule(disconnect,  5,  TimeUnit.SECONDS);
			session.setAttribute(TcpSessionAttributes.DISCONNECT_FUTURE, disconnectFuture);
			
			TaskFuture future = AppRegistry.getInstance().retrieve(TaskManager.class).schedule(disconnect, 5, TimeUnit.SECONDS);
			session.setAttribute(TcpSessionAttributes.DISCONNECT_FUTURE, future);
		}
		
		Message connected = new StandardMessage(session,MessageTopics.WORLDSERVER_CLIENT_CONNECTED);
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
			session.setAttribute(WorldserverSessionAttributes.WORLDSERVER_SESSION_STATE, WorldserverSessionState.NULL);
		}
		
		@SuppressWarnings("unchecked")
		IpSessionMonitor<TcpSession> monitor = (IpSessionMonitor<TcpSession>)this.getAttribute("SESSION_MONITOR");
		if(monitor != null)
		{
			monitor.remove(session);
		}
		
		Message disconnected = new StandardMessage(session, MessageTopics.WORLDSERVER_CLIENT_DISCONNECTED);
		disconnected.setProperty(MessageProperties.CLIENT_ADDRESS, ((InetSocketAddress)session.getRemoteAddress()).getHostString());
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(disconnected);
	}
	
	@Override
	public void onMessageReceived(TcpSession session, WorldserverPacket packet)
	{
		// If the session is not authenticated yet then we make sure that we are receiving the 
		// appropriate packet. Any deviation from the expected behavior will result in terminating
		// the session. This is done to prevent any sort of attempt to inject malformed/exploitative 
		// data into the network.
		WorldserverOpcode opcode = packet.opcode();
		if(!session.getAuthenticated() && !WorldserverOpcode.AUTH.equals(opcode))
		{
			LogHelper.warning(this, "Received a NON-AUTH Packet [{0}] from unauthorized client [{1}]. Disconnecting session.", opcode, ((InetSocketAddress)session.getRemoteAddress()).getHostString());
			session.close();
			return;
		}
		super.onMessageReceived(session, packet);
	}
}
