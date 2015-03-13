package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.common.AbstractReverseEngineeringStrategyFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class ReverseEngineeringStrategyProxy 
extends AbstractReverseEngineeringStrategyFacade  {
	
	public ReverseEngineeringStrategyProxy(
			IFacadeFactory facadeFactory, 
			ReverseEngineeringStrategy res) {
		super(facadeFactory, res);
	}

	public ReverseEngineeringStrategy getTarget() {
		return (ReverseEngineeringStrategy)super.getTarget();
	}

}
