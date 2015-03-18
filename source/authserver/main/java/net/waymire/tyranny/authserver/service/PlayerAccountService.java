package net.waymire.tyranny.authserver.service;

import java.util.List;

import net.waymire.tyranny.authserver.persistence.dao.DAOManager;
import net.waymire.tyranny.authserver.persistence.dao.MongoPlayerAccountDAO;
import net.waymire.tyranny.authserver.persistence.model.PlayerAccount;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.GUID;
import net.waymire.tyranny.common.dynload.AutoInitializable;
import net.waymire.tyranny.common.dynload.Autoload;

@Autoload(priority=200)
public class PlayerAccountService implements AutoInitializable
{
	private MongoPlayerAccountDAO playerAccountDAO;
	
	public PlayerAccountService()
	{
	}
	
	@Override
	public void autoInitialize()
	{
		AppRegistry.getInstance().register(PlayerAccountService.class, this);
		this.playerAccountDAO = DAOManager.getPlayerAccountDAO();
	}
	
	@Override
	public void autoDeinitialize()
	{
		AppRegistry.getInstance().unregister(PlayerAccountService.class);
	}

	public List<PlayerAccount> getAllPlayerAccounts()
	{
		return playerAccountDAO.find().asList();
	}
	
	public PlayerAccount getPlayerAccountById(GUID accountId)
	{
		return playerAccountDAO.get(accountId);
	}
	
	public PlayerAccount getPlayerAccountByName(String userName)
	{
		return playerAccountDAO.get(userName);
	}
	
	public void deletePlayerAccountById(GUID accountId)
	{
		playerAccountDAO.delete(accountId);
	}
	
	public void deletePlayerAccountByName(String userName)
	{
		playerAccountDAO.delete(userName);
	}
	
	public void deletePlayerAccount(PlayerAccount playerAccount)
	{
		playerAccountDAO.delete(playerAccount);
	}
	
	public void savePlayerAccount(PlayerAccount playerAccount)
	{
		playerAccountDAO.save(playerAccount);
	}	
}
