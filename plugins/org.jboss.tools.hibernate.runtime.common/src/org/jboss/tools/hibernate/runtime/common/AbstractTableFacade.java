package org.jboss.tools.hibernate.runtime.common;

import java.util.HashSet;

import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
import org.jboss.tools.hibernate.runtime.spi.ITable;

public abstract class AbstractTableFacade 
extends AbstractFacade 
implements ITable {

	protected HashSet<IColumn> columns = null;
	protected IPrimaryKey primaryKey = null;

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
	
	@Override
	public void setPrimaryKey(IPrimaryKey pk) {
		assert pk instanceof IFacade;
		Object pkTarget = Util.invokeMethod(
				pk, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(
				getTarget(), 
				"setPrimaryKey", 
				new Class[] { getPrimaryKeyClass() }, 
				new Object[] { pkTarget });
		primaryKey = pk;
	}

	@Override
	public String getCatalog() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getCatalog", 
				new Class[] {}, 
				new Object[] {});
	}

	protected Class<?> getColumnClass() {
		return Util.getClass(getColumnClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getPrimaryKeyClass() {
		return Util.getClass(getPrimaryKeyClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String getColumnClassName() {
		return "org.hibernate.mapping.Column";
	}

	protected String getPrimaryKeyClassName() {
		return "org.hibernate.mapping.PrimaryKey";
	}

}
