package org.jboss.tools.hibernate.proxy;

import java.util.ArrayList;

import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.hql.spi.QueryTranslator;
import org.jboss.tools.hibernate.runtime.common.AbstractHQLQueryPlanFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQueryTranslator;

public class HQLQueryPlanProxy extends AbstractHQLQueryPlanFacade {
	
	private IQueryTranslator[] translators = null;
	
	public HQLQueryPlanProxy(IFacadeFactory facadeFactory, HQLQueryPlan queryPlan) {
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
