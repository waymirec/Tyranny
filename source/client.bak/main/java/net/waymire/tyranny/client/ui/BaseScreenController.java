package net.waymire.tyranny.client.ui;

import net.waymire.tyranny.client.MainGameClient;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public abstract class BaseScreenController implements ScreenController {
	protected MainGameClient game;
	protected Nifty nifty;
	protected Screen screen;
	
	public BaseScreenController(MainGameClient game)
	{
		this.game = game;
	}
	
	@Override
	public void bind(Nifty nifty, Screen screen) 
	{
		this.nifty =  nifty;
		this.screen = screen;
	}

	@Override
	public void onEndScreen() {

	}

	@Override
	public void onStartScreen() {

	}

}
