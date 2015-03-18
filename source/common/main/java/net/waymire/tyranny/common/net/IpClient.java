package net.waymire.tyranny.common.net;

import java.net.InetSocketAddress;

import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;

public interface IpClient
{
	public void connect(InetSocketAddress serverAddress);
	public void connect(String ip, int port);
	public void disconnect();
	public boolean isConnected();
    public boolean isConnecting();
    public IpClientState getState();
	public void send(Packet<? extends Opcode> message);
}
