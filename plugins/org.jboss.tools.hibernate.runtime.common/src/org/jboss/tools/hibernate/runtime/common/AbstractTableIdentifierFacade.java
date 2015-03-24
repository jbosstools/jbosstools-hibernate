package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITableIdentifier;

public abstract class AbstractTableIdentifierFacade 
extends AbstractFacade 
implements ITableIdentifier {

	public AbstractTableIdentifierFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
