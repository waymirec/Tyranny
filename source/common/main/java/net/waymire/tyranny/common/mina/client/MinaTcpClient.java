package net.waymire.tyranny.common.mina.client;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import net.waymire.tyranny.common.mina.IoSessionHelper;
import net.waymire.tyranny.common.mina.MinaTcpSession;
import net.waymire.tyranny.common.net.IpProperties;
import net.waymire.tyranny.common.net.TcpClient;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;

public class MinaTcpClient<P extends Packet<? extends Opcode>> extends MinaIpClient<TcpSession,P> implements TcpClient
{
	public MinaTcpClient()
	{
		super(MinaTcpSession.class);
	}
	
	@Override
	public void sessionOpened(IoSession ioSession) throws Exception 
	{
		super.sessionOpened(ioSession);
		IoSessionHelper.setTcpNoDelay(ioSession,getProperties().getTcpNoDelay());		
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
	
	@Override
	protected void initializeSocket()
	{
		IpProperties properties = this.getProperties();
		NioSocketConnector conn = new NioSocketConnector();		
		//conn.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,  getProperties().getIdleTime());
		conn.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE,  getProperties().getIdleTime());
		conn.getFilterChain().addLast("codec",new ProtocolCodecFilter(getProtocolCodecFactory()));
		conn.setConnectTimeoutMillis(getProperties().getConnectTimeout());
		conn.setHandler(this);
		this.setConnector(conn);
	}	
}
