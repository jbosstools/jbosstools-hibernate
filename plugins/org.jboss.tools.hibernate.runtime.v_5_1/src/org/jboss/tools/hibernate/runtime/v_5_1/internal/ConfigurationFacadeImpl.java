package org.jboss.tools.hibernate.runtime.v_5_1.internal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.dialect.spi.DialectFactory;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.jboss.tools.hibernate.runtime.common.AbstractConfigurationFacade;
import org.jboss.tools.hibernate.runtime.common.AbstractSettingsFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IMappings;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.ISettings;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public class ConfigurationFacadeImpl extends AbstractConfigurationFacade {
	
	EntityResolver entityResolver = null;
//	Metadata metadata = null;
	
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
		getMetadata();
		mappings = new MappingsFacadeImpl(this);
	}
	
	@Override
	public ISettings buildSettings() {
		return new AbstractSettingsFacade(getFacadeFactory(), new Settings()) {};
	}
	
	@Override
	public INamingStrategy getNamingStrategy() {
		return namingStrategy;
	}
	
	@Override
	public void setProperty(String name, String value) {
		if (AvailableSettings.HBM2DDL_AUTO.equals(name) && "false".equals(value)) {
			return;
		}
		super.setProperty(name, value);
	}
	
	@Override
	protected Object createTargetMapping() {
		return getMetadata();
	}

	@Override
	protected void initializeClassMappings() {
		HashMap<String, IPersistentClass> classMappings = new HashMap<String, IPersistentClass>();
		Iterator<PersistentClass> origin = getMetadata().getEntityBindings().iterator();
		while (origin.hasNext()) {
			IPersistentClass pc = getFacadeFactory().createPersistentClass(origin.next());
			classMappings.put(pc.getEntityName(), pc);
		}
		setClassMappings(classMappings);
	}

	@Override
	protected void initializeTableMappings() {
		HashSet<ITable> tableMappings = new HashSet<ITable>();
		Iterator<Table> origin = getMetadata().collectTableMappings().iterator();
		while (origin.hasNext()) {
			ITable table = getFacadeFactory().createTable(origin.next());
			tableMappings.add(table);
		}
		setTableMappings(tableMappings);
	}

	protected Object buildTargetDialect() {
		return buildServiceRegistry()
				.getService(DialectFactory.class)
				.buildDialect(getProperties(), null);
	}
	
	private ServiceRegistry buildServiceRegistry() {
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings(((Configuration)getTarget()).getProperties());
		return builder.build();		
	}
	
	private Metadata getMetadata() {
		return MetadataHelper.getMetadata((Configuration)getTarget());
	}

}
