package net.waymire.tyranny.client;

import java.util.List;

import net.waymire.tyranny.client.appstates.GameState;
import net.waymire.tyranny.common.ObjectRegistry;

public class GameStateManager 
{
	private final MainGameClient game;
	private final ObjectRegistry registry = new ObjectRegistry();
	
	public GameStateManager(MainGameClient game)
	{
		this.game = game;
	}
	
	public <T extends GameState> void register(Class<T> clazz, T state)
	{
		registry.put(clazz, state);
	}
	
	public <T extends GameState> T retrieve(Class<T> clazz)
	{
		return registry.get(clazz);
	}
	
	public <T extends GameState> void attach(Class<T> gameState)
	{
		GameState state = registry.get(gameState);
		if(state == null)
		{
			throw new IllegalArgumentException();
		}
		game.getStateManager().attach(state);
	}
	
	public <T extends GameState> void detach(Class<T> gameState)
	{
		GameState state = registry.get(gameState);
		if(state == null)
		{
			throw new IllegalArgumentException();
		}
		game.getStateManager().detach(state);
	}
	
	public void detachAll()
	{
		List<GameState> states = registry.values();
		for(GameState state : states)
		{
			game.getStateManager().detach(state);
		}
	}
}
