package net.waymire.tyranny.authserver.protocol.processor;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.waymire.tyranny.authserver.message.MessageProperties;
import net.waymire.tyranny.authserver.message.MessageTopics;
import net.waymire.tyranny.authserver.net.LoginserverSessionAttributes;
import net.waymire.tyranny.authserver.net.LoginserverSessionState;
import net.waymire.tyranny.authserver.persistence.model.PlayerAccount;
import net.waymire.tyranny.authserver.service.ClientSessionService;
import net.waymire.tyranny.authserver.service.PlayerAccountService;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.CHAP;
import net.waymire.tyranny.common.ClientGameInformation;
import net.waymire.tyranny.common.ClientInformation;
import net.waymire.tyranny.common.ClientSystemInformation;
import net.waymire.tyranny.common.GUID;
import net.waymire.tyranny.common.delegate.DelegateImpl;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.StandardMessage;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionAttributes;
import net.waymire.tyranny.common.net.TcpSessionState;
import net.waymire.tyranny.common.protocol.LoginserverAuthResult;
import net.waymire.tyranny.common.protocol.LoginserverAuthOpcode;
import net.waymire.tyranny.common.protocol.LoginserverOpcode;
import net.waymire.tyranny.common.protocol.LoginserverPacket;
import net.waymire.tyranny.common.protocol.LoginserverProtocolProcessor;
import net.waymire.tyranny.common.protocol.ProtocolProcessorDelegate;
import net.waymire.tyranny.common.task.TaskFuture;
import net.waymire.tyranny.common.task.TaskManager;
import net.waymire.tyranny.common.util.Digest;
import net.waymire.tyranny.common.util.InetAddressUtil;

public class LoginserverAuthProcessors 
{
	private static final Map<LoginserverAuthOpcode,DelegateImpl> delegates = new HashMap<>();
	
	private LoginserverAuthProcessors() { }

	static
	{
		delegates.put(LoginserverAuthOpcode.IDENT,      new ProtocolProcessorDelegate<TcpSession,LoginserverPacket>(LoginserverAuthProcessors.class, "handleIdent", LoginserverPacket.class));
		delegates.put(LoginserverAuthOpcode.CHAP_PROOF, new ProtocolProcessorDelegate<TcpSession,LoginserverPacket>(LoginserverAuthProcessors.class, "handleChallengeProof", LoginserverPacket.class));
		delegates.put(LoginserverAuthOpcode.READY,      new ProtocolProcessorDelegate<TcpSession,LoginserverPacket>(LoginserverAuthProcessors.class, "handleReady", LoginserverPacket.class));
	}

