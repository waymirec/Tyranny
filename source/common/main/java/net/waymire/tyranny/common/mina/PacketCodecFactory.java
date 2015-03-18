package net.waymire.tyranny.common.mina;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

abstract public class PacketCodecFactory implements ProtocolCodecFactory {
	private ProtocolEncoder encoder;
	private ProtocolDecoder decoder;
	
	protected PacketCodecFactory()
	{
	}
	
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}
	
	protected void setEncoder(ProtocolEncoder encoder)
	{
		this.encoder = encoder;
	}
	
	protected ProtocolEncoder getEncoder()
	{
		return encoder;
	}
	
	protected void setDecoder(ProtocolDecoder decoder)
	{
		this.decoder = decoder;
	}
	
	protected ProtocolDecoder getDecoder()
	{
		return decoder;
	}
}
