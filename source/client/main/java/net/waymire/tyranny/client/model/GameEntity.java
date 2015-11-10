package net.waymire.tyranny.client.model;

import java.util.List;

import net.waymire.tyranny.client.component.GameComponent;

public interface GameEntity 
{
	public void setAttribute(String key, Object value);
	public Object getAttribute(String key);
	public boolean hasAttribute(String key);

	public void attachComponent(GameComponent value);
	public void detachComponent(Class<? extends GameComponent> componentClass);
	public boolean hasComponent(Class<? extends GameComponent> componentClass);
	public List<GameComponent> getComponents();
	public GameComponent getComponent(Class<? extends GameComponent> componentClass);

	public void update(float tpf);
}
