package org.jboss.tools.hibernate.runtime.common;

import java.io.File;
import java.util.Properties;

import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.xml.sax.EntityResolver;

public abstract class AbstractConfigurationFacade 
extends AbstractFacade 
implements IConfiguration {

	protected INamingStrategy namingStrategy;

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

	protected Class<?> getNamingStrategyClass() {
		return Util.getClass(getNamingStrategyClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String getNamingStrategyClassName() {
		return "org.hibernate.cfg.NamingStrategy";
	}

}
