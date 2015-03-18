package net.waymire.tyranny.authserver.persistence.dao;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.waymire.tyranny.authserver.persistence.DatabaseResourceManager;
import net.waymire.tyranny.common.AppRegistry;

public class DAOManager 
{
	private static final ReadWriteLock lock = new ReentrantReadWriteLock();
	private static MongoPlayerAccountDAO playerAccountDAO = null;
	
	public static MongoPlayerAccountDAO getPlayerAccountDAO()
	{
		lock.readLock().lock();
		try
		{
			if (playerAccountDAO == null)
			{
				lock.readLock().unlock();
				lock.writeLock().lock();
				try
				{
					DatabaseResourceManager dbmgr = AppRegistry.getInstance().retrieve(DatabaseResourceManager.class);
					playerAccountDAO = new MongoPlayerAccountDAO(dbmgr.getMongoClient(), dbmgr.getMorphia(), dbmgr.getDatabaseName());
				}
				finally
				{
					lock.readLock().lock();
					lock.writeLock().unlock();
				}
			}
		}
		finally
		{
			lock.readLock().unlock();
		}
		
		return playerAccountDAO;
	}

}
