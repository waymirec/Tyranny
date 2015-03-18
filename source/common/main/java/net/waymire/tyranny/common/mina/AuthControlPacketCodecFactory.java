package net.waymire.tyranny.common.mina;

public class AuthControlPacketCodecFactory extends PacketCodecFactory 
{
	public AuthControlPacketCodecFactory()
	{
		this.setEncoder(new AuthControlPacketEncoder());
		this.setDecoder(new AuthControlPacketDecoder());
	}
}