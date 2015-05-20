package org.jboss.tools.hibernate.runtime.common;

import java.util.ArrayList;

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

	protected void initializeTranslators() {
		Object[] targetTranslators = (Object[])Util.invokeMethod(
				getTarget(), 
				"getTranslators", 
				new Class[] {}, 
				new Object[] {});
		ArrayList<IQueryTranslator> destination = 
				new ArrayList<IQueryTranslator>(targetTranslators.length);
		for (Object translator : targetTranslators) {
			destination.add(getFacadeFactory().createQueryTranslator(translator));
		}
		translators = destination.toArray(
				new IQueryTranslator[targetTranslators.length]);
	}

}
