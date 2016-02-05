package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.util.MetadataHelper;
import org.jboss.tools.hibernate.runtime.common.AbstractConfigurationFacade;
import org.jboss.tools.hibernate.runtime.common.AbstractSettingsFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IMappings;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISettings;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public class ConfigurationFacadeImpl extends AbstractConfigurationFacade {
	
	EntityResolver entityResolver = null;
	INamingStrategy namingStrategy = null;
	IMappings mappings = null;

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
	public EntityResolver getEntityResolver() {
		// This method is not supported anymore from Hibernate 5+
		// Returning the cached EntityResolver for bookkeeping purposes
		return this.entityResolver;
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
	public IMappings createMappings() {
		buildMappings();
		return mappings;
	}
	
	@Override 
	public void buildMappings() {
		MetadataHelper.getMetadata(((Configuration)getTarget()));
		mappings = new MappingsFacadeImpl(this);
	}
	
	@Override
	public ISettings buildSettings() {
		return new AbstractSettingsFacade(getFacadeFactory(), new Settings()) {};
	}

}
