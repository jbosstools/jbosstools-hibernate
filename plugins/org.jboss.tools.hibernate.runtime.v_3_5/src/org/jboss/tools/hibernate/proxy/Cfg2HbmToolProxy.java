package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.hbm2x.Cfg2HbmTool;
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
	
	@Override
	public String getTag(IProperty property) {
		assert property instanceof PropertyProxy;
		return ((Cfg2HbmTool)getTarget()).getTag(((PropertyProxy)property).getTarget());
	}

}
