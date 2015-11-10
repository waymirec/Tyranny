package net.waymire.tyranny.client.launcher;

import java.util.Locale;

import net.waymire.tyranny.client.message.MessageProperties;
import net.waymire.tyranny.client.message.MessageTopics;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.i18n.ResourceTranslator;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.MessagePriority;
import net.waymire.tyranny.common.message.MessageProcessor;
import net.waymire.tyranny.common.message.StandardMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class MainGUIController 
{
	private ResourceTranslator translator;
	
	@FXML
	private Label usernameLabel;
	@FXML
	private Label passwordLabel;
	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Text statusText;
	
	public MainGUIController()
	{
		AppRegistry.getInstance().retrieve(MessageManager.class).load(this);
	}
	
	@FXML
    private void initialize()
    {
		this.translator = new ResourceTranslator("messages/Messages", Locale.US);
	}
	
	@FXML 
	protected void handlePlayButtonAction(ActionEvent event) 
	{
		statusText.setText(translator.translate("client.gui.status.login.attempting"));
		Message login = new StandardMessage(this, MessageTopics.LOGINSERVER_CLIENT_LOGIN_REQUEST);
		login.setPriority(MessagePriority.URGENT);
		login.setProperty(MessageProperties.LOGINSERVER_LOGIN_USERNAME, usernameField.getText());
		login.setProperty(MessageProperties.LOGINSERVER_LOGIN_PASSWORD, passwordField.getText());
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(login);
    }

	protected void finalize()
	{
		AppRegistry.getInstance().retrieve(MessageManager.class).unload(this);
	}

	@MessageProcessor(topic=MessageTopics.SYSTEM_STARTING)
	private void onSystemStartingMessage(Message message)
	{
		setStatus("client.gui.status.initializing");
	}
	
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_CONNECTING)
	private void onClientConnecting(Message message)
	{
		setStatus("client.gui.status.login.connecting");
	}
	
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_CONNECT_SUCCESS)
	private void onClientConnectSuccess(Message message)
	{
		setStatus("client.gui.status.login.connect.success");
	}
	
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_CONNECT_FAILED)
	private void onClientConnectFailed(Message message)
	{
		setStatus("client.gui.status.login.connect.failed");
	}
	
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_AUTH_SUCCESS)
	private void onClientAuthSuccess(Message message)
	{
		setStatus("client.gui.status.login.auth.successful");
	}

	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_AUTH_FAILED)
	private void onClientAuthFailed(Message message)
	{
		setStatus("client.gui.status.login.auth.failed");
	}

	@MessageProcessor(topic=MessageTopics.WORLDSERVER_CLIENT_CONNECTING)
	private void onWorldserverConnecting(Message message)
	{
		setStatus("client.gui.status.world.connecting");
	}
	
	@MessageProcessor(topic=MessageTopics.WORLDSERVER_CLIENT_CONNECT_SUCCESS)
	private void onWorldserverConnectSuccess(Message message)
	{
		setStatus("client.gui.status.world.connect.success");
	}
	
	@MessageProcessor(topic=MessageTopics.WORLDSERVER_CLIENT_CONNECT_FAILED)
	private void onWorldserverConnectFailed(Message message)
	{
		setStatus("client.gui.status.world.connect.failed");
	}
	
	@MessageProcessor(topic=MessageTopics.WORLDSERVER_CLIENT_IDENT_SUCCESS)
	private void onIdentSuccess(Message message)
	{
		setStatus("client.gui.status.world.connect.success");
	}

	@MessageProcessor(topic=MessageTopics.WORLDSERVER_CLIENT_IDENT_FAILED)
	private void onIdentFailed(Message message)
	{
		setStatus("client.gui.status.world.connect.failed");
	}
	
	@MessageProcessor(topic=MessageTopics.SYSTEM_STARTED)
	private void onSystemStartedMessage(Message message)
	{
		setStatus("client.gui.status.ready");
	}
	
	@MessageProcessor(topic=MessageTopics.ENTER_WORLD)
	private void onEnterWorldMessage(Message message)
	{
		setStatus("client.gui.status.world.enter");
	}

	private void setStatus(String messageKey)
	{
		Platform.runLater(() -> { statusText.setText(translator.translate(messageKey)); });
	}
}
