package org.jboss.tools.hibernate.runtime.common;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
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

public abstract class AbstractConfigurationFacade 
extends AbstractFacade 
implements IConfiguration {

	protected INamingStrategy namingStrategy;
	protected HashMap<String, IPersistentClass> classMappings = null;	
	protected IMapping mapping = null;
	protected HashSet<ITable> tableMappings = null;

	public AbstractConfigurationFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}
	
	@Override
	public String getProperty(String propertyName) {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getProperty", 
				new Class[] { String.class }, 
				new Object[] { propertyName });
	}

	@Override
	public IConfiguration addFile(File file) {
		Util.invokeMethod(
				getTarget(), 
				"addFile", 
				new Class[] { File.class }, 
				new Object[] { file });
		return this;
	}

	@Override
	public void setProperty(String name, String value) {
		Util.invokeMethod(
				getTarget(), 
				"setProperty", 
				new Class[] { String.class, String.class }, 
				new Object[] { name, value });
	}

	@Override
	public IConfiguration setProperties(Properties properties) {
		Util.invokeMethod(
				getTarget(), 
				"setProperties", 
				new Class[] { Properties.class }, 
				new Object[] { properties });
		return this;
	}

	@Override
	public void setEntityResolver(EntityResolver entityResolver) {
		Util.invokeMethod(
				getTarget(), 
				"setEntityResolver", 
				new Class[] { EntityResolver.class }, 
				new Object[] { entityResolver });
	}

	@Override
	public Properties getProperties() {
		return (Properties)Util.invokeMethod(
				getTarget(), 
				"getProperties", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public void setNamingStrategy(INamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
		Object namingStrategyTarget = Util.invokeMethod(
				namingStrategy, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(
				getTarget(), 
				"setNamingStrategy", 
				new Class[] { getNamingStrategyClass() }, 
				new Object[] { namingStrategyTarget });
	}
	
	@Override
	public void addProperties(Properties properties) {
		Util.invokeMethod(
				getTarget(), 
				"addProperties", 
				new Class[] { Properties.class }, 
				new Object[] { properties });
	}

	@Override
	public IConfiguration configure(Document document) {
		Util.invokeMethod(
				getTarget(), 
				"configure", 
				new Class[] { Document.class }, 
				new Object[] { document });
		return this;
	}

	@Override
	public IConfiguration configure(File file) {
		Util.invokeMethod(
				getTarget(), 
				"configure", 
				new Class[] { File.class }, 
				new Object[] { file });
		return this;
	}

	@Override
	public IConfiguration configure() {
		Util.invokeMethod(
				getTarget(), 
				"configure", 
				new Class[] {}, 
				new Object[] {});
		return this;
	}

	@Override
	public void buildMappings() {
		Util.invokeMethod(
				getTarget(), 
				"buildMappings", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public ISessionFactory buildSessionFactory() {
		return getFacadeFactory().createSessionFactory(buildTargetSessionFactory());
	}
	
	@Override
	public ISettings buildSettings() {
		return getFacadeFactory().createSettings(buildTargetSettings());
	}
	
	@Override
	public IMappings createMappings() {
		Object targetMappings = Util.invokeMethod(
				getTarget(), 
				"createMappings", 
				new Class[] {}, 
				new Object[] {});
		return getFacadeFactory().createMappings(targetMappings);
	}

	@Override
	public Iterator<IPersistentClass> getClassMappings() {
		if (classMappings == null) {
			initializeClassMappings();
		}
		return classMappings.values().iterator();
	}
	
	@Override
	public void setPreferBasicCompositeIds(boolean preferBasicCompositeids) {
		if (getJDBCMetaDataConfigurationClass().isAssignableFrom(getTarget().getClass())) {
			Util.invokeMethod(
					getTarget(), 
					"setPreferBasicCompositeIds", 
					new Class[] { boolean.class }, 
					new Object[] { preferBasicCompositeids });
		}
	}

	@Override
	public void setReverseEngineeringStrategy(IReverseEngineeringStrategy res) {
		assert res instanceof IFacade;
		if (getJDBCMetaDataConfigurationClass().isAssignableFrom(getTarget().getClass())) {
			Object reverseEngineeringStrategyTarget = Util.invokeMethod(
					res, 
					"getTarget", 
					new Class[] {}, 
					new Object[] {});
			Util.invokeMethod(
					getTarget(), 
					"setReverseEngineeringStrategy", 
					new Class[] { getReverseEngineeringStrategyClass() }, 
					new Object[] { reverseEngineeringStrategyTarget });
		}
	}

	@Override
	public void readFromJDBC() {
		if (getJDBCMetaDataConfigurationClass().isAssignableFrom(getTarget().getClass())) {
			Util.invokeMethod(
					getTarget(), 
					"readFromJDBC", 
					new Class[] {}, 
					new Object[] {});
		}
	}

	@Override
	public IMapping buildMapping() {
		if (mapping == null) {
			Object targetMapping = Util.invokeMethod(
					getTarget(), 
					"buildMapping", 
					new Class[] {}, 
					new Object[] {});
			if (targetMapping != null) {
				mapping = getFacadeFactory().createMapping(targetMapping);
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
			Object targetNamingStrategy = Util.invokeMethod(
					getTarget(), 
					"getNamingStrategy", 
					new Class[] {}, 
					new Object[] {});
			namingStrategy = getFacadeFactory().createNamingStrategy(targetNamingStrategy);
		}
		return namingStrategy;
	}

	@Override
	public EntityResolver getEntityResolver() {
		return (EntityResolver)Util.invokeMethod(
				getTarget(), 
				"getEntityResolver", 
				new Class[] {}, 
				new Object[] {});
	}

	protected Class<?> getNamingStrategyClass() {
		return Util.getClass(getNamingStrategyClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getJDBCMetaDataConfigurationClass() {
		return Util.getClass(getJDBCConfigurationClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getReverseEngineeringStrategyClass() {
		return Util.getClass(getReverseEngineeringStrategyClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String getNamingStrategyClassName() {
		return "org.hibernate.cfg.NamingStrategy";
	}
	
	protected String getJDBCConfigurationClassName() {
		return "org.hibernate.cfg.JDBCMetaDataConfiguration";
	}
	
	protected String getReverseEngineeringStrategyClassName() {
		return "org.hibernate.cfg.reveng.ReverseEngineeringStrategy";
	}
	
	protected Object buildTargetSessionFactory() {
		return Util.invokeMethod(
				getTarget(), 
				"buildSessionFactory", 
				new Class[] {}, 
				new Object[] {});
	}

	protected Object buildTargetSettings() {
		return Util.invokeMethod(
				getTarget(), 
				"buildSettings", 
				new Class[] {}, 
				new Object[] {});
	}	

	protected void initializeClassMappings() {
		classMappings = new HashMap<String, IPersistentClass>();
		Iterator<?> origin = (Iterator<?>)Util.invokeMethod(
				getTarget(), 
				"getClassMappings", 
				new Class[] {}, 
				new Object[] {});
		while (origin.hasNext()) {
			IPersistentClass pc = getFacadeFactory().createPersistentClass(origin.next());
			classMappings.put(pc.getEntityName(), pc);
		}
	}

}
