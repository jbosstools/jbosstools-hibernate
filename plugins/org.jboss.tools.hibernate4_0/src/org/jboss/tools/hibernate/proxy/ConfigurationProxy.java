package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.Settings;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IMappings;
import org.jboss.tools.hibernate.spi.INamingStrategy;
import org.jboss.tools.hibernate.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.spi.ISessionFactory;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public class ConfigurationProxy implements IConfiguration {
	
	private Configuration target;
	private INamingStrategy namingStrategy;
	
	public ConfigurationProxy(Configuration configuration) {
		target = configuration;
	}

	@Override
	public String getProperty(String propertyName) {
		return target.getProperty(propertyName);
	}

	@Override
	public IConfiguration addFile(File file) {
		target.addFile(file);
		return this;
	}

	@Override
	public void setProperty(String name, String value) {
		target.setProperty(name, value);
	}

	@Override
	public IConfiguration setProperties(Properties properties) {
		target.setProperties(properties);
		return this;
	}

	@Override
	public void setEntityResolver(EntityResolver entityResolver) {
		target.setEntityResolver(entityResolver);
	}

	@Override
	public void setNamingStrategy(INamingStrategy namingStrategy) {
		assert namingStrategy instanceof NamingStrategyProxy;
		this.namingStrategy = namingStrategy;
		target.setNamingStrategy(((NamingStrategyProxy)namingStrategy).getTarget());
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
	public IConfiguration configure(Document document) {
		target.configure(document);
		return this;
	}

	@Override
	public IConfiguration configure(File file) {
		target.configure(file);
		return this;
	}

	@Override
	public IConfiguration configure() {
		target.configure();
		return this;
	}

	@Override
	public void buildMappings() {
		target.buildMapping();
	}

	@Override
	public ISessionFactory buildSessionFactory() {
		return new SessionFactoryProxy(target.buildSessionFactory());
	}

	@Override
	public Settings buildSettings() {
		throw new RuntimeException("unsupported operation");
	}
	
	Configuration getConfiguration() {
		return target;
	}

	@Override
	public IMappings createMappings() {
		return new MappingsProxy(target.createMappings());
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
	public void setReverseEngineeringStrategy(IReverseEngineeringStrategy res) {
		assert res instanceof ReverseEngineeringStrategyProxy;
		if (target instanceof JDBCMetaDataConfiguration) {
			((JDBCMetaDataConfiguration)target).setReverseEngineeringStrategy(
					((ReverseEngineeringStrategyProxy)res).getTarget());
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
	public INamingStrategy getNamingStrategy() {
		if (namingStrategy == null) {
			namingStrategy = new NamingStrategyProxy(target.getNamingStrategy());
		}
		return namingStrategy;
	}

	@Override
	public EntityResolver getEntityResolver() {
		return target.getEntityResolver();
	}

	@Override
	public ISessionFactory buildSessionFactory(Object serviceRegistry) {
		if (serviceRegistry instanceof ServiceRegistry) {
			return new SessionFactoryProxy(target.buildSessionFactory((ServiceRegistry)serviceRegistry));
		} else {
			throw new RuntimeException("unknown service registry object");
		}
	}

	@Override
	public Object buildSettings(Object serviceRegistry) {
		if (serviceRegistry instanceof ServiceRegistry) {
			return target.buildSettings((ServiceRegistry)serviceRegistry);
		} else {
			throw new RuntimeException("unknown service registry object");
		}
	}	

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Table> getTableMappings() {
		Iterator<Table> result = null;
		if (target instanceof JDBCMetaDataConfiguration) {
			result = ((JDBCMetaDataConfiguration)target).getTableMappings();
		}
		return result;
	}

}
