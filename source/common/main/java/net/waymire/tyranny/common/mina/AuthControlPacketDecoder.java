package net.waymire.tyranny.common.mina;

import net.waymire.tyranny.common.protocol.AuthControlPacket;
import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;

public class AuthControlPacketDecoder extends PacketDecoder 
{
	@Override
	protected Packet<? extends Opcode> createPacket(byte[] bytes) 
	{
		return AuthControlPacket.fromBytes(bytes);
	}

}