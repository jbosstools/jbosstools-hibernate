package org.jboss.tools.hibernate.proxy;

import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.jboss.tools.hibernate.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.spi.IPersistentClass;

public class Cfg2HbmToolProxy implements ICfg2HbmTool {
	
	private Cfg2HbmTool target = null;
	
	public Cfg2HbmToolProxy(Cfg2HbmTool tool) {
		target = tool;
	}

	@Override
	public String getTag(IPersistentClass persistentClass) {
		assert persistentClass instanceof PersistentClassProxy;
		return target.getTag(((PersistentClassProxy)persistentClass).getTarget());
	}

	@Override
	public String getTag(Property property) {
		return target.getTag(property);
	}

}
