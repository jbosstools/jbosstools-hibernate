package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings;
import org.jboss.tools.hibernate.runtime.common.AbstractHibernateMappingGlobalSettingsFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class HibernateMappingGlobalSettingsProxy extends 
AbstractHibernateMappingGlobalSettingsFacade {
	
	private HibernateMappingGlobalSettings target = null;

	public HibernateMappingGlobalSettingsProxy(
			IFacadeFactory facadeFactory,
			HibernateMappingGlobalSettings hibernateMappingGlobalSettings) {
		super(facadeFactory, hibernateMappingGlobalSettings);
		target = hibernateMappingGlobalSettings;
	}

	public HibernateMappingGlobalSettings getTarget() {
		return target;
	}

}
