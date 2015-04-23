package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.resolver.DialectFactory;
import org.hibernate.engine.Mapping;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.AbstractConfigurationFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IDialect;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IMapping;
import org.jboss.tools.hibernate.runtime.spi.IMappings;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ISettings;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public class ConfigurationProxy extends AbstractConfigurationFacade {
	
	private INamingStrategy namingStrategy;
	private HashSet<ITable> tableMappings = null;
	private HashMap<String, IPersistentClass> classMappings = null;
	private IMapping mapping = null;
	private IDialect dialect = null;
	
	public ConfigurationProxy(
			IFacadeFactory facadeFactory, 
			Configuration configuration) {
		super(facadeFactory, configuration);
	}
	
	public Configuration getTarget() {
		return (Configuration)super.getTarget();
	}

	@Override
	public IConfiguration addFile(File file) {
		getTarget().addFile(file);
		return this;
	}

	@Override
	public void setProperty(String name, String value) {
		getTarget().setProperty(name, value);
	}

	@Override
	public IConfiguration setProperties(Properties properties) {
		getTarget().setProperties(properties);
		return this;
	}

	@Override
	public void setEntityResolver(EntityResolver entityResolver) {
		getTarget().setEntityResolver(entityResolver);
	}

	@Override
	public void setNamingStrategy(INamingStrategy namingStrategy) {
		assert namingStrategy instanceof IFacade;
		this.namingStrategy = namingStrategy;
		getTarget().setNamingStrategy((NamingStrategy)((IFacade)namingStrategy).getTarget());
	}

	@Override
	public Properties getProperties() {
		return getTarget().getProperties();
	}

	@Override
	public void addProperties(Properties properties) {
		getTarget().addProperties(properties);
	}

	@Override
	public IConfiguration configure(Document document) {
		getTarget().configure(document);
		return this;
	}

	@Override
	public IConfiguration configure(File file) {
		getTarget().configure(file);
		return this;
	}

	@Override
	public IConfiguration configure() {
		getTarget().configure();
		return this;
	}

	@Override
	public void buildMappings() {
		getTarget().buildMappings();
	}

	@Override
	public ISessionFactory buildSessionFactory() {
		return getFacadeFactory().createSessionFactory(getTarget().buildSessionFactory());
	}

	@Override
	public ISettings buildSettings() {
		return getFacadeFactory().createSettings(getTarget().buildSettings());
	}
	
	@Override
	public IMappings createMappings() {
		return getFacadeFactory().createMappings(
				getTarget().createMappings());
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
		Iterator<?> origin = getTarget().getClassMappings();
		while (origin.hasNext()) {
			IPersistentClass pc = getFacadeFactory().createPersistentClass(origin.next());
			classMappings.put(pc.getEntityName(), pc);
		}
	}

	@Override
	public void setPreferBasicCompositeIds(boolean preferBasicCompositeids) {
		if (getTarget() instanceof JDBCMetaDataConfiguration) {
			((JDBCMetaDataConfiguration)getTarget()).setPreferBasicCompositeIds(preferBasicCompositeids);
		}
	}

	@Override
	public void setReverseEngineeringStrategy(IReverseEngineeringStrategy res) {
		assert res instanceof IFacade;
		if (getTarget() instanceof JDBCMetaDataConfiguration) {
			((JDBCMetaDataConfiguration)getTarget()).setReverseEngineeringStrategy(
					(ReverseEngineeringStrategy)((IFacade)res).getTarget());
		}
	}

	@Override
	public void readFromJDBC() {
		if (getTarget() instanceof JDBCMetaDataConfiguration) {
			((JDBCMetaDataConfiguration)getTarget()).readFromJDBC();
		}
	}

	@Override
	public IMapping buildMapping() {
		if (mapping == null) {
			Mapping m = getTarget().buildMapping();
			if (m != null) {
				mapping = getFacadeFactory().createMapping(m);
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
			namingStrategy = getFacadeFactory().createNamingStrategy(getTarget().getNamingStrategy());
		}
		return namingStrategy;
	}

	@Override
	public EntityResolver getEntityResolver() {
		return getTarget().getEntityResolver();
	}

	@Override
	public Iterator<ITable> getTableMappings() {
		Iterator<ITable> result = null;
		if (getTarget() instanceof JDBCMetaDataConfiguration) {
			if (tableMappings == null) {
				initializeTableMappings();
			}
			result = tableMappings.iterator();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private void initializeTableMappings() {
		Iterator<Table> iterator = ((JDBCMetaDataConfiguration)getTarget()).getTableMappings();
		while (iterator.hasNext()) {
			tableMappings.add(getFacadeFactory().createTable(iterator.next()));
		}
	}

	@Override
	public IDialect getDialect() {
		if (dialect == null) {
			Dialect d = DialectFactory.buildDialect(getProperties());
			if (d != null) {
				dialect = getFacadeFactory().createDialect(d);
			}
		}
		return dialect;
	}

}
