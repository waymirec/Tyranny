package net.waymire.tyranny.common.mina.server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

import net.waymire.tyranny.common.mina.MinaUdpSession;
import net.waymire.tyranny.common.net.UdpServer;
import net.waymire.tyranny.common.net.UdpSession;
import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;

public class MinaUdpServer<P extends Packet<? extends Opcode>> extends MinaIpServer<UdpSession,P> implements UdpServer
{
	
	public MinaUdpServer(InetSocketAddress address)
	{
		super(address,MinaUdpSession.class);
	}
	
	public MinaUdpServer()
	{
		this(null);
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}	

	@Override
	protected void initAcceptor() throws BindException,IOException
	{
		NioDatagramAcceptor acceptor = new NioDatagramAcceptor();
		acceptor.getFilterChain().addLast("codec",new ProtocolCodecFilter(getProtocolCodecFactory()));
		acceptor.removeListener(this);
		acceptor.addListener((IoServiceListener)this);
		acceptor.setHandler(this);
		
		DatagramSessionConfig dcfg = acceptor.getSessionConfig();
		dcfg.setReuseAddress(true);
		
		this.setAcceptor(acceptor);
	}
}
