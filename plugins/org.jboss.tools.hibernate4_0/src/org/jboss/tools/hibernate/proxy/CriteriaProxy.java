package org.jboss.tools.hibernate.proxy;

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

}
