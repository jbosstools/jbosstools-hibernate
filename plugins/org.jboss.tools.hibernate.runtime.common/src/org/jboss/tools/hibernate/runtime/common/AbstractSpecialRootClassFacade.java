package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public abstract class AbstractSpecialRootClassFacade 
extends AbstractPersistentClassFacade {

	public AbstractSpecialRootClassFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
