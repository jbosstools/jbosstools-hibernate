package org.jboss.tools.hibernate.runtime.common;

import java.io.File;
import java.util.Properties;

import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.xml.sax.EntityResolver;

public abstract class AbstractConfigurationFacade 
extends AbstractFacade 
implements IConfiguration {

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

}
