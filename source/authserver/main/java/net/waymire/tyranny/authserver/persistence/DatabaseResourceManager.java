package net.waymire.tyranny.authserver.persistence;

import net.waymire.tyranny.authserver.configuration.AuthConfigKey;
import net.waymire.tyranny.authserver.configuration.AuthserverConfig;
import net.waymire.tyranny.authserver.persistence.model.PlayerAccount;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.dynload.AutoInitializable;
import net.waymire.tyranny.common.dynload.Autoload;
import net.waymire.tyranny.mongo.AbstractMongoResourceManager;

@Autoload(priority=50)
public class DatabaseResourceManager extends AbstractMongoResourceManager implements AutoInitializable
{

	@Override
	public void autoInitialize()
	{
		AppRegistry.getInstance().register(DatabaseResourceManager.class, this);
		init();
		connect();
	}
	
	@Override
	public void autoDeinitialize()
	{
		disconnect();
		AppRegistry.getInstance().unregister(DatabaseResourceManager.class);
	}
	
	protected void init()
	{
		AuthserverConfig config = AppRegistry.getInstance().retrieve(AuthserverConfig.class);
		this.setHost(config.getValue(AuthConfigKey.DB_HOST));
		this.setPort(config.getValueAs(Integer.class, AuthConfigKey.DB_PORT));
		this.setUsername(config.getValue(AuthConfigKey.DB_USERNAME));
		this.setPassword(config.getValue(AuthConfigKey.DB_PASSWORD));
		this.setDatabaseName(config.getValue(AuthConfigKey.DB_DBNAME));
		this.getMorphia().map(PlayerAccount.class);
	}	
}
