package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.ISettings;

public abstract class AbstractSettingsFacade 
extends AbstractFacade 
implements ISettings {

	public AbstractSettingsFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public String getDefaultCatalogName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getDefaultCatalogName", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public String getDefaultSchemaName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getDefaultSchemaName", 
				new Class[] {}, 
				new Object[] {});
	}

}
