package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;

public abstract class AbstractPrimaryKeyFacade 
extends AbstractFacade 
implements IPrimaryKey {

	public AbstractPrimaryKeyFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
