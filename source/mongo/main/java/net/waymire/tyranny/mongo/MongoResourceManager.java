package net.waymire.tyranny.mongo;

import org.mongodb.morphia.Datastore;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public interface MongoResourceManager 
{
	public boolean isConnected();
	public String getDatabaseName();
	public MongoClient getMongoClient();
	public DB getDB();
	public Datastore getDatastore();
}
