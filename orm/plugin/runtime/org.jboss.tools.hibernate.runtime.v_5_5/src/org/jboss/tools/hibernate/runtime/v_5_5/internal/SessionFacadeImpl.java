package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.jboss.tools.hibernate.runtime.common.AbstractSessionFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;

public class SessionFacadeImpl extends AbstractSessionFacade {

	public SessionFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
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

}
