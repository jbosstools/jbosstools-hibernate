package org.jboss.tools.hibernate.proxy;

import java.util.List;

import org.hibernate.Criteria;
import org.jboss.tools.hibernate.spi.ICriteria;

public class CriteriaProxy implements ICriteria {

	private Criteria target;

	public CriteriaProxy(Criteria criteria) {
		target = criteria;
	}
	
	public ICriteria createCriteria(String associationPath, String alias) {
		return new CriteriaProxy(target.createCriteria(associationPath, alias));
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
