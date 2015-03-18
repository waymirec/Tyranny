package net.waymire.tyranny.common.mina.server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;

import net.waymire.tyranny.common.AsyncQueue;
import net.waymire.tyranny.common.delegate.Delegate;
import net.waymire.tyranny.common.delegate.DelegateImpl;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.mina.IoSessionHelper;
import net.waymire.tyranny.common.mina.MinaSessionFactory;
import net.waymire.tyranny.common.net.IpProperties;
import net.waymire.tyranny.common.net.IpServer;
import net.waymire.tyranny.common.net.IpServerHandler;
import net.waymire.tyranny.common.net.IpSession;
import net.waymire.tyranny.common.net.IpServerState;
import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;
import net.waymire.tyranny.common.util.ExceptionUtil;

public abstract class MinaIpServer<S extends IpSession, P extends Packet<? extends Opcode>> extends IoHandlerAdapter implements IpServer, IoServiceListener
{
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
	private final Map<Long,S> sessions = new HashMap<Long,S>();
	private final Class<? extends S> sessionType;
	
	private String name;
	private ProtocolCodecFactory protocolCodecFactory = null;
	private IpProperties properties;
	private IoAcceptor acceptor;
	private InetSocketAddress address;
	private IpServerHandler<S,P> handler;
	private IpServerState state = IpServerState.STOPPED;
	private AsyncQueue<Object> messageQueue;
	
	public MinaIpServer(InetSocketAddress address, Class<? extends S> sessionType)
	{
		this.state = IpServerState.STOPPED;
		this.sessionType = sessionType;
		this.name = sessionType.getSimpleName();
		this.address = address;
		
		Delegate d = new DelegateImpl(this,"processMessage",new Class<?>[]{ Object.class });
		this.messageQueue = new AsyncQueue<Object>(d);
	}
	
	public MinaIpServer(Class<S> sessionType)
	{
		this(null,sessionType);
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public IpProperties getProperties() {
		return properties;
	}


	public void setProperties(IpProperties properties) {
		this.properties = properties;
	}


	public IoAcceptor getAcceptor() {
		return acceptor;
	}


	public void setAcceptor(IoAcceptor acceptor) {
		this.acceptor = acceptor;
	}

	public ProtocolCodecFactory getProtocolCodecFactory()
	{
		return protocolCodecFactory;
	}
	
	public void setProtocolCodecFactory(ProtocolCodecFactory protocolCodecFactory)
	{
		this.protocolCodecFactory = protocolCodecFactory;
	}

	public InetSocketAddress getAddress() {
		return address;
	}


	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}


	public IpServerHandler<? extends IpSession,P> getHandler() {
		return handler;
	}


	public void setHandler(IpServerHandler<S,P> handler) {
		this.handler = handler;
	}


	protected ReentrantReadWriteLock getLock() {
		return lock;
	}


	public Map<Long, S> getSessions() {
		return sessions;
	}


	public AsyncQueue<Object> getMessageQueue() {
		return messageQueue;
	}

	/**
	 * Starts the network server and prepares it to accept connections.
	 */
	@Override
	public boolean startup() 
	{
		lock.writeLock().lock();
		try 
		{
			if(state != IpServerState.STOPPED)
			{
				LogHelper.warning(this,  "Failed to start Server [{0}]. State Was: {0}", name, state.toString());
				return false;
			}
			
			LogHelper.info(this, "Starting Server [{0}].", name);
			state = IpServerState.STARTING;
			setup();
			state = IpServerState.STARTED;
			LogHelper.info(this, "Started Server [{0}].", name);
			return true;
		}
		catch (BindException be) 
		{
			state = IpServerState.STOPPED;
			LogHelper.info(this, "Failed to start Server [{0}]. Failed to bind: {1}", name, ExceptionUtil.getReason(be));
			return false;
		}
		catch(IOException ioe) 
		{
			state = IpServerState.STOPPED;
			LogHelper.info(this, "Failed to start Server [{0}]: {1}", name, ExceptionUtil.getReason(ioe));
			return false;
		}
		finally 
		{
			lock.writeLock().unlock();
		}
	}

