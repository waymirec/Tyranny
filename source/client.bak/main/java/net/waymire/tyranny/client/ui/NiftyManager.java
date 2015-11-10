package net.waymire.tyranny.client.ui;

import com.jme3.niftygui.NiftyJmeDisplay;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;

import net.waymire.tyranny.client.MainGameClient;

public class NiftyManager {
	private final MainGameClient game;
	private NiftyJmeDisplay display;
	
	public NiftyManager(MainGameClient game)
	{
		this.game = game;
		this.display = new NiftyJmeDisplay(game.getAssetManager(),game.getInputManager(),game.getAudioRenderer(),game.getViewPort());
		display.getNifty().registerScreenController(new LoginScreenController(game));
		display.getNifty().addXml("/assets/Interface/Login.xml");
		
		game.getViewPort().addProcessor(display);
		game.getInputManager().setCursorVisible(true);
	}
	
	public NiftyJmeDisplay getDisplay()
	{
		return display;
	}
	
	public Nifty getNifty()
	{
		return display.getNifty();
	}
	
	public Screen getScreen()
	{
		return display.getNifty().getCurrentScreen();
	}
	
	public void gotoScreen(String screen)
	{
		display.getNifty().gotoScreen(screen);
	}
}
