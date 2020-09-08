package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.jboss.tools.hibernate.runtime.common.AbstractConfigurationFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.JdbcMetadataConfiguration;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.MetadataHelper;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public class ConfigurationFacadeImpl extends AbstractConfigurationFacade {

	EntityResolver entityResolver = null;
	INamingStrategy namingStrategy = null;
	Metadata metadata;
	ArrayList<IPersistentClass> addedClasses = new ArrayList<IPersistentClass>();

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
	public INamingStrategy getNamingStrategy() {
		return namingStrategy;
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

	public Metadata getMetadata() {
		if (metadata == null) {
			Object target = getTarget();
			if (target instanceof Configuration) { 
				metadata = MetadataHelper.getMetadata((Configuration)target);
			} else if (target instanceof JdbcMetadataConfiguration) {
				metadata = ((JdbcMetadataConfiguration)target).getMetadata();
			}
		}
		return metadata;
	}
	
	@Override 
	public void buildMappings() {
		getMetadata();
	}

	public ArrayList<IPersistentClass> getAddedClasses() {
		return addedClasses;
	}
	
	@Override
	public void addClass(IPersistentClass persistentClass) {
		addedClasses.add(persistentClass);
	}
	
	@Override
	public void setReverseEngineeringStrategy(IReverseEngineeringStrategy res) {
		if (getTarget() instanceof JdbcMetadataConfiguration) {
			RevengStrategy revengStrategy = (RevengStrategy)((IFacade)res).getTarget();
			((JdbcMetadataConfiguration)getTarget()).setReverseEngineeringStrategy(revengStrategy);
		}
	}

	@Override
	public void readFromJDBC() {
		if (getTarget() instanceof JdbcMetadataConfiguration) {
			((JdbcMetadataConfiguration)getTarget()).readFromJdbc();
		}
	}
	@Override
	protected void initializeClassMappings() {
		HashMap<String, IPersistentClass> classMappings = new HashMap<String, IPersistentClass>();
		Iterator<PersistentClass> origin = getMetadata().getEntityBindings().iterator();
		while (origin.hasNext()) {
			IPersistentClass pc = getFacadeFactory().createPersistentClass(origin.next());
			classMappings.put(pc.getEntityName(), pc);
		}
		for (IPersistentClass pc : addedClasses) {
			classMappings.put(pc.getEntityName(), pc);
		}
		setClassMappings(classMappings);
	}

	@Override
	protected void initializeTableMappings() {
		HashSet<ITable> tableMappings = new HashSet<ITable>();
		Metadata metadata = getMetadata();
		if (metadata != null) {
			Iterator<Table> origin = metadata.collectTableMappings().iterator();
			while (origin.hasNext()) {
				ITable table = getFacadeFactory().createTable(origin.next());
				tableMappings.add(table);
			}
		}
		setTableMappings(tableMappings);
	}

	@Override
	protected String getJDBCConfigurationClassName() {
		return "org.jboss.tools.hibernate.runtime.v_6_0.internal.util.JdbcMetadataConfiguration";
	}
	
}