	/**
	 * Stops the network server, closing any open connections.
	 */
	@Override
	public boolean shutdown() 
	{
		lock.writeLock().lock();
		try 
		{
			if(state != IpServerState.STARTED)
			{
				LogHelper.warning(this,  "Failed to stop Server [{0}]. State Was: {0}", name, state.toString());
				return false;
			}
			
			state = IpServerState.STOPPING;
			try
			{
				teardown();
				return true;
			}
			finally
			{
				state = IpServerState.STOPPED;
			}
		} 
		finally 
		{
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * @param service the {@link IoService}
	 * @see org.apache.mina.core.service.IoServiceListener#serviceActivated(IoService)
	 */
	@Override
	public void serviceActivated(IoService service) { }
	
	/**
	 * @param service the {@link IoService}
	 * @see org.apache.mina.core.service.IoServiceListener#serviceDeactivated(IoService)
	 */
	@Override
	public void serviceDeactivated(IoService service) { }

	/**
	 * @param service the {@link IoService}
	 * @param status idle status
	 * @see org.apache.mina.core.service.IoServiceListener#serviceIdle(IoService, IdleStatus)
	 */
	public void serviceIdle(IoService service, IdleStatus status) { }

	/**
	 * @param session client session
	 * @see org.apache.mina.core.service.IoServiceListener#sessionCreated(IoSession)
	 */	
	@Override
	public void sessionCreated(IoSession session) { }

	/**
	 * @param session client session
	 * @see org.apache.mina.core.service.IOServiceListener#sessionDestroyed(IoSession)
	 */
	@Override
	public void sessionDestroyed(IoSession session) { }	
	
	/**
	 * @param session connected client's session
	 * @throws Exception
	 * @see org.apache.mina.core.service.IoHandlerAdapter#sessionOpened(IoSession)
	 */	
	@Override
	public void sessionOpened(IoSession session) throws Exception 
	{
		LogHelper.info(this, "Accepted connection from [{0}].",  session);
		lock.writeLock().lock();
		try
		{
			IoSessionHelper.setReceiveBufferSize(session, properties.getReceiveBufferSize());
			IoSessionHelper.setSendBufferSize(session,  properties.getSendBufferSize());
			IoSessionHelper.setIdleTime(session, properties.getIdleTime());

			S clientSession = MinaSessionFactory.create(sessionType, session);
			sessions.put(session.getId(),clientSession);
			handler.onSessionOpened(clientSession);
		}
		finally 
		{ 
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * @param session client session
	 * @param message message received
	 * @throws Exception
	 * @see org.apache.mina.core.service.IoHandlerAdapter#sessionClosed(IoSession)
	 */	
	@Override
	public void sessionClosed(IoSession session)
	{
		LogHelper.info(this, "Dropped connection to {0}.", session);
		lock.writeLock().lock();
		try 
		{
			S clientSession = sessions.get(session.getId());
			handler.onSessionClosed(clientSession);
		}
		finally 
		{ 
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * @param session client session
	 * @param status idle status
	 * @throws Exception
	 * @see org.apache.mina.core.service.IoHandlerAdapter#sessionIdle(IoSession,IdleStatus)
	 */	
	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
	{
		LogHelper.info(this, "Session {0} has gone idle.",  session);
		S clientSession = sessions.get(session.getId());			
		handler.onSessionIdle(clientSession);
	}
	
	/**
	 * @param session client session
	 * @param message message received
	 * @throws Exception
	 * @see org.apache.mina.core.service.IoHandlerAdapter#messageReceived(IoSession,Object)
	 */	
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception
	{
		lock.writeLock().lock();
		try
		{
			S clientSession = sessions.get(session.getId());
			messageQueue.put((Object)new Object[]{clientSession,message});
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * @param session client session
	 * @param T exception cause
	 * @see org.apache.mina.core.service.IoHandlerAdapter#exceptionCaught(IoSession,Throwable)
	 */	
	@Override
	public void exceptionCaught(IoSession session, Throwable t) throws Exception
	{
		if(!(t instanceof IOException))
		{
			LogHelper.severe(this,"Exception encountered: {0}", ExceptionUtil.getReason(t));
			LogHelper.severe(this,"{0}", ExceptionUtil.getStackTrace(t));
			t.printStackTrace();
			S clientSession = sessions.get(session.getId());
			handler.onException(clientSession, t);
		}
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}	

	abstract protected void initAcceptor() throws BindException,IOException;
	
	protected void setup() throws IOException
	{
		initAcceptor();
		acceptor.unbind();
		acceptor.bind(address);
		messageQueue.start();
	}
	
	protected void teardown()
	{
		if(acceptor != null)
		{
			if(acceptor.isActive() && !acceptor.isDisposed() && !acceptor.isDisposing())
			{
				acceptor.removeListener((IoServiceListener)this);
				acceptor.unbind();
				acceptor.dispose();
				acceptor = null;
			}
		}
		
		if(messageQueue != null && messageQueue.isRunning())
		{
			messageQueue.stop();
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
