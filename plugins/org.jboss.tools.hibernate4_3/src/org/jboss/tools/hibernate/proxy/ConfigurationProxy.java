package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.Settings;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectFactory;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IDialect;
import org.jboss.tools.hibernate.spi.IMapping;
import org.jboss.tools.hibernate.spi.IMappings;
import org.jboss.tools.hibernate.spi.INamingStrategy;
import org.jboss.tools.hibernate.spi.IPersistentClass;
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
	private HashMap<String, IPersistentClass> classMappings = null;
	private ServiceRegistry serviceRegistry = null;
	private IMapping mapping = null;
	private IDialect dialect = null;
	
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
		target.buildMappings();
	}

	@Override
	public ISessionFactory buildSessionFactory() {
		return new SessionFactoryProxy(target.buildSessionFactory());
	}

	@Override
	public ISettings buildSettings() {
		if (serviceRegistry == null) {
			ServiceRegistryBuilder builder = new ServiceRegistryBuilder();
			builder.configure();
			serviceRegistry = builder.build();
		}
		return new SettingsProxy((Settings)buildSettings(serviceRegistry));
	}
	
	Configuration getConfiguration() {
		return target;
	}

	@Override
	public IMappings createMappings() {
		return new MappingsProxy(target.createMappings());
	}

	@Override
	public Iterator<IPersistentClass> getClassMappings() {
		if (classMappings == null) {
			initializeClassMappings();
		}
		return classMappings.values().iterator();
	}
	
	private void initializeClassMappings() {
		classMappings = new HashMap<String, IPersistentClass>();
		Iterator<?> origin = target.getClassMappings();
		while (origin.hasNext()) {
			IPersistentClass pc = new PersistentClassProxy((PersistentClass)origin.next());
			classMappings.put(pc.getEntityName(), pc);
		}
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
	public IMapping buildMapping() {
		if (mapping == null) {
			Mapping m = target.buildMapping();
			if (m != null) {
				mapping = new MappingProxy(m);
			}
		}
		return mapping;
	}

	@Override
	public IPersistentClass getClassMapping(String string) {
		if (classMappings == null) {
			initializeClassMappings();
		}
		return classMappings.get(string);
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
	
	private void initializeTableMappings() {
		Iterator<Table> iterator = ((JDBCMetaDataConfiguration)target).getTableMappings();
		while (iterator.hasNext()) {
			tableMappings.add(new TableProxy(iterator.next()));
		}
	}

	@Override
	public IDialect getDialect() {
		if (dialect != null) {
			DialectFactory dialectFactory = serviceRegistry.getService(DialectFactory.class);
			Dialect d = dialectFactory.buildDialect(getProperties(), null);
			if (d != null) {
				dialect = new DialectProxy(d);
			}
		}
		return dialect;
	}

}
