package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import org.hibernate.Session;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.AbstractSessionFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.Util;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;

import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class SessionFacadeImpl extends AbstractSessionFacade {

	public SessionFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	@Override
	public boolean contains(Object object) {
		boolean result = false;
		try {
			result = super.contains(object);
		} catch (IllegalArgumentException e) {
			if (!e.getMessage().startsWith("Not an entity [")) {
				throw e;
			}
		}
		return result;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ICriteria createCriteria(Class<?> persistentClass) {
		CriteriaBuilder criteriaBuilder = ((Session)getTarget()).getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(persistentClass);
		Root root = criteriaQuery.from(persistentClass);
		criteriaQuery.select(root);
		Query query = ((Session)getTarget()).createQuery(criteriaQuery);
		return getFacadeFactory().createCriteria(query);		
	}

	@Override
	public ISessionFactory getSessionFactory() {
		if (targetFactory == null) {
			Object targetSessionFactory = Util.invokeMethod(
					getTarget(), 
					"getSessionFactory", 
					new Class[] {}, 
					new Object[] {});
			if (targetSessionFactory != null) {
				targetFactory = (ISessionFactory)GenericFacadeFactory
						.createFacade(ISessionFactory.class, targetSessionFactory);
			}
		}
		return targetFactory;
	}

}
