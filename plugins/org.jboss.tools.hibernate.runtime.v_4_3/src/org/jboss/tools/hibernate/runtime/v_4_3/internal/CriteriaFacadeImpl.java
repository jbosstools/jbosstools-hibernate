package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import java.util.List;

import org.hibernate.Criteria;
import org.jboss.tools.hibernate.runtime.common.AbstractCriteriaFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class CriteriaFacadeImpl extends AbstractCriteriaFacade {
	
	public CriteriaFacadeImpl(
			IFacadeFactory facadeFactory,
			Criteria criteria) {
		super(facadeFactory, criteria);
	}
	
	public Criteria getTarget() {
		return (Criteria)super.getTarget();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> list() {
		return getTarget().list();
	}

}
