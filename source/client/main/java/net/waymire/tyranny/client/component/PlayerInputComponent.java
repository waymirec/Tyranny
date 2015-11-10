package net.waymire.tyranny.client.component;

import java.lang.reflect.Field;

import net.waymire.tyranny.client.AppRegistryKeys;
import net.waymire.tyranny.client.PlayerInputConstants;
import net.waymire.tyranny.client.configuration.ClientConfig;
import net.waymire.tyranny.client.configuration.ClientConfigKey;
import net.waymire.tyranny.client.message.MessageProperties;
import net.waymire.tyranny.client.message.MessageTopics;
import net.waymire.tyranny.client.message.MovementAction;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.StandardMessage;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

public class PlayerInputComponent extends AbstractAppState implements GameComponent,ActionListener 
{
	@Override
	public void initialize(AppStateManager stateManager, Application app)
	{

	}
	
	@Override
	public void cleanup()
	{
		
	}
	
	@Override
	public void stateAttached(AppStateManager stateManager)
	{
		ClientConfig config = (ClientConfig)AppRegistry.getInstance().retrieve(AppRegistryKeys.TYRANNY_CLIENT_CONFIG);
		InputManager inputManager = stateManager.getApplication().getInputManager();
		
		registerInputMapping(inputManager, PlayerInputConstants.MOVE_LEFT, config.getValue(ClientConfigKey.INPUT_MOVE_LEFT));
		registerInputMapping(inputManager, PlayerInputConstants.MOVE_RIGHT, config.getValue(ClientConfigKey.INPUT_MOVE_RIGHT));
		registerInputMapping(inputManager, PlayerInputConstants.MOVE_FORWARD, config.getValue(ClientConfigKey.INPUT_MOVE_FORWARD));
		registerInputMapping(inputManager, PlayerInputConstants.MOVE_BACKWARD, config.getValue(ClientConfigKey.INPUT_MOVE_BACKWARD));
		registerInputMapping(inputManager, PlayerInputConstants.MOVE_ROT_LEFT, config.getValue(ClientConfigKey.INPUT_MOVE_ROT_LEFT));
		registerInputMapping(inputManager, PlayerInputConstants.MOVE_ROT_RIGHT, config.getValue(ClientConfigKey.INPUT_MOVE_ROT_RIGHT));
		registerInputMapping(inputManager, PlayerInputConstants.MOVE_JUMP, config.getValue(ClientConfigKey.INPUT_MOVE_JUMP));
		registerInputMapping(inputManager, PlayerInputConstants.MOVE_RUN, config.getValue(ClientConfigKey.INPUT_MOVE_RUN_TOGGLE));
		registerInputMapping(inputManager, PlayerInputConstants.MOVE_STRAFE, config.getValue(ClientConfigKey.INPUT_MOVE_STRAFE));
		registerInputMapping(inputManager, PlayerInputConstants.ACTION_SHOOT, config.getValue(ClientConfigKey.INPUT_ACTION_SHOOT));		
	}
	
	@Override
	public void stateDetached(AppStateManager stateManager)
	{
		stateManager.getApplication().getInputManager().clearMappings();
		stateManager.getApplication().getInputManager().removeListener(this);
	}
	
	@Override
	public void onAction(String binding, boolean keyPressed, float tpf) 
	{
		MessageManager messageManager = AppRegistry.getInstance().retrieve(MessageManager.class);
		Message message = null;

		switch(binding)
		{
			case PlayerInputConstants.MOVE_STRAFE:
			{
				message = new StandardMessage(this, MessageTopics.PLAYER_INPUT_MOVE);
				message.setProperty(MessageProperties.ACTION, MovementAction.STRAFE);
				break;
			}
			case PlayerInputConstants.MOVE_LEFT:
			{
				message = new StandardMessage(this, MessageTopics.PLAYER_INPUT_MOVE);
				message.setProperty(MessageProperties.ACTION, MovementAction.LEFT);
				message.setProperty(MessageProperties.STATUS, keyPressed);
				messageManager.publish(message);
				break;
			}
			case PlayerInputConstants.MOVE_RIGHT:
			{
				message = new StandardMessage(this, MessageTopics.PLAYER_INPUT_MOVE);
				message.setProperty(MessageProperties.ACTION, MovementAction.RIGHT);
				message.setProperty(MessageProperties.STATUS, keyPressed);
				messageManager.publish(message);
				break;
			}
			case PlayerInputConstants.MOVE_FORWARD:
			{
				message = new StandardMessage(this, MessageTopics.PLAYER_INPUT_MOVE);
				message.setProperty(MessageProperties.ACTION, MovementAction.FORWARD);
				message.setProperty(MessageProperties.STATUS, keyPressed);
				messageManager.publish(message);
				break;
			}
			case PlayerInputConstants.MOVE_BACKWARD:
			{
				message = new StandardMessage(this, MessageTopics.PLAYER_INPUT_MOVE);
				message.setProperty(MessageProperties.ACTION, MovementAction.BACKWARD);
				message.setProperty(MessageProperties.STATUS, keyPressed);
				messageManager.publish(message);
				break;
			}
			case PlayerInputConstants.MOVE_JUMP:
			{
				message = new StandardMessage(this, MessageTopics.PLAYER_INPUT_MOVE);
				message.setProperty(MessageProperties.ACTION, MovementAction.JUMP);
				message.setProperty(MessageProperties.STATUS, keyPressed);
				messageManager.publish(message);
				break;
			}
		}
	}

	private void registerInputMapping(InputManager inputManager, String action, String triggerName)
	{
		try
		{
			Field field = KeyInput.class.getField(triggerName);
			Class<?> fieldType = field.getType();
			if(fieldType == int.class)
			{
				inputManager.addMapping(action, new KeyTrigger(field.getInt(null)));
				inputManager.addListener(this, action);
			}
		}
		catch(NoSuchFieldException | IllegalAccessException reflectionException)
		{
		}
	}
}
