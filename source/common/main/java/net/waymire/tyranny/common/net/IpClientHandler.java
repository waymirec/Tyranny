package net.waymire.tyranny.common.net;

import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;

public interface IpClientHandler<S extends IpSession, T extends Packet<? extends Opcode>>
{
	public void onConnectFailed(S session);
	public void onSessionOpened(S session);
	public void onSessionIdle(S session);
	public void onSessionClosed(S session);
	public void onMessageReceived(S session,T message);
	public void onException(S session,Throwable t);
	public void setAttribute(String key, Object value);
	public Object getAttribute(String key);
}
