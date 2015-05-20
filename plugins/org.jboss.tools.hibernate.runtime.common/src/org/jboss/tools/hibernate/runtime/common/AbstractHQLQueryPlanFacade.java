package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.runtime.spi.IQueryTranslator;

public abstract class AbstractHQLQueryPlanFacade 
extends AbstractFacade 
implements IHQLQueryPlan {

	protected IQueryTranslator[] translators = null;
	
	public AbstractHQLQueryPlanFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
