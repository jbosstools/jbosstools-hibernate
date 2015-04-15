package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.Settings;
import org.jboss.tools.hibernate.runtime.common.AbstractSettingsFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class SettingsProxy extends AbstractSettingsFacade {
	
	public SettingsProxy(
			IFacadeFactory facadeFactory, 
			Settings settings) {
		super(facadeFactory, settings);
	}

	public Settings getTarget() {
		return (Settings)super.getTarget();
	}

	@Override
	public String getDefaultCatalogName() {
		return getTarget().getDefaultCatalogName();
	}

	@Override
	public String getDefaultSchemaName() {
		return getTarget().getDefaultSchemaName();
	}

}
