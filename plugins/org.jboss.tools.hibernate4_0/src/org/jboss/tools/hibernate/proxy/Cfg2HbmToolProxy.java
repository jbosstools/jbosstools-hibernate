package org.jboss.tools.hibernate.proxy;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.jboss.tools.hibernate.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.spi.IProperty;

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
	public String getTag(IProperty property) {
		assert property instanceof PropertyProxy;
		return target.getTag(((PropertyProxy)property).getTarget());
	}

}
