package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import java.util.List;

import javax.persistence.Query;

import org.jboss.tools.hibernate.runtime.common.AbstractCriteriaFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;

public class CriteriaFacadeImpl extends AbstractCriteriaFacade {

	public CriteriaFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}
	
	@Override
	public List<?> list() {
		return ((Query)getTarget()).getResultList();
	}

}
