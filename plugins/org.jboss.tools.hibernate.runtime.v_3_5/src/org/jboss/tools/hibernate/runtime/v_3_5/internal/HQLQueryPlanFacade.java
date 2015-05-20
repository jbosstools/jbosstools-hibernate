package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import java.util.ArrayList;

import org.hibernate.engine.query.HQLQueryPlan;
import org.hibernate.hql.QueryTranslator;
import org.jboss.tools.hibernate.runtime.common.AbstractHQLQueryPlanFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQueryTranslator;

public class HQLQueryPlanFacade extends AbstractHQLQueryPlanFacade {
	
	private IQueryTranslator[] translators = null;
	
	public HQLQueryPlanFacade(IFacadeFactory facadeFactory, HQLQueryPlan queryPlan) {
		super(facadeFactory, queryPlan);
	}
	
	public HQLQueryPlan getTarget() {
		return (HQLQueryPlan)super.getTarget();
	}
	
	@Override
	public IQueryTranslator[] getTranslators() {
		if (translators == null) {
			initializeTranslators();
		}
		return translators;
	}
	
	private void initializeTranslators() {
		QueryTranslator[] origin = getTarget().getTranslators();
		ArrayList<IQueryTranslator> destination = 
				new ArrayList<IQueryTranslator>(origin.length);
		for (QueryTranslator translator : origin) {
			destination.add(getFacadeFactory().createQueryTranslator(translator));
		}
		translators = destination.toArray(new IQueryTranslator[origin.length]);
	}

}
