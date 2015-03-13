package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;

public class ReverseEngineeringStrategyProxy implements
		IReverseEngineeringStrategy {
	
	private ReverseEngineeringStrategy target = null;
	
	public ReverseEngineeringStrategyProxy(
			IFacadeFactory facadeFactory, 
			ReverseEngineeringStrategy res) {
		target = res;
	}

	ReverseEngineeringStrategy getTarget() {
		return target;
	}

	@Override
	public void setSettings(IReverseEngineeringSettings settings) {
		assert settings instanceof IFacade;
		target.setSettings((ReverseEngineeringSettings)((IFacade)settings).getTarget());
	}

}
