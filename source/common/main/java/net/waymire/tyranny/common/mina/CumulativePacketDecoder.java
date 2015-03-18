package net.waymire.tyranny.common.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import net.waymire.tyranny.common.protocol.PacketBuffer;

public class CumulativePacketDecoder extends CumulativeProtocolDecoder {

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,ProtocolDecoderOutput out) throws Exception 
	{
		int size = in.getInt(0);
		if(in.remaining() >= size+4)
		{
			byte[] bytes = new byte[size+4];
			in.get(bytes);
			PacketBuffer pb = PacketBuffer.wrap(bytes);
			out.write(pb);
			return in.hasRemaining();
		}
		return false;
	}
}
