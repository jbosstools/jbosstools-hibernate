package org.jboss.tools.hibernate.proxy;

import org.jboss.tools.hibernate.runtime.common.AbstractCfg2HbmTool;
import org.jboss.tools.hibernate.runtime.common.Util;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

public class Cfg2HbmToolProxy extends AbstractCfg2HbmTool {
	
	@Override
	public String getTag(IPersistentClass persistentClass) {
		return getPersistentClassTag(persistentClass);
	}
	
	private Class<?> getPersistentClassClass() {
		return Util.getClass("org.hibernate.mapping.PersistentClass", this);
	}
	
	private Class<?> getPropertyClass() {
		return Util.getClass("org.hibernate.mapping.Property", this);
	}
	
	private Object getTarget(Object object) {
		return Util.invokeMethod(object, "getTarget", new Class[] {}, new Object[] {});
	}
	
	private String getPersistentClassTag(Object object) {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getTag", 
				new Class[] { getPersistentClassClass() }, 
				new Object[] { getTarget(object) });
	}
	
	private String getPropertyTag(Object object) {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getTag", 
				new Class[] { getPropertyClass() }, 
				new Object[] { getTarget(object) });
	}
	
	@Override
	public String getTag(IProperty property) {
		return getPropertyTag(property);
	}

}
