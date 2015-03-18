package net.waymire.tyranny.common.mina.server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

import net.waymire.tyranny.common.mina.IoSessionHelper;
import net.waymire.tyranny.common.mina.MinaTcpSession;
import net.waymire.tyranny.common.net.TcpServer;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;

public class MinaTcpServer<P extends Packet<? extends Opcode>> extends MinaIpServer<TcpSession,P>  implements TcpServer
{
	
	public MinaTcpServer(InetSocketAddress address)
	{
		super(address,MinaTcpSession.class);
	}
	
	public MinaTcpServer()
	{
		this(null);
	}
	
	@Override
	public void sessionOpened(IoSession session) throws Exception 
	{
		super.sessionOpened(session);
		IoSessionHelper.setTcpNoDelay(session, getProperties().getTcpNoDelay());
		
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}	

	@Override
	protected void initAcceptor() throws BindException,IOException
	{
		NioSocketAcceptor acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("codec",new ProtocolCodecFilter(getProtocolCodecFactory()));
		acceptor.setReuseAddress(true);
		acceptor.removeListener(this);
		acceptor.addListener((IoServiceListener)this);
		acceptor.setHandler(this);
		
		this.setAcceptor(acceptor);
	}
}