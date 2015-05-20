package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.jboss.tools.hibernate.runtime.common.AbstractHQLQueryPlanFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class HQLQueryPlanFacade extends AbstractHQLQueryPlanFacade {
	
	public HQLQueryPlanFacade(IFacadeFactory facadeFactory, HQLQueryPlan queryPlan) {
		super(facadeFactory, queryPlan);
	}
	
	public HQLQueryPlan getTarget() {
		return (HQLQueryPlan)super.getTarget();
	}
	
}
