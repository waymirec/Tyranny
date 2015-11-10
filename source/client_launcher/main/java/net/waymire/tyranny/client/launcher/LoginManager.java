package net.waymire.tyranny.client.launcher;

import net.waymire.tyranny.client.message.MessageProperties;
import net.waymire.tyranny.client.message.MessageTopics;
import net.waymire.tyranny.client.net.LoginserverClient;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.CHAP;
import net.waymire.tyranny.common.annotation.Locked;
import net.waymire.tyranny.common.annotation.Locked.LockMode;
import net.waymire.tyranny.common.dynload.AutoInitializable;
import net.waymire.tyranny.common.dynload.Autoload;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.MessageProcessor;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.protocol.LoginserverAuthOpcode;
import net.waymire.tyranny.common.protocol.LoginserverOpcode;
import net.waymire.tyranny.common.protocol.LoginserverPacket;

@Autoload(priority=101)
public class LoginManager implements AutoInitializable
{
	private LoginserverClient loginserverClient;
	
	private String username = null;
	private String password = null;

	public LoginManager()
	{
	}
	
	
	public void autoInitialize()
	{
		this.loginserverClient = new LoginserverClient();
		AppRegistry.getInstance().retrieve(MessageManager.class).load(this);		
	}
	
	public void autoDeinitialize()
	{
		
	}
	
	protected void finalize()
	{
		AppRegistry.getInstance().retrieve(MessageManager.class).unload(this);
	}

	@Locked(mode=LockMode.WRITE)
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_LOGIN_REQUEST)
	private void onLoginRequestMessage(Message message)
	{
		this.username = (String)message.getProperty(MessageProperties.LOGINSERVER_LOGIN_USERNAME);
		this.password = (String)message.getProperty(MessageProperties.LOGINSERVER_LOGIN_PASSWORD);
		this.loginserverClient.connect("localhost", 12346);
	}

	@MessageProcessor(topic=MessageTopics.LOGINSERVER_AUTH_CHALLENGE)
	private void onAuthChallengeMessage(Message message)
	{
		final TcpSession session = (TcpSession)message.getSource();
		final byte[] challenge = (byte[])message.getProperty(MessageProperties.LOGINSERVER_AUTH_CHALLENGE_BYTES);
		
		CHAP chap = new CHAP();
		chap.setSecret(password);
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
		LogHelper.info(this, "User [{0}] at session [{1}] successfully authenticated.", this.username, session);
	}
	
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_AUTH_FAILED)
	private void onAuthFailedMessage(Message message)
	{
		TcpSession session = (TcpSession)message.getSource();
		LogHelper.info(this, "User [{0}] at session [{1}] failed to authenticate. Disconnecting...", this.username, session);
		session.close();
	}
}
