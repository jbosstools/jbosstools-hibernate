package org.jboss.tools.hibernate.proxy;

import java.util.List;

import org.hibernate.Criteria;
import org.jboss.tools.hibernate.runtime.common.AbstractCriteriaFacade;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class CriteriaProxy extends AbstractCriteriaFacade {

	private Criteria target;

	public CriteriaProxy(
			IFacadeFactory facadeFactory, 
			Criteria criteria) {
		super(facadeFactory, criteria);
		target = criteria;
	}
	
	public ICriteria createCriteria(String associationPath, String alias) {
		return new CriteriaProxy(
				getFacadeFactory(), 
				target.createCriteria(associationPath, alias));
	}

	@Override
	public void setMaxResults(int intValue) {
		target.setMaxResults(intValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> list() {
		return target.list();
	}

}
