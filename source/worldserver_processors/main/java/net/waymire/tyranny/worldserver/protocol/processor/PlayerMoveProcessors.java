package net.waymire.tyranny.worldserver.protocol.processor;

import net.waymire.tyranny.common.GUID;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.protocol.WorldserverOpcode;
import net.waymire.tyranny.common.protocol.WorldserverPacket;
import net.waymire.tyranny.common.protocol.WorldserverProtocolProcessor;
import net.waymire.tyranny.worldserver.net.WorldserverSessionAttributes;

public class PlayerMoveProcessors 
{
	@WorldserverProtocolProcessor(opcode=WorldserverOpcode.PLAYER_MOVE_FORWARD)
	private static void handlePlayerMoveForward(TcpSession session, WorldserverPacket packet)
	{
		GUID characterId = (GUID)session.getAttribute(WorldserverSessionAttributes.PLAYER_CHAR_ID);
		Boolean status = packet.getBoolean();
		
		System.out.printf("GOT MOVE FORWARD PACKET FOR CHARACTER [%s] WITH STATUS OF [%s].\n", characterId, status);
	}
	
	@WorldserverProtocolProcessor(opcode=WorldserverOpcode.PLAYER_MOVE_BACKWARD)
	private static void handlePlayerMoveBackward(TcpSession session, WorldserverPacket packet)
	{
		GUID characterId = (GUID)session.getAttribute(WorldserverSessionAttributes.PLAYER_CHAR_ID);
		Boolean status = packet.getBoolean();
		
		System.out.printf("GOT MOVE BACKWARD PACKET FOR CHARACTER [%s] WITH STATUS OF [%s].\n", characterId, status);
	}
	
	@WorldserverProtocolProcessor(opcode=WorldserverOpcode.PLAYER_MOVE_LEFT)
	private static void handlePlayerMoveLeft(TcpSession session, WorldserverPacket packet)
	{
		GUID characterId = (GUID)session.getAttribute(WorldserverSessionAttributes.PLAYER_CHAR_ID);
		Boolean status = packet.getBoolean();
		
		System.out.printf("GOT MOVE LEFT PACKET FOR CHARACTER [%s] WITH STATUS OF [%s].\n", characterId, status);
	}
	
	@WorldserverProtocolProcessor(opcode=WorldserverOpcode.PLAYER_MOVE_RIGHT)
	private static void handlePlayerMoveRight(TcpSession session, WorldserverPacket packet)
	{
		GUID characterId = (GUID)session.getAttribute(WorldserverSessionAttributes.PLAYER_CHAR_ID);
		Boolean status = packet.getBoolean();
		
		System.out.printf("GOT MOVE RIGHT PACKET FOR CHARACTER [%s] WITH STATUS OF [%s].\n", characterId, status);
	}
	
	@WorldserverProtocolProcessor(opcode=WorldserverOpcode.PLAYER_MOVE_JUMP)
	private static void handlePlayerJump(TcpSession session, WorldserverPacket packet)
	{
		GUID characterId = (GUID)session.getAttribute(WorldserverSessionAttributes.PLAYER_CHAR_ID);
		Boolean status = packet.getBoolean();
		
		System.out.printf("GOT JUMP PACKET FOR CHARACTER [%s] WITH STATUS OF [%s].\n", characterId, status);
	}
	
	@WorldserverProtocolProcessor(opcode=WorldserverOpcode.PLAYER_MOVE_ROTATE_LEFT)
	private static void handlePlayerRotateLeft(TcpSession session, WorldserverPacket packet)
	{
		GUID characterId = (GUID)session.getAttribute(WorldserverSessionAttributes.PLAYER_CHAR_ID);
		Boolean status = packet.getBoolean();
		
		System.out.printf("GOT ROTATE LEFT PACKET FOR CHARACTER [%s] WITH STATUS OF [%s].\n", characterId, status);
	}
	
	@WorldserverProtocolProcessor(opcode=WorldserverOpcode.PLAYER_MOVE_ROTATE_RIGHT)
	private static void handlePlayerRotateRight(TcpSession session, WorldserverPacket packet)
	{
		GUID characterId = (GUID)session.getAttribute(WorldserverSessionAttributes.PLAYER_CHAR_ID);
		Boolean status = packet.getBoolean();
		
		System.out.printf("GOT ROTATE RIGHT PACKET FOR CHARACTER [%s] WITH STATUS OF [%s].\n", characterId, status);
	}

}
