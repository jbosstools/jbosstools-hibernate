package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public abstract class AbstractCriteriaFacade 
extends AbstractFacade 
implements ICriteria {

	public AbstractCriteriaFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}