package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

public class Cfg2HbmToolProxy implements ICfg2HbmTool {
	
	private Cfg2HbmTool target = null;
	
	public Cfg2HbmToolProxy() {
		target = new Cfg2HbmTool();
	}

	@Override
	public String getTag(IPersistentClass persistentClass) {
		assert persistentClass instanceof PersistentClassProxy;
		return target.getTag(((PersistentClassProxy)persistentClass).getTarget());
	}

	@Override
	public String getTag(IProperty property) {
		assert property instanceof PropertyProxy;
		return target.getTag(((PropertyProxy)property).getTarget());
	}

}
