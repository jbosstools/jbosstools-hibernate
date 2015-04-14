package org.jboss.tools.hibernate.proxy;

import java.util.ArrayList;

import org.hibernate.engine.query.HQLQueryPlan;
import org.hibernate.hql.QueryTranslator;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.runtime.spi.IQueryTranslator;

public class HQLQueryPlanProxy implements IHQLQueryPlan {
	
	private HQLQueryPlan target = null;
	private IQueryTranslator[] translators = null;
	
	public HQLQueryPlanProxy(IFacadeFactory facadeFactory, HQLQueryPlan queryPlan) {
		target = queryPlan;
	}
	
	@Override
	public IQueryTranslator[] getTranslators() {
		if (translators == null) {
			initializeTranslators();
		}
		return translators;
	}
	
	private void initializeTranslators() {
		QueryTranslator[] origin = target.getTranslators();
		ArrayList<IQueryTranslator> destination = 
				new ArrayList<IQueryTranslator>(origin.length);
		for (QueryTranslator translator : origin) {
			destination.add(new QueryTranslatorProxy(translator));
		}
		translators = destination.toArray(new IQueryTranslator[origin.length]);
	}

}
