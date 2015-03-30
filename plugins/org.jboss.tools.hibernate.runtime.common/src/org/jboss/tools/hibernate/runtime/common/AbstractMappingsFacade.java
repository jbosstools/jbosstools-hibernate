package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IMappings;

public abstract class AbstractMappingsFacade 
extends AbstractFacade 
implements IMappings {

	public AbstractMappingsFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
