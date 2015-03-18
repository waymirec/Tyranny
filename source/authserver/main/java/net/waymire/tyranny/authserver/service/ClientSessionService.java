package net.waymire.tyranny.authserver.service;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.GUID;
import net.waymire.tyranny.common.ObjectManager;
import net.waymire.tyranny.common.dynload.AutoInitializable;
import net.waymire.tyranny.common.dynload.Autoload;
import net.waymire.tyranny.common.net.TcpSession;

@Autoload(priority=201)
public class ClientSessionService extends ObjectManager<GUID, TcpSession> implements AutoInitializable
{

	@Override
	public void autoInitialize()
	{
		AppRegistry.getInstance().register(ClientSessionService.class, this);
	}
	
	@Override
	public void autoDeinitialize()
	{
		AppRegistry.getInstance().unregister(ClientSessionService.class);
	}
}
