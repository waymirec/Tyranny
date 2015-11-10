package net.waymire.tyranny.mongo;

import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import net.waymire.tyranny.common.GUID;

public class GUIDTypeConverter extends TypeConverter 
{
	public GUIDTypeConverter() 
	{
		super(GUID.class);
	}
	
	@Override
	public Object encode(final Object value, final MappedField optionalExtraInfo)
	{
		if(value != null)
		{
			GUID guid = (GUID)value;
			DBObject dbObject = new BasicDBObject();
			dbObject.put("value", guid.toString());
			return dbObject;
		}
		
		return null;
	}
	
	@Override
	public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) 
	{
		BasicDBObject dbObject = (BasicDBObject)fromDBObject;
		return dbObject != null ? GUID.generate((String)dbObject.getString("value")) : null;
	}

}
