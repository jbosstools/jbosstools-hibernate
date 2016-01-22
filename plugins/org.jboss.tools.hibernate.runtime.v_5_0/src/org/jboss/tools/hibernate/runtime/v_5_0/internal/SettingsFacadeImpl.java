package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.jboss.tools.hibernate.runtime.common.AbstractSettingsFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;

public class SettingsFacadeImpl extends AbstractSettingsFacade {

	public SettingsFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}
	
	@Override
	public String getDefaultCatalogName() {
		return Environment.getProperties().getProperty(
				AvailableSettings.DEFAULT_CATALOG);
	}

	@Override
	public String getDefaultSchemaName() {
		return Environment.getProperties().getProperty(
				AvailableSettings.DEFAULT_SCHEMA);
	}	

}
