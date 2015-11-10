package net.waymire.tyranny.client.ui;

import java.util.concurrent.Callable;

import net.waymire.tyranny.client.MainGameClient;
import net.waymire.tyranny.client.message.MessageProperties;
import net.waymire.tyranny.client.message.MessageTopics;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.MessageProcessor;
import net.waymire.tyranny.common.message.StandardMessage;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;

public class LoginScreenController extends BaseScreenController 
{
	private TextRenderer status;

	public LoginScreenController(MainGameClient game)
	{
		super(game);
		AppRegistry.getInstance().retrieve(MessageManager.class).load(this);
	}
	
	@Override
	public void bind(Nifty nifty, Screen screen) 
	{
		super.bind(nifty, screen);
		this.status = screen.findElementByName(NiftyConstants.LAYER_ELEMENT).findElementByName(NiftyConstants.PANEL_ELEMENT).findElementByName(NiftyConstants.LOGIN_STATUS_ELEMENT).getRenderer(TextRenderer.class);
    }
	
	public void login()
	{
		setStatusText("Connecting...");
		Message request = new StandardMessage(this, MessageTopics.LOGINSERVER_CLIENT_LOGIN_REQUEST);
		request.setProperty(MessageProperties.LOGINSERVER_LOGIN_USERNAME, getUsername());
		request.setProperty(MessageProperties.LOGINSERVER_LOGIN_PASSWORD, getPassword());
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(request);		
	}
	
	public void quit()
	{
		game.stop();
	}
	
	public String getUsername()
	{
		return screen.findNiftyControl(NiftyConstants.LOGIN_USERNAME_ELEMENT, TextField.class).getRealText();
	}
	
	public String getPassword()
	{
		return screen.findNiftyControl(NiftyConstants.LOGIN_PASSWORD_ELEMENT, TextField.class).getRealText();
	}
	
	/**
     * sets the status text of the main login view, threadsafe
     * @param text
     */
    public void setStatusText(final String text) {
        game.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
            	status.setText(text);
                return null;
            }
        });
    }

    @Override
    protected void finalize()
    {
    	AppRegistry.getInstance().retrieve(MessageManager.class).unload(this);
    }
    
    @MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_CONNECT_SUCCESS)
	private void onClientConnected(Message message)
	{
    	setStatusText("Logging in...");
	}
    
    @MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_CONNECT_FAILED)
	private void onConnectFailed(Message message)
	{
		setStatusText("Connect failed.");
	}
    
    @MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_DISCONNECTED)
	private void onClientDisconnected(Message message)
	{
    	setStatusText("Disconnected.");
	}
    
    @MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_AUTH_SUCCESS)
	private void onAuthSuccessMessage(Message message)
	{
    	setStatusText("Authentication successful.");
	}
	
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_AUTH_FAILED)
	private void onAuthFailedMessage(Message message)
	{
		setStatusText("Authentication failed.");
	}	
}
