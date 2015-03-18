package net.waymire.tyranny.common.mina;

public class WorldserverPacketCodecFactory extends PacketCodecFactory 
{
	public WorldserverPacketCodecFactory()
	{
		this.setEncoder(new WorldserverPacketEncoder());
		this.setDecoder(new WorldserverPacketDecoder());
	}
}
