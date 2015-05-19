package org.jboss.tools.hibernate.runtime.common;

import java.util.HashSet;

import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;

public abstract class AbstractTableFacade 
extends AbstractFacade 
implements ITable {

	protected HashSet<IColumn> columns = null;

	public AbstractTableFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public String getName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getName", 
				new Class[] {}, 
				new Object[] {});
	}

}
