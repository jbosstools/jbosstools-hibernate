package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingGlobalSettings;

public class HibernateMappingGlobalSettingsProxy implements
		IHibernateMappingGlobalSettings {
	
	private HibernateMappingGlobalSettings target = null;

	public HibernateMappingGlobalSettingsProxy(
			IFacadeFactory facadeFactory,
			HibernateMappingGlobalSettings hibernateMappingGlobalSettings) {
		target = hibernateMappingGlobalSettings;
	}

	public HibernateMappingGlobalSettings getTarget() {
		return target;
	}

}
