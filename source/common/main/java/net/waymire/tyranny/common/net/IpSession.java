package net.waymire.tyranny.common.net;

import java.net.SocketAddress;

import net.waymire.tyranny.common.GUID;
import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;

public interface IpSession
{
	public boolean isValid();
	public void close();
	public boolean isClosing();
	public boolean isConnected();
	public GUID getId();
	public SocketAddress getRemoteAddress();
	public SocketAddress getLocalAddress();
	public void setAuthenticated(boolean authenticated);
	public boolean getAuthenticated();
	public void setIdleTime(int value);
	public int getIdleTime();
	public void setAttribute(Object key,Object value);
	public Object getAttribute(Object key);
	public void clearAttribute(Object key);
	public void clearAttributes();
	public void send(Packet<? extends Opcode> message);
}
