package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IMetaDataDialect;

public abstract class AbstractMetaDataDialectFacade 
extends AbstractFacade 
implements IMetaDataDialect {

	public AbstractMetaDataDialectFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
