package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import java.util.List;

import org.jboss.tools.hibernate.runtime.common.AbstractCriteriaFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;

import jakarta.persistence.Query;

public class CriteriaFacadeImpl extends AbstractCriteriaFacade {

	public CriteriaFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	@Override
	public List<?> list() {
		return ((Query)getTarget()).getResultList();
	}

}
