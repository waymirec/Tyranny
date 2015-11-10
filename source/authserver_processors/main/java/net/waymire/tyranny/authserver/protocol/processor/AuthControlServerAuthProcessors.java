package net.waymire.tyranny.authserver.protocol.processor;

import java.net.InetAddress;

import net.waymire.tyranny.authserver.configuration.AuthConfigKey;
import net.waymire.tyranny.authserver.configuration.AuthserverConfig;
import net.waymire.tyranny.authserver.message.MessageProperties;
import net.waymire.tyranny.authserver.message.MessageTopics;
import net.waymire.tyranny.authserver.net.AuthControlServerSessionAttributes;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.GUID;
import net.waymire.tyranny.common.TyrannyConstants;
import net.waymire.tyranny.common.World;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.StandardMessage;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionAttributes;
import net.waymire.tyranny.common.net.TcpSessionState;
import net.waymire.tyranny.common.protocol.AuthControlIdentResponseCode;
import net.waymire.tyranny.common.protocol.AuthControlOpcode;
import net.waymire.tyranny.common.protocol.AuthControlPacket;
import net.waymire.tyranny.common.protocol.AuthControlProtocolProcessor;
import net.waymire.tyranny.common.task.TaskFuture;
import net.waymire.tyranny.common.task.TaskManager;
import net.waymire.tyranny.common.util.InetAddressUtil;


public class AuthControlServerAuthProcessors 
{
	private AuthControlServerAuthProcessors() { }

	@AuthControlProtocolProcessor(opcode=AuthControlOpcode.AUTH)
	private static void handleAuth(TcpSession session, AuthControlPacket packet)
	{
		// The identification process is complete. Cancel the pending disconnect task.
		TaskFuture disconnectFuture = (TaskFuture)session.getAttribute(TcpSessionAttributes.DISCONNECT_FUTURE);
		AppRegistry.getInstance().retrieve(TaskManager.class).cancel(disconnectFuture);
		session.clearAttribute(TcpSessionAttributes.DISCONNECT_TASK);
		session.clearAttribute(TcpSessionAttributes.DISCONNECT_FUTURE);
		
		AuthserverConfig config = (AuthserverConfig)AppRegistry.getInstance().retrieve(AuthserverConfig.class);
		
		GUID worldId = GUID.generate(packet.getLong(), packet.getLong());
		String hostname = packet.getString();
		String worldname = packet.getString();
		InetAddress ip = InetAddressUtil.long2Inet(packet.getLong());
		Integer port = packet.getInt();
		String authKey = packet.getString();
		int majorVersion = packet.getInt();
		int minorVersion = packet.getInt();
		int maintVersion = packet.getInt();

		LogHelper.info(AuthControlServerAuthProcessors.class, "Worldserver {0} [{1}] connected from {2}:{3}", worldname, worldId, ip, port);
		
		if(!config.getValue(AuthConfigKey.SECURITY_AUTH_KEY).equals(authKey))
		{
			LogHelper.info(AuthControlServerAuthProcessors.class, "Worldserver [{0}] provided an incorrect key: {1}", worldId, authKey);
			AuthControlPacket response = new AuthControlPacket(AuthControlOpcode.AUTH_RESULT);
			response.put(AuthControlIdentResponseCode.INVALID_KEY.byteValue());
			response.prepare();
			session.send(response);
			session.close();
			return;
		}
		
		if(!(TyrannyConstants.AUTH_CONTROLSERVER_VERSION_MAJOR == majorVersion) || !(TyrannyConstants.AUTH_CONTROLSERVER_VERSION_MINOR == minorVersion))
		{
			LogHelper.info(AuthControlServerAuthProcessors.class, "Worldserver [{0}] provided an version: {1}.{2}", worldId, majorVersion, minorVersion);
			session.setAuthenticated(false);
			session.setAttribute(TcpSessionAttributes.SESSION_STATE,TcpSessionState.ERR);
			AuthControlPacket response = new AuthControlPacket(AuthControlOpcode.AUTH_RESULT);
			response.put(AuthControlIdentResponseCode.VERSION_MISMATCH.byteValue());
			response.prepare();
			session.send(response);
			session.close();
			return;
		}

		
		World world = new World();
		world.setGUID(worldId);
		world.setHostname(hostname);
		world.setWorldname(worldname);
		world.setInetAddress(ip);
		world.setPort(port);
		
		session.setAuthenticated(true);
		session.setAttribute(TcpSessionAttributes.SESSION_STATE,TcpSessionState.READY);
		session.setAttribute(AuthControlServerSessionAttributes.WORLD_ID, worldId);
		session.setAttribute(AuthControlServerSessionAttributes.WORLD, world);

		AuthControlPacket response = new AuthControlPacket(AuthControlOpcode.AUTH_RESULT);
		response.put(AuthControlIdentResponseCode.SUCCESS.byteValue());
		response.prepare();
		session.send(response);
		
		Message authenticated = new StandardMessage(session, MessageTopics.AUTHCONTROL_SERVER_CLIENT_AUTHENTICATED);
		authenticated.setProperty(MessageProperties.WORLD_ID, world.getGUID());
		authenticated.setProperty(MessageProperties.WORLD, world);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(authenticated);		
	}
	
}
