package net.waymire.tyranny.common.configuration;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.xml.XmlSchemaValidator;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XmlConfiguration implements Configuration {
	private final URL configURL;
	private final URL xsdURL;
	private Document doc;
	private XPath xpath;
	
	public XmlConfiguration(URL configFile,URL xsdFile) throws ConfigException
	{
		this.configURL = configFile;
		this.xsdURL = xsdFile;
		init();
		validate();
	}
	
	@Override
	public String getString(String path) throws ConfigPathException 
	{		
		return getStringValue(path);
	}

	public String getString(String path,String defaultValue) throws ConfigPathException
	{
		String value = getStringValue(path);
		return (value != null) ? value : defaultValue;
		
	}
	
	@Override
	public Integer getInteger(String path) throws ConfigPathException
	{
		return Integer.parseInt(getStringValue(path));		
	}

	public Integer getInteger(String path, Integer defaultValue) throws ConfigPathException
	{
		String value = getStringValue(path);
		return (value != null) ? Integer.parseInt(value) : defaultValue;
	}
	
	@Override
	public BigInteger getBigInteger(String key) throws ConfigPathException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getBoolean(String path) throws ConfigPathException
	{
		return Boolean.valueOf(getStringValue(path));
	}

	@Override
	public Byte getByte(String path) throws ConfigPathException
	{
		return Byte.valueOf(getStringValue(path));
	}

	@Override
	public Double getDouble(String path) throws ConfigPathException
	{
		return Double.valueOf(getStringValue(path));
	}

	@Override
	public Float getFloat(String path) throws ConfigPathException
	{
		return Float.valueOf(getStringValue(path));
	}

	@Override
	public Long getLong(String path) throws ConfigPathException
	{
		return Long.valueOf(getStringValue(path));
	}

	@Override
	public Short getShort(String path) throws ConfigPathException
	{
		return Short.valueOf(getStringValue(path));
	}

	private String getStringValue(String path) throws ConfigPathException
	{
		try
		{
			XPathExpression expr = xpath.compile(path);
			return (String)expr.evaluate(this.doc,XPathConstants.STRING);
		}
		catch(Exception e)
		{
			throw new ConfigPathException(e);
		}
	}
	
	/**
	 * Returns the Configuration value identified by <b>key</b> as a <code>NodeList</code> 
	 * @param	key	key value of the entry to retrieve.
	 * @return	NodeList
	 */	
	public NodeList getNodeList(String path) throws ConfigPathException
	{
		try
		{
			XPathExpression expr = xpath.compile(path);
			return (NodeList)expr.evaluate(this.doc,XPathConstants.NODESET);
		}
		catch(Exception e)
		{
			throw new ConfigPathException(e);
		}
	}

	private void verifyConfigURL() throws ConfigFileException
	{
		if(configURL == null)
		{
			throw new ConfigFileNull();
		}

		try
		{
			configURL.openConnection();
		}
		catch (IOException ioe)
		{
			throw new ConfigFileNotFound(configURL.toString());
		}
	}
	
	private void init() throws ConfigFileException
	{
		verifyConfigURL();		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			this.doc = builder.parse(this.configURL.toString());
		}
		catch(Exception e)
		{
			throw new ConfigFileException(e);
		}
		XPathFactory xpathFactory = XPathFactory.newInstance();
		this.xpath = xpathFactory.newXPath();
	}
	
	private void validate() throws ConfigException
	{
		XmlSchemaValidator validator = new XmlSchemaValidator(xsdURL);
		try
		{
			boolean valid = validator.validate(configURL);
			LogHelper.info(this, "Configuration [{0}] with XSD [{1}] is {2}.", configURL, xsdURL, (valid ? "valid" : "not valid"));
			if(!valid)
			{
				throw new InvalidConfigException(validator.getError());
			}
		}
		catch(IOException ioe)
		{
			throw new ConfigFileException(ioe);
		}
	}
}
