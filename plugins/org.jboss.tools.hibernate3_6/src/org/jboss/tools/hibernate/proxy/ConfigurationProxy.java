package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.engine.Mapping;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IMappings;
import org.jboss.tools.hibernate.spi.INamingStrategy;
import org.jboss.tools.hibernate.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.spi.ISessionFactory;
import org.jboss.tools.hibernate.spi.ISettings;
import org.jboss.tools.hibernate.spi.ITable;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public class ConfigurationProxy implements IConfiguration {
	
	private Configuration target;
	private INamingStrategy namingStrategy;
	private HashSet<ITable> tableMappings = null;
	
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
	public ISettings buildSettings() {
		return new SettingsProxy(target.buildSettings());
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
		throw new RuntimeException("not supported");
	}

	@Override
	public Object buildSettings(Object serviceRegisrty) {
		throw new RuntimeException("not supported");
	}

	@Override
	public Iterator<ITable> getTableMappings() {
		Iterator<ITable> result = null;
		if (target instanceof JDBCMetaDataConfiguration) {
			if (tableMappings == null) {
				initializeTableMappings();
			}
			result = tableMappings.iterator();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private void initializeTableMappings() {
		Iterator<Table> iterator = ((JDBCMetaDataConfiguration)target).getTableMappings();
		while (iterator.hasNext()) {
			tableMappings.add(new TableProxy(iterator.next()));
		}
	}

}
