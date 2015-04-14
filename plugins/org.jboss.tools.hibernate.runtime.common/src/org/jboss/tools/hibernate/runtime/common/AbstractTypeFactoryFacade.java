package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;

public abstract class AbstractTypeFactoryFacade 
extends AbstractFacade 
implements ITypeFactory {

	public AbstractTypeFactoryFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
