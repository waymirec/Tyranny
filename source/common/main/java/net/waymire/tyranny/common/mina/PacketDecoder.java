package net.waymire.tyranny.common.mina;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;

abstract public class PacketDecoder extends CumulativeProtocolDecoder 
{
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception
	{
		while(in.remaining() > 0)
		{
			int size = in.getInt(in.position());
			if(in.remaining() >= size)
			{
				byte[] bytes = new byte[size];
				in.get(bytes);
				Packet<? extends Opcode> packet = createPacket(bytes);
				out.write(packet);
			}
			return !(in.remaining() > 0);
		}
		return false;
	}
	
	@Override
	public void dispose(IoSession session) throws Exception {
		
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
	
	abstract protected Packet<? extends Opcode> createPacket(byte[] bytes);
}
