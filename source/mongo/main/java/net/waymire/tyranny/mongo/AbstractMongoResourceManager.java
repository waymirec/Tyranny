package net.waymire.tyranny.mongo;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.util.ExceptionUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

public abstract class AbstractMongoResourceManager implements MongoResourceManager
{
	private MongoClient mongoClient = null;
	private String host = null;
	private Integer port = null;
	private DB database = null;
	private String databaseName = null;
	private String username = null;
	private String password = null;
	
	private Morphia morphia = null;
	private Datastore datastore = null;

	public AbstractMongoResourceManager()
	{
		this.morphia = new Morphia();
	}
	
	public boolean isConnected()
	{
		DBObject ping = new BasicDBObject("ping", "1");
		try 
		{
			database.command(ping);
			return true;
		} 
		catch (MongoException e) 
		{
			return false;
		}
	}
	
	public void setDatabaseName(String databaseName)
	{
		this.databaseName = databaseName;
	}
	
	public String getDatabaseName()
	{
		return databaseName;
	}
	
	public MongoClient getMongoClient()
	{
		return mongoClient;
	}
	
	public Morphia getMorphia()
	{
		return morphia;
	}
	
	public DB getDB()
	{
		return database;
	}

	public Datastore getDatastore()
	{
		return datastore;
	}
	
	abstract protected void init();
	
	protected void setHost(String host)
	{
		this.host = host;
	}
	
	protected String getHost()
	{
		return host;
	}
	
	protected void setPort(int port)
	{
		this.port = port;
	}

	protected Integer getPort()
	{
		return port;
	}
	
	protected void setUsername(String username)
	{
		this.username = username;
	}
	
	protected String getUsername()
	{
		return username;
	}
	
	protected void setPassword(String password)
	{
		this.password = password;
	}
	
	protected String getPassword()
	{
		return password;
	}
	
	protected void connect()
	{
		try
		{
			MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
			builder.writeConcern(WriteConcern.JOURNAL_SAFE);
			
			//MongoCredential auth = MongoCredential.createPlainCredential(this.username, "tyranny", this.password.toCharArray());
			ServerAddress address =  new ServerAddress(this.host, this.port);

			//this.mongoClient = new MongoClient(address, Arrays.asList(auth), builder.build());
			this.mongoClient = new MongoClient(address, builder.build());
			this.database = mongoClient.getDB(databaseName);
			this.datastore = morphia.createDatastore(mongoClient, databaseName);
		}
		catch(UnknownHostException unknownHostException)
		{
			LogHelper.severe(this,  "Failed to connect to database [{0}:{1}]: {2}", this.host,this.port,ExceptionUtil.getReason(unknownHostException));
		}
	}
	
	protected void disconnect()
	{
		mongoClient.close();
	}
}
