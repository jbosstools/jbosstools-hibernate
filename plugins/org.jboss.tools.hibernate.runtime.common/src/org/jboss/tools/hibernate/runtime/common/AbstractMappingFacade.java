package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IMapping;

public abstract class AbstractMappingFacade 
extends AbstractFacade 
implements IMapping {

	public AbstractMappingFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
