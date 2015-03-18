package net.waymire.tyranny.common.mina;

import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;
import net.waymire.tyranny.common.protocol.WorldserverPacket;

public class WorldserverPacketDecoder extends PacketDecoder {

	@Override
	protected Packet<? extends Opcode> createPacket(byte[] bytes) 
	{
		return WorldserverPacket.fromBytes(bytes);
	}

}
