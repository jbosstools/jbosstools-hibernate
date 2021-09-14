package org.jboss.tools.hibernate.runtime.v_5_6.internal;

import java.util.List;

import javax.persistence.Query;

import org.jboss.tools.hibernate.runtime.common.AbstractCriteriaFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;

public class CriteriaFacadeImpl extends AbstractCriteriaFacade implements ICriteria {

	public CriteriaFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	@Override
	public List<?> list() {
		return ((Query)getTarget()).getResultList();
	}

}
