package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;

public abstract class AbstractTableFilterFacade 
extends AbstractFacade 
implements ITableFilter {

	public AbstractTableFilterFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
