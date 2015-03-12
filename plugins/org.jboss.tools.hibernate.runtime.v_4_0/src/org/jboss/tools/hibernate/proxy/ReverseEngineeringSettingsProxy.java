package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.common.AbstractReverseEngineeringSettingsFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class ReverseEngineeringSettingsProxy 
extends AbstractReverseEngineeringSettingsFacade {
	
	public ReverseEngineeringSettingsProxy(
			IFacadeFactory facadeFactory,
			ReverseEngineeringSettings settings) {
		super(facadeFactory, settings);
	}

	public ReverseEngineeringSettings getTarget() {
		return (ReverseEngineeringSettings)super.getTarget();
	}

}
