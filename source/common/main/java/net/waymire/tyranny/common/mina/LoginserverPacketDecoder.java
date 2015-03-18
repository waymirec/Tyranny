package net.waymire.tyranny.common.mina;

import net.waymire.tyranny.common.protocol.LoginserverPacket;
import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;

public class LoginserverPacketDecoder extends PacketDecoder {

	@Override
	protected Packet<? extends Opcode> createPacket(byte[] bytes) 
	{
		return LoginserverPacket.fromBytes(bytes);
	}

}
