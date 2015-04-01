package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public abstract class AbstractEnvironmentFacade 
extends AbstractFacade 
implements IEnvironment {

	public AbstractEnvironmentFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
