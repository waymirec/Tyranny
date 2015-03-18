package net.waymire.tyranny.common.mina.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;

import net.waymire.tyranny.common.AsyncQueue;
import net.waymire.tyranny.common.delegate.Delegate;
import net.waymire.tyranny.common.delegate.DelegateImpl;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.mina.IoSessionHelper;
import net.waymire.tyranny.common.mina.MinaSessionFactory;
import net.waymire.tyranny.common.mina.MinaTcpSession;
import net.waymire.tyranny.common.net.IpClient;
import net.waymire.tyranny.common.net.IpClientHandler;
import net.waymire.tyranny.common.net.IpProperties;
import net.waymire.tyranny.common.net.IpSession;
import net.waymire.tyranny.common.net.IpClientState;
import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;
import net.waymire.tyranny.common.util.ExceptionUtil;

public abstract class MinaIpClient<S extends IpSession, P extends Packet<? extends Opcode>> extends IoHandlerAdapter implements IoFutureListener<ConnectFuture>, IpClient
{

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
	private final AsyncQueue<Object> messageQueue;
	private final Class<? extends S> sessionType;
	
	private IpProperties properties = null;
	private IpClientHandler<S,P> handler = null;
	private ProtocolCodecFactory protocolCodecFactory = null;
	private IoConnector connector = null;
	private ConnectFuture future = null;
	private S session = null;
	private IpClientState state = IpClientState.DISCONNECTED;
	
	private InetSocketAddress currentConnection = null;
	
	public MinaIpClient(Class<? extends S> sessionType)
	{
		this.sessionType = sessionType;
		Delegate d = new DelegateImpl(this,"processMessage",new Class<?>[]{ Object.class });
		this.messageQueue = new AsyncQueue<Object>(d);
	}

	public IpProperties getProperties() {
		return properties;
	}

	public void setProperties(IpProperties properties) {
		this.properties = properties;
	}

	public IpClientHandler<S,P> getHandler() {
		return handler;
	}

	public void setHandler(IpClientHandler<S,P> handler) {
		this.handler = handler;
	}

	public ProtocolCodecFactory getProtocolCodecFactory()
	{
		return protocolCodecFactory;
	}
	
	public void setProtocolCodecFactory(ProtocolCodecFactory protocolCodecFactory)
	{
		this.protocolCodecFactory = protocolCodecFactory;
	}
	
	public IoConnector getConnector() {
		return connector;
	}

	public void setConnector(IoConnector connector) {
		this.connector = connector;
	}

	public ConnectFuture getFuture() {
		return future;
	}

	public void setFuture(ConnectFuture future) {
		this.future = future;
	}

	public Class<? extends S> getSessionType()
	{
		return sessionType;
	}
	
	public S getSession() {
		return session;
	}

	public void setSession(S session) {
		this.session = session;
	}

	public IpClientState getState() {
		return state;
	}

	public void setState(IpClientState state) {
		this.state = state;
	}

	public InetSocketAddress getCurrentConnection()
	{
		return currentConnection;
	}
	
	public void setCurrentConnection(InetSocketAddress currentConnection)
	{
		this.currentConnection = currentConnection;
	}
	
	public ReentrantReadWriteLock getLock() {
		return lock;
	}

	public AsyncQueue<Object> getMessageQueue() {
		return messageQueue;
	}
	
	@Override
	public void connect(String ip, int port)
	{
		InetSocketAddress addr = new InetSocketAddress(ip, port);
		connect(addr);
	}
	
