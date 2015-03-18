package net.waymire.tyranny.common.xml;

import java.io.IOException;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class XmlSchemaValidator {
	private final URL schemaURL;
	private URL xmlURL = null;
	private boolean valid = false;
	private String err;
	
	public XmlSchemaValidator(URL schemaURL) {
		this.schemaURL = schemaURL;
	}
	
	public boolean isValid()
	{
		return valid;
	}
	
	public String getError()
	{
		return err;
	}
	
	public URL getSchemaURL()
	{
		return schemaURL;
	}
	
	public URL getXmlURL()
	{
		return xmlURL;
	}
		
	public boolean validate(String xmlFileName) throws IOException
	{
		return validate(new URL(xmlFileName));
	}
	
	public boolean validate(URL xmlURL) throws IOException
	{
		this.xmlURL = xmlURL;
		
		try {			
			// 1. Lookup a factory for the W3C XML Schema language
			SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

			// 2. Compile the schema.
			// Here the schema is loaded from a java.io.File, but you could use
			// a java.net.URL or a javax.xml.transform.Source instead.
			Schema schema = schemaFactory.newSchema(schemaURL);

			// 3. Get a validator from the schema.
			Validator validator = schema.newValidator();

			// 4. Parse the document you want to check.
			Source source = new StreamSource(xmlURL.openStream());

			// 5. Check the document
			validator.validate(source);
			valid = true;
			err = null;
		} catch (SAXException e) {
			valid = false;
			err = e.getMessage();
		}
		
		return valid;
	}
}
