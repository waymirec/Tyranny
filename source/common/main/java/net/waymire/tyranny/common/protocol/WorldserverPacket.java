package net.waymire.tyranny.common.protocol;

import java.nio.ByteBuffer;

public class WorldserverPacket extends Packet<WorldserverOpcode>
{
	public static WorldserverPacket fromByteBuffer(ByteBuffer in)
	{
		int size = in.getInt();
		if(in.remaining() != size-4)
		{
			String err = String.format("Invalid size definition. Received %s, expected %s.", in.remaining(), size);
			throw new IllegalArgumentException(err);
		}
		
		WorldserverOpcode opcode = WorldserverOpcode.valueOf(in.getInt());
		if(opcode == null)
		{
			throw new IllegalArgumentException("Invalid opcode definition");
		}
		
		WorldserverPacket packet = new WorldserverPacket(opcode);
		packet.putBuffer(in);
		packet.prepare();
		return packet;
	}
	
	public static WorldserverPacket fromBytes(byte[] bytes)
	{
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		return fromByteBuffer(buff);
	}
	
	public static WorldserverPacket fromPacket(WorldserverPacket in)
	{
		WorldserverOpcode opcode = in.opcode();
		WorldserverPacket packet = new WorldserverPacket(opcode);
		packet.putBuffer(in.getPayload());
		packet.prepare();
		return packet;
	}
	
	public WorldserverPacket(WorldserverOpcode opcode)
	{
		super(opcode);
	}
	
	@Override
	public WorldserverOpcode opcode()
	{
		return WorldserverOpcode.valueOf(this.getHeader().getInt(4));
	}
}
