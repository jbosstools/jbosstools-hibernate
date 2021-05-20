package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractCriteriaFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;

public class CriteriaFacadeImpl extends AbstractCriteriaFacade implements ICriteria {

	public CriteriaFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

}
