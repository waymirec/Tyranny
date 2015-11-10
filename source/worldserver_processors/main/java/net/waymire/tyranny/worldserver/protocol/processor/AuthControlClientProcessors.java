package net.waymire.tyranny.worldserver.protocol.processor;

import java.net.InetAddress;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.GUID;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionAttributes;
import net.waymire.tyranny.common.protocol.AuthControlOpcode;
import net.waymire.tyranny.common.protocol.AuthControlPacket;
import net.waymire.tyranny.common.protocol.AuthControlProtocolProcessor;
import net.waymire.tyranny.common.util.InetAddressUtil;
import net.waymire.tyranny.worldserver.PlayerIdentManager;
import net.waymire.tyranny.worldserver.PlayerIdentToken;

public class AuthControlClientProcessors 
{
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
	
	@AuthControlProtocolProcessor(opcode=AuthControlOpcode.PLAYER_IDENT_TOKEN_REQ)
	private static void handlePlayerIdentTokenRequest(TcpSession session,AuthControlPacket packet)
	{
		GUID accountId = GUID.generate(packet.getLong(),packet.getLong());
		InetAddress inetAddress = InetAddressUtil.long2Inet(packet.getLong());
	
		PlayerIdentToken token = new PlayerIdentToken();
		token.setAccountId(accountId);
		token.setInetAddress(inetAddress);
		AppRegistry.getInstance().retrieve(PlayerIdentManager.class).add(token.getId(), token);
		
		AuthControlPacket response = new AuthControlPacket(AuthControlOpcode.PLAYER_IDENT_TOKEN_RSP);
		response.putLong(accountId.getMostSignificantBits());
		response.putLong(accountId.getLeastSignificantBits());
		response.putLong(token.getId().getMostSignificantBits());
		response.putLong(token.getId().getLeastSignificantBits());
		response.prepare();
		session.send(response);
	}
}
