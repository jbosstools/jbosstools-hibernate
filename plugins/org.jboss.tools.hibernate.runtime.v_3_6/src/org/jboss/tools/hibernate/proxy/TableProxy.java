package org.jboss.tools.hibernate.proxy;

import java.util.HashSet;
import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.AbstractTableFacade;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public class TableProxy extends AbstractTableFacade {
	
	private HashSet<IColumn> columns = null;
	private IValue identifierValue = null;
	private IPrimaryKey primaryKey = null;
	private HashSet<IForeignKey> foreignKeys = null;
	
	public TableProxy(
			IFacadeFactory facadeFactory,
			Table table) {
		super(facadeFactory, table);
	}

	public Table getTarget() {
		return (Table)super.getTarget();
	}

	@Override
	public String getName() {
		return getTarget().getName();
	}

	@Override
	public void addColumn(IColumn column) {
		assert column instanceof ColumnProxy;
		getTarget().addColumn(((ColumnProxy)column).getTarget());
		columns = null;
	}

	@Override
	public void setPrimaryKey(IPrimaryKey pk) {
		assert pk instanceof PrimaryKeyProxy;
		primaryKey = pk;
		getTarget().setPrimaryKey(((PrimaryKeyProxy)pk).getTarget());
	}

	@Override
	public String getCatalog() {
		return getTarget().getCatalog();
	}

	@Override
	public String getSchema() {
		return getTarget().getSchema();
	}

	@Override
	public IPrimaryKey getPrimaryKey() {
		if (primaryKey == null && getTarget().getPrimaryKey() != null) {
			primaryKey = new PrimaryKeyProxy(getFacadeFactory(), getTarget().getPrimaryKey());
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
	
	@SuppressWarnings("unchecked")
	private void initializeColumns() {
		columns = new HashSet<IColumn>();
		Iterator<Column> iterator = getTarget().getColumnIterator();
		while (iterator.hasNext()) {
			columns.add(getFacadeFactory().createColumn(iterator.next()));
		}
	}

	@Override
	public Iterator<IForeignKey> getForeignKeyIterator() {
		if (foreignKeys == null) {
			initializeForeignKeys();
		}
		return foreignKeys.iterator();
	}
	
	private void initializeForeignKeys() {
		foreignKeys = new HashSet<IForeignKey>();
		Iterator<?> origin = getTarget().getForeignKeyIterator();
		while (origin.hasNext()) {
			foreignKeys.add(getFacadeFactory().createForeignKey(origin.next()));
		}
	}

	@Override
	public String getComment() {
		return getTarget().getComment();
	}

	@Override
	public String getRowId() {
		return getTarget().getRowId();
	}

	@Override
	public String getSubselect() {
		return getTarget().getSubselect();
	}

	@Override
	public boolean hasDenormalizedTables() {
		return getTarget().hasDenormalizedTables();
	}

	@Override
	public boolean isAbstract() {
		return getTarget().isAbstract();
	}

	@Override
	public boolean isAbstractUnionTable() {
		return getTarget().isAbstractUnionTable();
	}

	@Override
	public boolean isPhysicalTable() {
		return getTarget().isPhysicalTable();
	}
	
	@Override
	public boolean equals(Object o) {
		boolean result = false;
		if (o != null & o.getClass() == getClass()) {
			result = ((TableProxy)o).getTarget().equals(getTarget());
		}
		return result;
	}

	@Override
	public IValue getIdentifierValue() {
		if (identifierValue == null && getTarget().getIdentifierValue() != null) {
			identifierValue = new ValueProxy(getFacadeFactory(), getTarget().getIdentifierValue());
		}
		return identifierValue;
	}

}
