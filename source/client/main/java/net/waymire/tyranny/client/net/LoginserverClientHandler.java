package net.waymire.tyranny.client.net;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.client.message.MessageProperties;
import net.waymire.tyranny.client.message.MessageTopics;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.CHAP;
import net.waymire.tyranny.common.Environment;
import net.waymire.tyranny.common.annotation.Locked;
import net.waymire.tyranny.common.annotation.Locked.LockMode;
import net.waymire.tyranny.common.auth.UsernamePasswordCredentials;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.MessageProcessor;
import net.waymire.tyranny.common.message.StandardMessage;
import net.waymire.tyranny.common.net.TcpClientHandler;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.net.TcpSessionAttributes;
import net.waymire.tyranny.common.protocol.LoginserverAuthOpcode;
import net.waymire.tyranny.common.protocol.LoginserverOpcode;
import net.waymire.tyranny.common.protocol.LoginserverPacket;
import net.waymire.tyranny.common.protocol.ProtocolHandler;
import net.waymire.tyranny.common.util.ExceptionUtil;
import net.waymire.tyranny.common.util.InetAddressUtil;

public class LoginserverClientHandler implements TcpClientHandler<LoginserverPacket>
{
	private final Map<String,Object> attributes = new HashMap<String,Object>();
	
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private ProtocolHandler<TcpSession,LoginserverPacket> protocolHandler;
	private UsernamePasswordCredentials credentials;

	public LoginserverClientHandler(ProtocolHandler<TcpSession,LoginserverPacket> protocolHandler)
	{
		this.protocolHandler = protocolHandler;
		AppRegistry.getInstance().retrieve(MessageManager.class).load(this);
	}
	
	public void setProtocolHandler(ProtocolHandler<TcpSession,LoginserverPacket> protocolHandler)
	{
		this.protocolHandler = protocolHandler;
	}
	
	@Override
	public void onSessionOpened(TcpSession session)
	{
		session.setAttribute(LoginserverClientSessionAttributes.LOGINSERVERCLIENT_SESSION_STATE, LoginserverClientSessionState.CONNECTED);
		
		session.setAuthenticated(false);
		
		session.setAttribute(TcpSessionAttributes.LAST_PING_TX_SEQ,Long.valueOf(0));
		session.setAttribute(TcpSessionAttributes.LAST_PING_TX_TIME,Long.valueOf(0));
		session.setAttribute(TcpSessionAttributes.LAST_PONG_RX_SEQ,Long.valueOf(0));
		session.setAttribute(TcpSessionAttributes.LAST_PONG_RX_TIME,(long)0);
		
		session.setAttribute(TcpSessionAttributes.LAST_PING_RX_SEQ,Long.valueOf(0));
		session.setAttribute(TcpSessionAttributes.LAST_PING_RX_TIME,Long.valueOf(0));
		session.setAttribute(TcpSessionAttributes.LAST_PONG_TX_SEQ,Long.valueOf(0));
		session.setAttribute(TcpSessionAttributes.LAST_PONG_TX_TIME,Long.valueOf(0));

		session.setAttribute(LoginserverClientSessionAttributes.LOGINSERVERCLIENT_SESSION_STATE, LoginserverClientSessionState.PENDING_AUTH);
		
		Message connected = new StandardMessage(session, MessageTopics.LOGINSERVER_CLIENT_CONNECT_SUCCESS);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(connected);
		
		final LoginserverPacket ident = new LoginserverPacket(LoginserverOpcode.AUTH);
		ident.putInt(LoginserverAuthOpcode.IDENT.intValue());
		ident.putLong(InetAddressUtil.inet2Long(InetAddressUtil.getLocalhost()));
		ident.putString(Environment.getCountry());
		ident.putString(Environment.getOperatingSystem());
		ident.putString(Environment.getArchitecture());
		ident.putString(Environment.getJavaVersion());
		ident.putString(Environment.getOwner());
		ident.putString(Environment.getLanguage());
		ident.putString(Environment.getEndian());
		ident.putString("tyranny");
		ident.put((byte)0);
		ident.put((byte)1);
		ident.putShort((short)0);
		ident.putInt(12345);
		ident.putString(credentials.getUsername());
		ident.prepare();
		
		session.send(ident);
	}
	
	@Override
	public void onSessionClosed(TcpSession session)
	{
		LogHelper.info(this,"Lost connection to server: [{0}].", ((InetSocketAddress)session.getRemoteAddress()).getHostString());
		synchronized(session)
		{
			session.setAttribute(LoginserverClientSessionAttributes.LOGINSERVERCLIENT_SESSION_STATE, LoginserverClientSessionState.NULL);
		}

		Message disconnected = new StandardMessage(session, MessageTopics.LOGINSERVER_CLIENT_DISCONNECTED);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(disconnected);		
	}
	
	@Override
	public void onConnectFailed(TcpSession session) 
	{
		Message connectionFailed = new StandardMessage(session, MessageTopics.LOGINSERVER_CLIENT_CONNECT_FAILED);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(connectionFailed);
	}

	@Override
	public void onSessionIdle(TcpSession session) 
	{
		Message idle = new StandardMessage(session, MessageTopics.LOGINSERVER_CLIENT_SESSION_IDLE);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(idle);
	}
	
	@Override
	public void onMessageReceived(TcpSession session,LoginserverPacket message) 
	{
		lock.readLock().lock();
		try
		{
			if(protocolHandler != null)
			{
				protocolHandler.handle(session, message);
			}
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	@Override
	public void onException(TcpSession session, Throwable t) 
	{
		LogHelper.severe(this, "EXCEPTION [{0}]: {1}", session, ExceptionUtil.getReason(t));
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
	
	@Override
	public void setAttribute(String key, Object value)
	{
		attributes.put(key, value);
	}
	
	@Override
	public Object getAttribute(String key)
	{
		return attributes.get(key);
	}
	
	protected void finalize()
	{
		AppRegistry.getInstance().retrieve(MessageManager.class).unload(this);
	}
	
	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_LOGIN_REQUEST)
	private void onLoginRequestMessage(Message message)
	{
		String username = (String)message.getProperty(MessageProperties.LOGINSERVER_LOGIN_USERNAME);
		String password = (String)message.getProperty(MessageProperties.LOGINSERVER_LOGIN_PASSWORD);
		this.credentials = new UsernamePasswordCredentials(username,password);
	}
	
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_AUTH_CHALLENGE)
	private void onAuthChallengeMessage(Message message)
	{
		final TcpSession session = (TcpSession)message.getSource();
		final byte[] challenge = (byte[])message.getProperty(MessageProperties.LOGINSERVER_AUTH_CHALLENGE_BYTES);
		
		CHAP chap = new CHAP();
		chap.setSecret(credentials.getPassword());
		chap.setChallenge(challenge);
		
		LoginserverPacket response = new LoginserverPacket(LoginserverOpcode.AUTH);
		response.putInt(LoginserverAuthOpcode.CHAP_PROOF.intValue());
		response.put(chap.getProof());
		response.prepare();
		session.send(response);
	}
	
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_AUTH_SUCCESS)
	private void onAuthSuccessMessage(Message message)
	{
		TcpSession session = (TcpSession)message.getSource();
		LogHelper.info(this, "User [{0}] at session [{1}] successfully authenticated.", credentials.getUsername(), session);
	}
	
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_AUTH_FAILED)
	private void onAuthFailedMessage(Message message)
	{
		TcpSession session = (TcpSession)message.getSource();
		LogHelper.info(this, "User [{0}] at session [{1}] failed to authenticate. Disconnecting...", credentials.getUsername(), session);
		session.close();
	}
}
