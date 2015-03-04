package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.jboss.tools.hibernate.runtime.common.AbstractCfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

public class Cfg2HbmToolProxy extends AbstractCfg2HbmTool {
	
	@Override
	public String getTag(IPersistentClass persistentClass) {
		assert persistentClass instanceof PersistentClassProxy;
		return ((Cfg2HbmTool)getTarget()).getTag(((PersistentClassProxy)persistentClass).getTarget());
	}
	
	@Override
	public String getTag(IProperty property) {
		assert property instanceof PropertyProxy;
		return ((Cfg2HbmTool)getTarget()).getTag(((PropertyProxy)property).getTarget());
	}

}
