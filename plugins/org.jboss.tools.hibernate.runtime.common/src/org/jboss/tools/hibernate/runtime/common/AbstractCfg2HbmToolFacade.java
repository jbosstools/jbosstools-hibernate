package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

public abstract class AbstractCfg2HbmToolFacade 
extends AbstractFacade 
implements ICfg2HbmTool {
	
	private static final String TARGET_CLASS_NAME = "org.hibernate.tool.hbm2x.Cfg2HbmTool";
	
	public AbstractCfg2HbmToolFacade(IFacadeFactory facadeFactory) {
		super(facadeFactory, createTarget(TARGET_CLASS_NAME, facadeFactory));
	}

	private Class<?> getPersistentClassClass() {
		return Util.getClass("org.hibernate.mapping.PersistentClass", getFacadeFactoryClassLoader());
	}
	
	private Class<?> getPropertyClass() {
		return Util.getClass("org.hibernate.mapping.Property", getFacadeFactoryClassLoader());
	}
	
	private Object getTarget(Object object) {
		return Util.invokeMethod(object, "getTarget", new Class[] {}, new Object[] {});
	}
	
	@Override
	public String getTag(IPersistentClass persistentClass) {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getTag", 
				new Class[] { getPersistentClassClass() }, 
				new Object[] { getTarget(persistentClass) });
	}
	
	@Override
	public String getTag(IProperty property) {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getTag", 
				new Class[] { getPropertyClass() }, 
				new Object[] { getTarget(property) });
	}

}
