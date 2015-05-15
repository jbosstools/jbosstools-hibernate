package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

public abstract class AbstractSpecialRootClassFacade 
extends AbstractPersistentClassFacade {

	protected IProperty property;
	protected IProperty parentProperty;

	public AbstractSpecialRootClassFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
