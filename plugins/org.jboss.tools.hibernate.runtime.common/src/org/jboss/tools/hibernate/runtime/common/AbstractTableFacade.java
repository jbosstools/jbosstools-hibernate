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

	@Override
	public void addColumn(IColumn column) {
		assert column instanceof IFacade;
		Object columnTarget = Util.invokeMethod(
				column, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(
				getTarget(), 
				"addColumn", 
				new Class[] { getColumnClass() }, 
				new Object[] { columnTarget });
		columns = null;
	}
	
	protected Class<?> getColumnClass() {
		return Util.getClass(getColumnClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String getColumnClassName() {
		return "org.hibernate.mapping.Column";
	}

}
