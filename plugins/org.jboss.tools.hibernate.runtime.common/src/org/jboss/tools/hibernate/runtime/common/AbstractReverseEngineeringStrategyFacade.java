package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;

public abstract class AbstractReverseEngineeringStrategyFacade 
extends AbstractFacade 
implements IReverseEngineeringStrategy {

	public AbstractReverseEngineeringStrategyFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
