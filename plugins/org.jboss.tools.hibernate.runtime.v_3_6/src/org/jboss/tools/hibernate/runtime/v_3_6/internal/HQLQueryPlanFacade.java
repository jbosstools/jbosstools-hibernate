package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.engine.query.HQLQueryPlan;
import org.jboss.tools.hibernate.runtime.common.AbstractHQLQueryPlanFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class HQLQueryPlanFacade extends AbstractHQLQueryPlanFacade {
	
	public HQLQueryPlanFacade(IFacadeFactory facadeFactory, HQLQueryPlan queryPlan) {
		super(facadeFactory, queryPlan);
	}
	
}
