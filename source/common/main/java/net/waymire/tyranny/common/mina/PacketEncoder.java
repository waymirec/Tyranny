package net.waymire.tyranny.common.mina;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;

abstract public class PacketEncoder extends ProtocolEncoderAdapter 
{
	@SuppressWarnings("unchecked")
	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception 
	{
		if (message instanceof Packet)
		{
			Packet<? extends Opcode> packet = (Packet<? extends Opcode>)message;
			
			IoBuffer buff = IoBuffer.wrap(packet.asReadOnlyByteBuffer());
			out.write(buff);
		}
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
}