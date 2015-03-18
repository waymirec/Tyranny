package net.waymire.tyranny.common.protocol;

import java.nio.ByteBuffer;

public class LoginserverPacket extends Packet<LoginserverOpcode>
{
	public static LoginserverPacket fromByteBuffer(ByteBuffer in)
	{
		int size = in.getInt();
		if(in.remaining() != size-4)
		{
			String err = String.format("Invalid size definition. Received %s, expected %s.", in.remaining(), size);
			throw new IllegalArgumentException(err);
		}
		
		LoginserverOpcode opcode = LoginserverOpcode.valueOf(in.getInt());
		if(opcode == null)
		{
			throw new IllegalArgumentException("Invalid opcode definition");
		}
		
		LoginserverPacket packet = new LoginserverPacket(opcode);
		packet.putBuffer(in);
		packet.prepare();
		return packet;
	}
	
	public static LoginserverPacket fromBytes(byte[] bytes)
	{
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		return fromByteBuffer(buff);
	}
	
	public static LoginserverPacket fromPacket(LoginserverPacket in)
	{
		LoginserverOpcode opcode = in.opcode();
		LoginserverPacket packet = new LoginserverPacket(opcode);
		packet.putBuffer(in.getPayload());
		packet.prepare();
		return packet;
	}
	
	public LoginserverPacket(LoginserverOpcode opcode)
	{
		super(opcode);
	}
	
	@Override
	public LoginserverOpcode opcode()
	{
		return LoginserverOpcode.valueOf(this.getHeader().getInt(4));
	}
}
