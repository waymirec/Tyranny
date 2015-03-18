package net.waymire.tyranny.common.net;

import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;

/**
 * This interface represents the handler class that is used to
 * process network events resulting from a TCP client connecting
 * to a server.
 *
 * @param <T> The type of Packet this handler will process
 */
public interface TcpClientHandler<T extends Packet<? extends Opcode>> extends IpClientHandler<TcpSession, T> 
{

}