	@LoginserverProtocolProcessor(opcode=LoginserverOpcode.AUTH)
	private static void handleAuth(TcpSession session, LoginserverPacket packet)
	{
		LoginserverAuthOpcode opcode = LoginserverAuthOpcode.valueOf(packet.getInt());
		if(delegates.containsKey(opcode))
		{
			if(LogHelper.isDebugEnabled(LoginserverAuthProcessors.class))
			{
				LogHelper.debug(LoginserverAuthProcessors.class, "AUTH Opcode [{0}] Received From Endpoint [{1}].", opcode, ((InetSocketAddress)session.getRemoteAddress()).getHostString());
			}
			delegates.get(opcode).invoke(session,packet);
		}
		else
		{
			LogHelper.warning(LoginserverAuthProcessors.class, "Unknown AUTH Opcode [{0}] Received From Endpoint [{1}].", opcode, ((InetSocketAddress)session.getRemoteAddress()).getHostString());
		}
	}

	
	@SuppressWarnings("unused")
	private static void handleIdent(TcpSession session,LoginserverPacket packet)
	{
		try
		{
			session.setAttribute(LoginserverSessionAttributes.LOGINSERVER_SESSION_STATE,LoginserverSessionState.IDENT_RCVD);
			
			InetAddress ip = InetAddressUtil.long2Inet(packet.getLong());
			String country = packet.getString();
			String os = packet.getString();
			String platform = packet.getString();
			String javaVersion = packet.getString();
			String owner = packet.getString();
			String language = packet.getString();
			String endian = packet.getString();
			String game = packet.getString();
			int majorVersion = (int)packet.get();			
			int minorVersion = (int)packet.get();
			int maintVersion = (int)packet.getShort();
			int build = packet.getInt();
			String username = packet.getString();
			
			ClientSystemInformation csi = new ClientSystemInformation();
			csi.setIp(ip);
			csi.setCountry(country);
			csi.setOperatingSystem(os);
			csi.setPlatform(platform);
			csi.setJavaVersion(javaVersion);
			csi.setOwner(owner);
			csi.setLanguage(language);
			csi.setEndian(endian);
			
			
			ClientGameInformation cgi = new ClientGameInformation();
			cgi.setGame(game);
			cgi.setMajorVersion(majorVersion);
			cgi.setMinorVersion(minorVersion);
			cgi.setMaintenanceVersion(maintVersion);
			cgi.setBuild(build);
			cgi.setUsername(username);
			
			ClientInformation ci = new ClientInformation(csi,cgi);
			session.setAttribute(TcpSessionAttributes.CLIENT_INFO,ci);

			AppRegistry registry = AppRegistry.getInstance();
			PlayerAccountService service = registry.retrieve(PlayerAccountService.class);
			PlayerAccount acct = new PlayerAccount();
			acct.setAccountId(GUID.generate());
			acct.setFirstname("Chris");
			acct.setLastname("Waymire");
			acct.setPassword("password");
			acct.setUsername("waymirec");
			//service.savePlayerAccount(acct);

			PlayerAccount account = AppRegistry.getInstance().retrieve(PlayerAccountService.class).getPlayerAccountByName(username);
			byte[] challenge = Digest.sha1().digest(username.getBytes());
			CHAP chap = null;
			
			if(account != null)
			{
				if(LogHelper.isDebugEnabled(LoginserverAuthProcessors.class))
				{
					LogHelper.debug(LoginserverAuthProcessors.class, "Received IDENT from user [{0}] [{1}].", username, account.getAccountId());
				}
				
				String password = account.getPassword();
				chap = new CHAP();
				chap.setSecret(password);
				chap.setChallenge(challenge);
				session.setAttribute(LoginserverSessionAttributes.PLAYER_ACCT_ID,account.getAccountId());
				AppRegistry.getInstance().retrieve(ClientSessionService.class).put(account.getAccountId(),  session);
			}
			else
			{
				if(LogHelper.isDebugEnabled(LoginserverAuthProcessors.class))
				{
					LogHelper.debug(LoginserverAuthProcessors.class, "Received IDENT from unknown user [{0}].", username);
				}
			}
			
			session.setAttribute(LoginserverSessionAttributes.CHAP,chap);			
			LoginserverPacket response = new LoginserverPacket(LoginserverOpcode.AUTH);
			response.putInt(LoginserverAuthOpcode.CHAP_CHALLENGE.intValue());
			response.put(challenge);
			response.prepare();
			session.send(response);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private static void handleChallengeProof(TcpSession session,LoginserverPacket packet)
	{
		try
		{
			// The identification process is complete. Cancel the pending disconnect task.
			TaskFuture disconnectFuture = (TaskFuture)session.getAttribute(TcpSessionAttributes.DISCONNECT_FUTURE);
			AppRegistry.getInstance().retrieve(TaskManager.class).cancel(disconnectFuture);
			
			session.setAttribute(LoginserverSessionAttributes.LOGINSERVER_SESSION_STATE, LoginserverSessionState.AUTH_PROOF_RCVD);
			
			byte[] proof = new byte[20];			
			packet.get(proof);
			
			session.setAttribute(TcpSessionAttributes.SESSION_STATE,LoginserverSessionState.AUTH_PROOF_RCVD);
			
			CHAP chap = (CHAP)session.getAttribute(LoginserverSessionAttributes.CHAP);
			if(chap ==  null)
			{
				LogHelper.info(LoginserverAuthProcessors.class, "DEBUG::INVALID AUTH CREDENTIALS");
				session.setAuthenticated(false);
				session.setAttribute(TcpSessionAttributes.SESSION_STATE,TcpSessionState.ERR);
				
				LoginserverPacket response = new LoginserverPacket(LoginserverOpcode.AUTH);
				response.putInt(LoginserverAuthOpcode.CHAP_RESPONSE.intValue());
				response.putBoolean(false);
				response.prepare();			
				session.send(response);
				
				response = new LoginserverPacket(LoginserverOpcode.AUTH_RESULT);
				response.putShort(LoginserverAuthResult.INVALID_CREDENTIALS.shortValue());
				response.prepare();
				session.send(response);
				
				session.close();
				return;
			}
			
			byte[] expected = chap.getProof();
			boolean match = Arrays.equals(proof,expected);
			
			if(match)
			{
				session.setAuthenticated(true);
				session.setAttribute(TcpSessionAttributes.SESSION_STATE,TcpSessionState.READY);
			}
			else
			{
				session.setAuthenticated(false);
				session.setAttribute(TcpSessionAttributes.SESSION_STATE,TcpSessionState.ERR);
			}
			
			LoginserverPacket response = new LoginserverPacket(LoginserverOpcode.AUTH);
			response.putInt(LoginserverAuthOpcode.CHAP_RESPONSE.intValue());
			response.putBoolean(match);
			response.prepare();	
			session.send(response);
			
			if(match)
			{
				response = new LoginserverPacket(LoginserverOpcode.AUTH_RESULT);
				response.putShort(LoginserverAuthResult.READY.shortValue());
				response.prepare();
				session.send(response);

				GUID accountId = (GUID)session.getAttribute(LoginserverSessionAttributes.PLAYER_ACCT_ID);
				Message authenticated = new StandardMessage(session, MessageTopics.LOGINSERVER_CLIENT_AUTHENTICATED);
				authenticated.setProperty(MessageProperties.LOGINSERVER_CLIENT_ACCOUNT_ID, accountId);
				AppRegistry.getInstance().retrieve(MessageManager.class).publish(authenticated);				
				
				LogHelper.info(LoginserverAuthProcessors.class, "DEBUG::AUTH SUCCESS");
			}
			else
			{
				LogHelper.info(LoginserverAuthProcessors.class, "DEBUG::INVALID CREDENTIALS");
				response = new LoginserverPacket(LoginserverOpcode.AUTH_RESULT);
				response.putShort(LoginserverAuthResult.INVALID_CREDENTIALS.shortValue());
				response.prepare();
				session.send(response);
				session.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private static void handleReady(TcpSession session,LoginserverPacket packet)
	{
		
	}
}
