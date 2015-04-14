package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHQLQueryPlan;

public abstract class AbstractHQLQueryPlanFacade 
extends AbstractFacade 
implements IHQLQueryPlan {

	public AbstractHQLQueryPlanFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
