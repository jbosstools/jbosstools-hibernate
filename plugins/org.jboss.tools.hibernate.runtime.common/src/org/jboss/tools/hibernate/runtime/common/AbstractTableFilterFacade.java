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

	@Override
	public void setExclude(Boolean exclude) {
		Util.invokeMethod(
				getTarget(), 
				"setExclude", 
				new Class[] { Boolean.class }, 
				new Object[] { exclude });
	}

}