	@Override
	public void connect(InetSocketAddress address)
	{
		lock.writeLock().lock();
		try
		{
			if(!IpClientState.DISCONNECTED.equals(state))
			{
				LogHelper.warning(this,"Ignoring connect request. Current state is {0}",state);
				return;
			}
			
			if(LogHelper.isDebugEnabled(this))
			{
				LogHelper.debug(this,"Attempting to establish a connection with {0}:{1}.", address.getHostName(),Integer.toString(address.getPort()));
			}
			
			state = IpClientState.CONNECTING;
			initializeSocket();
			future = connector.connect(address);
			future.addListener((IoFutureListener<ConnectFuture>)this);			
		}
		catch(Exception e)
		{
			LogHelper.severe(this,"Connection to {0}:{1} failed.", address.getHostName(),Integer.toString(address.getPort()));
			state = IpClientState.DISCONNECTED;
			cleanup();
			handler.onConnectFailed(session);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	public void disconnect()
	{
		lock.writeLock().lock();
		try
		{
			if(!IpClientState.CONNECTED.equals(state))
			{
				LogHelper.warning(this,"Ignoring connect request. Current state is {0}",state);
				return;
			}
			
			if(LogHelper.isDebugEnabled(this))
			{
				LogHelper.debug(this,"Terminating connection to {0}:{1}.", currentConnection.getHostName(),Integer.toString(currentConnection.getPort()));
			}
			
			state = IpClientState.DISCONNECTING;
			if(session != null && session.isValid())
			{
				session.close();
			}
			
			cleanup();
			
			return;
		}
		catch(Exception e)
		{
			LogHelper.severe(this,"Exception encountered during disconnect: {0}.",ExceptionUtil.getReason(e));
		}
		finally
		{
			boolean success = ((session != null) && (!session.isConnected() || session.isClosing()));
			state = success ? IpClientState.DISCONNECTED : IpClientState.CONNECTED;			
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public boolean isConnected()
	{
		lock.readLock().lock();
		try
		{
			if(session != null && session.isValid())
			{
				return session.isConnected();
			}
			else
			{
				return false;
			}
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean isConnecting()
	{
		lock.readLock().lock();
        try
        {
            return IpClientState.CONNECTING.equals(state);
        } 
        finally
        {
            lock.readLock().unlock();
        }
	}
	
	/**
	 * Writes the {@link PacketBuffer} to the network socket.
	 * @param packet packet to write
	 */
	public void send(Packet<? extends Opcode> message)
	{
		lock.readLock().lock();
		final IpClientState _state = state;
		lock.readLock().unlock();
		
		if((session == null) || !session.isConnected() || session.isClosing() || !IpClientState.CONNECTED.equals(_state))
		{
			return;
		}
				
		IoSessionHelper.writePacket(((MinaTcpSession)session).getIoSession(),message);
	}
	
	/**
	 * @param session connected client's session
	 * @throws Exception
	 * @see org.apache.mina.core.service.IoHandlerAdapter#sessionOpened(IoSession)
	 */	
	@Override
	public void sessionOpened(IoSession ioSession) throws Exception 
	{
		try
		{
			session = MinaSessionFactory.create(sessionType, ioSession);
			currentConnection = (InetSocketAddress)session.getRemoteAddress();
			
			IoSessionHelper.setReceiveBufferSize(ioSession,properties.getReceiveBufferSize());
			IoSessionHelper.setSendBufferSize(ioSession, properties.getSendBufferSize());
			IoSessionHelper.setIdleTime(ioSession,properties.getIdleTime());
		
			if(LogHelper.isDebugEnabled(this))
			{
				InetSocketAddress remote = (InetSocketAddress)session.getRemoteAddress();
				LogHelper.debug(this,"Connection established with {0}:{1}",remote.getHostName(),Integer.toString(remote.getPort()));
			}
			state = IpClientState.CONNECTED;
			messageQueue.start();	
			handler.onSessionOpened(session);
		}
		catch(Exception e)
		{
			LogHelper.warning(this,"Exception encountered while connecting to {0}: {1}",ioSession,ExceptionUtil.getReason(e));
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * @param session client session
	 * @param message message received
	 * @throws Exception
	 * @see org.apache.mina.core.service.IoHandlerAdapter#sessionClosed(IoSession)
	 */	
	@Override
	public void sessionClosed(IoSession ioSession)
	{
		if(LogHelper.isDebugEnabled(this))
		{
			InetSocketAddress remote = (InetSocketAddress)ioSession.getRemoteAddress();
			LogHelper.debug(this,"Connection to {0}:{1} lost.",remote.getHostName(),Integer.toString(remote.getPort()));
		}

		state = IpClientState.DISCONNECTED;
		messageQueue.stop();
		currentConnection = null;
		handler.onSessionClosed(session);
		session = null;
	}
	
	/**
	 * @param session client session
	 * @param status idle status
	 * @throws Exception
	 * @see org.apache.mina.core.service.IoHandlerAdapter#sessionIdle(IoSession,IdleStatus)
	 */	
	@Override
	public void sessionIdle(IoSession ioSession, IdleStatus status)
	{
		if(LogHelper.isDebugEnabled(this))
		{
			LogHelper.debug(this,"Session {0} has gone idle",session);
		}
		handler.onSessionIdle(session);
	}
	
	/**
	 * @param session client session
	 * @param message message received
	 * @throws Exception
	 * @see org.apache.mina.core.service.IoHandlerAdapter#messageReceived(IoSession,Object)
	 */	
	@Override
	public void messageReceived(IoSession ioSession, Object message) throws Exception
	{
		messageQueue.put((Object)new Object[]{session,message});
	}
	
	/**
	 * @param session client session
	 * @param message message sent
	 * @throws Exception
	 * @see org.apache.mina.core.service.IoHandlerAdapter#messageReceived(IoSession,Object)
	 */
	@Override
	public void messageSent(IoSession ioSession, Object message) throws Exception
	{
		
	}
	
	/**
	 * Invoked when a connection attempt is over. If the attempt was unsuccessful we then invoke {@link NetworkClientBase#onFailedConnect(ConnectFuture).
	 * @param future The source {@link org.apache.mina.core.future.IoFuture IoFuture} which called this callback.
	 * @see org.apache.mina.core.future.IoFutureListener#operationComplete(org.apache.mina.core.future.IoFuture)
	 */
	@Override
	public void operationComplete(ConnectFuture future)
	{
		lock.writeLock().lock();
		try
		{			
			if (!future.isConnected())
			{
				try
				{
					InetSocketAddress remote = (InetSocketAddress)future.getSession().getRemoteAddress();
					LogHelper.warning(this,"Connection to {0}:{1} failed.",remote.getHostName(),Integer.toString(remote.getPort()));
				}
				catch(Exception exception)
				{
					LogHelper.warning(this,"Connection failed.");
				}
				state = IpClientState.DISCONNECTED;
				handler.onConnectFailed(session);
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void exceptionCaught(IoSession ioSession,Throwable cause)
	{
		if(!(cause instanceof IOException))
		{
			LogHelper.severe(this,"Exception encountered: {0}",ExceptionUtil.getReason(cause));
			LogHelper.severe(this,ExceptionUtil.getStackTrace(cause));
			cause.printStackTrace();
			handler.onException(session, cause);
		}
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
	
	
	abstract protected void initializeSocket();
	
	protected void cleanup()
	{
		if(session != null && ((MinaTcpSession)session).getIoSession() !=  null)
		{
			if(session.isConnected())
			{
				session.close();
			}
		}
		
		if(connector != null)
		{
			try
			{
				if(!connector.isDisposed() && !connector.isDisposing())
				{
					connector.dispose();
				}
			}
			catch(Exception exception)
			{
				LogHelper.info(this, "Unable to dispose of TCP client connector: {0}", ExceptionUtil.getStackTrace(exception));
			}
			connector = null;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void processMessage(Object message)
	{
		S session = (S)((Object[])message)[0];
		P packet = (P)((Object[])message)[1];
		handler.onMessageReceived(session, packet);
	}
}
