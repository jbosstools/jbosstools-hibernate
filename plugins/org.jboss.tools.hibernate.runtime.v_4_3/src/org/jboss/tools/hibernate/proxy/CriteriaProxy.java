package org.jboss.tools.hibernate.proxy;

import java.util.List;

import org.hibernate.Criteria;
import org.jboss.tools.hibernate.runtime.common.AbstractCriteriaFacade;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class CriteriaProxy extends AbstractCriteriaFacade {
	
	public CriteriaProxy(
			IFacadeFactory facadeFactory,
			Criteria criteria) {
		super(facadeFactory, criteria);
	}
	
	public Criteria getTarget() {
		return (Criteria)super.getTarget();
	}
	
	public ICriteria createCriteria(String associationPath, String alias) {
		return getFacadeFactory().createCriteria(
				getTarget().createCriteria(associationPath, alias));
	}

	@Override
	public void setMaxResults(int intValue) {
		getTarget().setMaxResults(intValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> list() {
		return getTarget().list();
	}

}
