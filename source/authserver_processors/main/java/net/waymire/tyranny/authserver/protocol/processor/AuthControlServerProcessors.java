package net.waymire.tyranny.authserver.protocol.processor;

import net.waymire.tyranny.authserver.LoginClientSessionManager;
import net.waymire.tyranny.authserver.net.AuthControlServerSessionAttributes;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.GUID;
import net.waymire.tyranny.common.World;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionAttributes;
import net.waymire.tyranny.common.protocol.AuthControlOpcode;
import net.waymire.tyranny.common.protocol.AuthControlPacket;
import net.waymire.tyranny.common.protocol.AuthControlProtocolProcessor;
import net.waymire.tyranny.common.protocol.LoginserverOpcode;
import net.waymire.tyranny.common.protocol.LoginserverPacket;
import net.waymire.tyranny.common.util.InetAddressUtil;

public class AuthControlServerProcessors 
{
	private AuthControlServerProcessors() { }
	
		
	@AuthControlProtocolProcessor(opcode=AuthControlOpcode.PING)
	private static void handlePing(TcpSession session,AuthControlPacket packet)
	{
		Long pingSeq = packet.getLong();
		session.setAttribute(TcpSessionAttributes.LAST_PING_RX_SEQ,pingSeq);
		session.setAttribute(TcpSessionAttributes.LAST_PING_RX_TIME,System.currentTimeMillis());
			
		AuthControlPacket pong = new AuthControlPacket(AuthControlOpcode.PONG);
		pong.putLong(pingSeq);
		pong.prepare();
		session.send(pong);
		session.setAttribute(TcpSessionAttributes.LAST_PONG_TX_SEQ,pingSeq);
		session.setAttribute(TcpSessionAttributes.LAST_PONG_TX_TIME,System.currentTimeMillis());
	}
	
	@AuthControlProtocolProcessor(opcode=AuthControlOpcode.PONG)
	private static void handlePong(TcpSession session,AuthControlPacket packet)
	{
		//Long lastPingSeq = (Long)session.getAttribute(TcpSessionAttributes.LAST_PING_TX_SEQ);
		Long pongSeq = packet.getLong();
			
		session.setAttribute(TcpSessionAttributes.LAST_PONG_RX_SEQ,pongSeq);
		session.setAttribute(TcpSessionAttributes.LAST_PONG_RX_TIME,System.currentTimeMillis());
	}

	@AuthControlProtocolProcessor(opcode=AuthControlOpcode.PLAYER_IDENT_TOKEN_RSP)
	private static void handlePlayerIdentTokenResponse(TcpSession session,AuthControlPacket packet)
	{
		GUID accountId = GUID.generate(packet.getLong(),packet.getLong());
		GUID token = GUID.generate(packet.getLong(),packet.getLong());
		World world = (World)session.getAttribute(AuthControlServerSessionAttributes.WORLD);
		TcpSession loginClientSession = AppRegistry.getInstance().retrieve(LoginClientSessionManager.class).getLoginClientSession(accountId);

		LoginserverPacket response = new LoginserverPacket(LoginserverOpcode.WORLD_INFO);
		response.putLong(token.getMostSignificantBits());
		response.putLong(token.getLeastSignificantBits());
		response.putLong(InetAddressUtil.inet2Long(world.getInetAddress()));
		response.putInt(world.getPort());
		response.prepare();
		loginClientSession.send(response);
		loginClientSession.close();
	}
}