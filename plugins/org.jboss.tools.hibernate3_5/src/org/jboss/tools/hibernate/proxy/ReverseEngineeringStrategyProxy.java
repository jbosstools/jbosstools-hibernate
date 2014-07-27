package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.jboss.tools.hibernate.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.spi.IReverseEngineeringStrategy;

public class ReverseEngineeringStrategyProxy implements
		IReverseEngineeringStrategy {
	
	private ReverseEngineeringStrategy target = null;
	
	public ReverseEngineeringStrategyProxy(ReverseEngineeringStrategy res) {
		target = res;
	}

	ReverseEngineeringStrategy getTarget() {
		return target;
	}

	@Override
	public void setSettings(IReverseEngineeringSettings settings) {
		assert settings instanceof ReverseEngineeringSettingsProxy;
		target.setSettings(((ReverseEngineeringSettingsProxy)settings).getTarget());
	}

}
