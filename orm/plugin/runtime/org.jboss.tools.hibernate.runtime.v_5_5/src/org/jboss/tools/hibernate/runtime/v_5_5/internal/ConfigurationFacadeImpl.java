package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jboss.tools.hibernate.runtime.common.AbstractConfigurationFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public class ConfigurationFacadeImpl extends AbstractConfigurationFacade {

	private EntityResolver entityResolver = null;
	private INamingStrategy namingStrategy = null;
	private ArrayList<IPersistentClass> addedClasses = new ArrayList<IPersistentClass>();

	public ConfigurationFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	@Override
	public void setEntityResolver(EntityResolver entityResolver) {
		// This method is not supported anymore from Hibernate 5+
		// Only caching the EntityResolver for bookkeeping purposes
		this.entityResolver = entityResolver;
	}
	
	@Override
	public void setNamingStrategy(INamingStrategy namingStrategy) {
		// The method Configuration.setNamingStrategy() is not supported 
		// anymore from Hibernate 5+.
		// Naming strategies can be configured using the 
		// AvailableSettings.IMPLICIT_NAMING_STRATEGY property.
		// Only caching the EntityResolver for bookkeeping purposes
		this.namingStrategy = namingStrategy;
	}
	
	@Override
	public IConfiguration configure(Document document) {
		File tempFile = null;
		IConfiguration result = null;
		try {
			tempFile = File.createTempFile(document.toString(), "cfg.xml");
			DOMSource domSource = new DOMSource(document);
			StringWriter stringWriter = new StringWriter();
			StreamResult stream = new StreamResult(stringWriter);
		    TransformerFactory tf = TransformerFactory.newInstance();
		    Transformer transformer = tf.newTransformer();
		    transformer.transform(domSource, stream);
		    FileWriter fileWriter = new FileWriter(tempFile);
		    fileWriter.write(stringWriter.toString());
		    fileWriter.close();
			result = configure(tempFile);
		} catch(IOException | TransformerException e) {
			throw new RuntimeException("Problem while configuring", e);
		} finally {
			tempFile.delete();
		}
		return result;
	}
	
	@Override
	public void addClass(IPersistentClass persistentClass) {
		addedClasses.add(persistentClass);
	}
	
}
