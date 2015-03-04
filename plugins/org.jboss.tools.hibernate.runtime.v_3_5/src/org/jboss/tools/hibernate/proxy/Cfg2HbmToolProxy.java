package org.jboss.tools.hibernate.proxy;

import org.jboss.tools.hibernate.runtime.common.AbstractCfg2HbmTool;
import org.jboss.tools.hibernate.runtime.common.Util;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

public class Cfg2HbmToolProxy extends AbstractCfg2HbmTool {
	
	private Class<?> getPersistentClassClass() {
		return Util.getClass("org.hibernate.mapping.PersistentClass", this);
	}
	
	private Class<?> getPropertyClass() {
		return Util.getClass("org.hibernate.mapping.Property", this);
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
