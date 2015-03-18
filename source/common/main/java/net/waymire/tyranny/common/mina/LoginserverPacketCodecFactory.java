package net.waymire.tyranny.common.mina;

public class LoginserverPacketCodecFactory extends PacketCodecFactory 
{
	public LoginserverPacketCodecFactory()
	{
		this.setEncoder(new LoginserverPacketEncoder());
		this.setDecoder(new LoginserverPacketDecoder());
	}
}
