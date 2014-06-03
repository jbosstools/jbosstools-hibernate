package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.Mappings;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.console.spi.HibernateConfiguration;
import org.hibernate.engine.Mapping;
import org.hibernate.mapping.PersistentClass;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public class ConfigurationProxy implements HibernateConfiguration {
	
	private Configuration target;
	
	public ConfigurationProxy(Configuration configuration) {
		target = configuration;
	}

	@Override
	public String getProperty(String propertyName) {
		return target.getProperty(propertyName);
	}

	@Override
	public HibernateConfiguration addFile(File file) {
		target.addFile(file);
		return this;
	}

	@Override
	public void setProperty(String name, String value) {
		target.setProperty(name, value);
	}

	@Override
	public HibernateConfiguration setProperties(Properties properties) {
		target.setProperties(properties);
		return this;
	}

	@Override
	public void setEntityResolver(EntityResolver entityResolver) {
		target.setEntityResolver(entityResolver);
	}

	@Override
	public void setNamingStrategy(NamingStrategy namingStrategy) {
		target.setNamingStrategy(namingStrategy);
	}

	@Override
	public Properties getProperties() {
		return target.getProperties();
	}

	@Override
	public void addProperties(Properties properties) {
		target.addProperties(properties);
	}

	@Override
	public HibernateConfiguration configure(Document document) {
		target.configure(document);
		return this;
	}

	@Override
	public HibernateConfiguration configure(File file) {
		target.configure(file);
		return this;
	}

	@Override
	public HibernateConfiguration configure() {
		target.configure();
		return this;
	}

	@Override
	public void buildMappings() {
		target.buildMapping();
	}

	@Override
	public SessionFactory buildSessionFactory() {
		return target.buildSessionFactory();
	}

	@Override
	public Settings buildSettings() {
		return target.buildSettings();
	}
	
	Configuration getConfiguration() {
		return target;
	}

	@Override
	public Mappings createMappings() {
		return target.createMappings();
	}

	@Override
	public Iterator<? extends PersistentClass> getClassMappings() {
		return target.getClassMappings();
	}

	@Override
	public void setPreferBasicCompositeIds(boolean preferBasicCompositeids) {
		if (target instanceof JDBCMetaDataConfiguration) {
			((JDBCMetaDataConfiguration)target).setPreferBasicCompositeIds(preferBasicCompositeids);
		}
	}

	@Override
	public void setReverseEngineeringStrategy(ReverseEngineeringStrategy res) {
		if (target instanceof JDBCMetaDataConfiguration) {
			((JDBCMetaDataConfiguration)target).setReverseEngineeringStrategy(res);
		}
	}

	@Override
	public void readFromJDBC() {
		if (target instanceof JDBCMetaDataConfiguration) {
			((JDBCMetaDataConfiguration)target).readFromJDBC();
		}
	}

	@Override
	public Mapping buildMapping() {
		return target.buildMapping();
	}

	@Override
	public PersistentClass getClassMapping(String string) {
		return target.getClassMapping(string);
	}

	@Override
	public NamingStrategy getNamingStrategy() {
		return target.getNamingStrategy();
	}

	@Override
	public EntityResolver getEntityResolver() {
		return target.getEntityResolver();
	}

	@Override
	public SessionFactory buildSessionFactory(Object serviceRegistry) {
		throw new RuntimeException("not supported");
	}

	@Override
	public Object buildSettings(Object serviceRegisrty) {
		throw new RuntimeException("not supported");
	}

}
