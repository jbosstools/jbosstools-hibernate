package org.jboss.tools.hibernate.proxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.hibernate.Filter;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.internal.SessionFactoryImpl;
import org.jboss.tools.hibernate.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.spi.IQueryTranslator;
import org.jboss.tools.hibernate.spi.ISessionFactory;

public class HQLQueryPlanProxy implements IHQLQueryPlan {
	
	private HQLQueryPlan target = null;
	private IQueryTranslator[] translators = null;
	
	public HQLQueryPlanProxy(
			String hql,
			boolean shallow,
			ISessionFactory sessionFactory) {
		assert sessionFactory instanceof SessionFactoryProxy;
		SessionFactoryImpl factory = 
				(SessionFactoryImpl) ((SessionFactoryProxy)sessionFactory).getTarget();
		Map<String, Filter> enabledFilters = Collections.emptyMap();
		target = new HQLQueryPlan(hql, shallow, enabledFilters, factory);
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
