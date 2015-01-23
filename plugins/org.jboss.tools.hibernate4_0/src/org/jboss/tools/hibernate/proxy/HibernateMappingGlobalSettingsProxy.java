package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingGlobalSettings;

public class HibernateMappingGlobalSettingsProxy implements
		IHibernateMappingGlobalSettings {
	
	private HibernateMappingGlobalSettings target = null;

	public HibernateMappingGlobalSettingsProxy(
			HibernateMappingGlobalSettings hibernateMappingGlobalSettings) {
		target = hibernateMappingGlobalSettings;
	}

	HibernateMappingGlobalSettings getTarget() {
		return target;
	}

}
