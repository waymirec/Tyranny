package net.waymire.tyranny.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.waymire.tyranny.client.appstate.GameState;
import net.waymire.tyranny.client.component.GameComponent;
import net.waymire.tyranny.common.annotation.LockField;
import net.waymire.tyranny.common.annotation.Locked;
import net.waymire.tyranny.common.annotation.Locked.LockMode;

public class BasicGameEntity implements GameEntity 
{
	@LockField
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	
	private final Map<String,Object> attributes = new HashMap<String,Object>();
	private final Map<Class<? extends GameComponent>,GameComponent> components = new HashMap<Class<? extends GameComponent>,GameComponent>();

	@Override
	@Locked(mode=LockMode.WRITE)
	public void setAttribute(String key, Object value) 
	{
		attributes.put(key, value);
	}

	@Override
	@Locked(mode=LockMode.READ)
	public Object getAttribute(String key) 
	{
		return attributes.get(key);
	}

	@Override
	@Locked(mode=LockMode.READ)
	public boolean hasAttribute(String key) 
	{
		return attributes.containsKey(key);
	}

	@Override
	@Locked(mode=LockMode.WRITE)
	public void attachComponent(GameComponent component) 
	{
		components.put(component.getClass(),component);
		if(component instanceof GameState)
		{
			/*
			@SuppressWarnings("unchecked")
			Class<GameState> clazz = (Class<GameState>)component.getClass();
			GameStateManager gameStateManager = AppRegistry.getInstance().retrieve(GameStateManager.class);
			gameStateManager.register(clazz, (GameState)component);
			gameStateManager.attach(clazz);
			*/
		}
	}

	@Override
	@Locked(mode=LockMode.WRITE)
	public void detachComponent(Class<? extends GameComponent> componentClass) 
	{
		components.remove(componentClass);
		if(GameState.class.isAssignableFrom(componentClass))
		{
			/*
			@SuppressWarnings("unchecked")
			Class<GameState> clazz = (Class<GameState>)componentClass;
			GameStateManager gameStateManager = AppRegistry.getInstance().retrieve(GameStateManager.class);
			gameStateManager.detach(clazz);
			*/
		}
	}

	@Override
	@Locked(mode=LockMode.READ)
	public boolean hasComponent(Class<? extends GameComponent> componentClass) 
	{
		return components.containsKey(componentClass);
	}

	@Override
	@Locked(mode=LockMode.READ)
	public List<GameComponent> getComponents() 
	{
		return new ArrayList<GameComponent>(components.values());
	}

	@Override
	@Locked(mode=LockMode.READ)
	public GameComponent getComponent(Class<? extends GameComponent> componentClass) 
	{
		return components.get(componentClass);
	}

	@Override
	public void update(float tpf) 
	{
		// TODO Auto-generated method stub

	}
}
