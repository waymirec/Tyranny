package net.waymire.tyranny.authserver.persistence.dao;

import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoClient;

import net.waymire.tyranny.authserver.persistence.model.PlayerAccount;
import net.waymire.tyranny.common.GUID;

public class MongoPlayerAccountDAO extends BasicDAO<PlayerAccount,GUID>
{
	public MongoPlayerAccountDAO(MongoClient mongo, Morphia morphia, String databaseName)
	{
		super(mongo,morphia,databaseName);
	}
	
	public PlayerAccount get(String username) 
	{
		return this.findOne("username", username);
	}

	public void delete(GUID accountId)
	{
		Query<PlayerAccount> query = this.getDatastore().createQuery(PlayerAccount.class).field("id").equal(accountId);
		this.deleteByQuery(query);
	}
	
	public void delete(String username) 
	{
		Query<PlayerAccount> query = this.getDatastore().createQuery(PlayerAccount.class).field("username").equal(username);
		this.deleteByQuery(query);
	}
}