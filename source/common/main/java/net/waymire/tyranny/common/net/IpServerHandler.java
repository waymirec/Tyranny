package net.waymire.tyranny.common.net;

import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;
import net.waymire.tyranny.common.protocol.ProtocolHandler;

/**
 * This interface represents the handler class that is used to
 * process network events from an IP server.
 * 
 *
 * @param <T> The type of Packet this handler will process
 */
public interface IpServerHandler<S extends IpSession, T extends Packet<? extends Opcode>>
{
	public void setProtocolHandler(ProtocolHandler<S,T> protocolHandler);
	public void onSessionOpened(S session);
	public void onSessionClosed(S session);
	public void onSessionIdle(S session);
	public void onMessageReceived(S session,T message);
	public void onException(S session,Throwable t);
	public void setAttribute(String key, Object value);
	public Object getAttribute(String key);
}
