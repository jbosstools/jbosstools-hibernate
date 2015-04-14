package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQueryTranslator;

public abstract class AbstractQueryTranslatorFacade 
extends AbstractFacade 
implements IQueryTranslator {

	public AbstractQueryTranslatorFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
