package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.common.internal.Util;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

public abstract class AbstractCfg2HbmTool implements ICfg2HbmTool {

	protected Object target = null;
	
	protected Object getTarget() {
		if (target == null) {
			target = Util.getInstance(getTargetClassName(), this);
		}
		return target;
	}
	
	protected String getTargetClassName() {
		return "org.hibernate.tool.hbm2x.Cfg2HbmTool";
	}
	
	@Override
	public String getTag(IPersistentClass persistentClass) {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getTag", 
				new Class[] { IPersistentClass.class }, 
				new Object[] { persistentClass } );
	}

	@Override
	public String getTag(IProperty property) {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getTag", 
				new Class[] { IProperty.class }, 
				new Object[] { property });
	}

}
