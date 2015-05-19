package org.jboss.tools.hibernate.runtime.common;

import java.util.HashSet;
import java.util.Iterator;

import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
import org.jboss.tools.hibernate.runtime.spi.ITable;

public abstract class AbstractTableFacade 
extends AbstractFacade 
implements ITable {

	protected HashSet<IColumn> columns = null;
	protected IPrimaryKey primaryKey = null;
	protected HashSet<IForeignKey> foreignKeys = null;

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

	@Override
	public String getSchema() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getSchema", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public IPrimaryKey getPrimaryKey() {
		if (primaryKey == null) {
			Object targetPrimaryKey = Util.invokeMethod(
					getTarget(), 
					"getPrimaryKey", 
					new Class[] {}, 
					new Object[] {});
			if (targetPrimaryKey != null) {
				primaryKey = getFacadeFactory().createPrimaryKey(targetPrimaryKey);
			}
		}
		return primaryKey;
	}

	@Override
	public Iterator<IColumn> getColumnIterator() {
		if (columns == null) {
			initializeColumns();
		}
		return columns.iterator();
	}
	
	@Override
	public Iterator<IForeignKey> getForeignKeyIterator() {
		if (foreignKeys == null) {
			initializeForeignKeys();
		}
		return foreignKeys.iterator();
	}
	
	@Override
	public String getComment() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getComment", 
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

	protected void initializeColumns() {
		columns = new HashSet<IColumn>();
		Iterator<?> targetColumnIterator = (Iterator<?>)Util.invokeMethod(
				getTarget(), 
				"getColumnIterator", 
				new Class[] {}, 
				new Object[] {});
		while (targetColumnIterator.hasNext()) {
			columns.add(getFacadeFactory().createColumn(targetColumnIterator.next()));
		}
	}

	protected void initializeForeignKeys() {
		foreignKeys = new HashSet<IForeignKey>();
		Iterator<?> targetForeignKeyIterator = (Iterator<?>)Util.invokeMethod(
				getTarget(), 
				"getForeignKeyIterator", 
				new Class[] {}, 
				new Object[] {});
		while (targetForeignKeyIterator.hasNext()) {
			foreignKeys.add(
					getFacadeFactory().createForeignKey(
							targetForeignKeyIterator.next()));
		}
	}

}
