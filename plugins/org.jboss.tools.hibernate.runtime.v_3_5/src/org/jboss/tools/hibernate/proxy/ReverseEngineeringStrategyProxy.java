package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.common.AbstractReverseEngineeringStrategyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;

public class ReverseEngineeringStrategyProxy 
extends AbstractReverseEngineeringStrategyFacade {
	
	public ReverseEngineeringStrategyProxy(
			IFacadeFactory facadeFactory, 
			ReverseEngineeringStrategy res) {
		super(facadeFactory, res);
	}

	public ReverseEngineeringStrategy getTarget() {
		return (ReverseEngineeringStrategy)super.getTarget();
	}

	@Override
	public void setSettings(IReverseEngineeringSettings settings) {
		assert settings instanceof IFacade;
		getTarget().setSettings((ReverseEngineeringSettings)((IFacade)settings).getTarget());
	}

}
