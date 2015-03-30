package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public abstract class AbstractDatabaseCollectorFacade 
extends AbstractFacade 
implements IDatabaseCollector {

	public AbstractDatabaseCollectorFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
