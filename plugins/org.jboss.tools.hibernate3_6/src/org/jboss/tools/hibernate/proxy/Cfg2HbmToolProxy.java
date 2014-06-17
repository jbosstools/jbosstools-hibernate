package org.jboss.tools.hibernate.proxy;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.jboss.tools.hibernate.spi.ICfg2HbmTool;

public class Cfg2HbmToolProxy implements ICfg2HbmTool {
	
	private Cfg2HbmTool target = null;
	
	public Cfg2HbmToolProxy(Cfg2HbmTool tool) {
		target = tool;
	}

	@Override
	public String getTag(PersistentClass persistentClass) {
		return target.getTag(persistentClass);
	}

	@Override
	public String getTag(Property property) {
		return target.getTag(property);
	}

}
