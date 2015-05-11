package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;

public abstract class AbstractPOJOClassFacade 
extends AbstractFacade 
implements IPOJOClass {

	public AbstractPOJOClassFacade(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

}
