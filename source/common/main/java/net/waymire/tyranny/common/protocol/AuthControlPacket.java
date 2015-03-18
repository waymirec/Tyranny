package net.waymire.tyranny.common.protocol;

import java.nio.ByteBuffer;

public class AuthControlPacket extends Packet<AuthControlOpcode> 
{
	public static AuthControlPacket fromByteBuffer(ByteBuffer in)
	{
		int size = in.getInt();
		if(in.remaining() != size-4)
		{
			String err = String.format("Invalid size definition. Received %s, expected %s.", in.remaining(), size);
			throw new IllegalArgumentException(err);
		}
		
		AuthControlOpcode opcode = AuthControlOpcode.valueOf(in.getInt());
		if(opcode == null)
		{
			throw new IllegalArgumentException("Invalid opcode definition");
		}
		
		AuthControlPacket packet = new AuthControlPacket(opcode);
		packet.putBuffer(in);
		packet.prepare();
		return packet;
	}
	
	public static AuthControlPacket fromBytes(byte[] bytes)
	{
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		return fromByteBuffer(buff);
	}
	
	public static AuthControlPacket fromPacket(AuthControlPacket in)
	{
		AuthControlOpcode opcode = in.opcode();
		AuthControlPacket packet = new AuthControlPacket(opcode);
		packet.putBuffer(in.getPayload());
		packet.prepare();
		return packet;
	}
	
	public AuthControlPacket(AuthControlOpcode opcode)
	{
		super(opcode);
	}
	
	@Override
	public AuthControlOpcode opcode()
	{
		return AuthControlOpcode.valueOf(this.getHeader().getInt(4));
	}

}