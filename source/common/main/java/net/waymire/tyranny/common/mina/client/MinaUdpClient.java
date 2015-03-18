package net.waymire.tyranny.common.mina.client;

import java.net.InetSocketAddress;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;

import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.mina.IoSessionHelper;
import net.waymire.tyranny.common.mina.MinaSessionFactory;
import net.waymire.tyranny.common.mina.MinaUdpSession;
import net.waymire.tyranny.common.net.IpClientState;
import net.waymire.tyranny.common.net.UdpClient;
import net.waymire.tyranny.common.net.UdpSession;
import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;
import net.waymire.tyranny.common.util.ExceptionUtil;

public class MinaUdpClient<P extends Packet<? extends Opcode>> extends MinaIpClient<UdpSession,P> implements UdpClient
{
	public MinaUdpClient()
	{
		super(MinaUdpSession.class);
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
	
	@Override
	public void sessionOpened(IoSession ioSession) throws Exception 
	{
		if(IpClientState.CONNECTED != this.getState())
		{
			try
			{
				this.setSession(MinaSessionFactory.create(this.getSessionType(), ioSession));
				this.setCurrentConnection((InetSocketAddress)this.getSession().getRemoteAddress());
			
				IoSessionHelper.setReceiveBufferSize(ioSession,this.getProperties().getReceiveBufferSize());
				IoSessionHelper.setSendBufferSize(ioSession, this.getProperties().getSendBufferSize());
				IoSessionHelper.setIdleTime(ioSession,this.getProperties().getIdleTime());

				this.setState(IpClientState.CONNECTED);
				this.getMessageQueue().start();
				this.getHandler().onSessionOpened(this.getSession());
			}
			catch(Exception e)
			{
				LogHelper.warning(this,"Exception encountered while connecting to {0}: {1}",ioSession,ExceptionUtil.getReason(e));
				e.printStackTrace();
				return;
			}
		}
	}
	
	@Override
	public void sessionIdle(IoSession ioSession, IdleStatus status)
	{
		try
		{
			this.getHandler().onSessionIdle(this.getSession());
		}
		catch(Exception e)
		{
			InetSocketAddress endpoint = (InetSocketAddress)ioSession.getRemoteAddress();
			LogHelper.warning(this,"Exception encountered during call to {0}.sessionIdle() for session [{1}:{2}]: {3}",this.getClass().getSimpleName(),endpoint.getHostString(),endpoint.getPort(),ExceptionUtil.getReason(e));
			e.printStackTrace();
			return;
		}
	}
	
	@Override
	public void sessionClosed(IoSession ioSession)
	{
		try
		{
			this.getHandler().onSessionClosed(this.getSession());
		}
		catch(Exception e)
		{
			InetSocketAddress endpoint = (InetSocketAddress)ioSession.getRemoteAddress();
			LogHelper.warning(this,"Exception encountered during call to {0}.sessionClosed() for session [{1}:{2}]: {3}",this.getClass().getSimpleName(),endpoint.getHostString(),endpoint.getPort(),ExceptionUtil.getReason(e));
			e.printStackTrace();
			return;
		}
	}
	
	@Override
	protected void initializeSocket()
	{
		NioDatagramConnector conn = new NioDatagramConnector();
		conn.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,  getProperties().getIdleTime());
		conn.getSessionConfig().setReuseAddress(true);
		conn.getFilterChain().addLast("codec",new ProtocolCodecFilter(getProtocolCodecFactory()));
		conn.setConnectTimeoutMillis(getProperties().getConnectTimeout());
		conn.setHandler(this);
		this.setConnector(conn);
	}
}
