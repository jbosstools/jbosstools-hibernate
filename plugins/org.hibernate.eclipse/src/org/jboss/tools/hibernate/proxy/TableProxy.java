package org.jboss.tools.hibernate.proxy;

import java.util.HashSet;
import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.spi.IColumn;
import org.jboss.tools.hibernate.spi.ITable;

public class TableProxy implements ITable {
	
	private Table target = null;
	private HashSet<IColumn> columns = null;
	
	public TableProxy(Table table) {
		target = table;
	}

	@Override
	public String getName() {
		return target.getName();
	}

	public Table getTarget() {
		return target;
	}

	@Override
	public void addColumn(IColumn column) {
		assert column instanceof ColumnProxy;
		target.addColumn(((ColumnProxy)column).getTarget());
		columns = null;
	}

	@Override
	public void setPrimaryKey(PrimaryKey pk) {
		target.setPrimaryKey(pk);
	}

	@Override
	public String getCatalog() {
		return target.getCatalog();
	}

	@Override
	public String getSchema() {
		return target.getSchema();
	}

	@Override
	public PrimaryKey getPrimaryKey() {
		return target.getPrimaryKey();
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
		Iterator<Column> iterator = target.getColumnIterator();
		while (iterator.hasNext()) {
			columns.add(new ColumnProxy(iterator.next()));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<ForeignKey> getForeignKeyIterator() {
		return target.getForeignKeyIterator();
	}

	@Override
	public String getComment() {
		return target.getComment();
	}

	@Override
	public String getRowId() {
		return target.getRowId();
	}

	@Override
	public String getSubselect() {
		return target.getSubselect();
	}

	@Override
	public boolean hasDenormalizedTables() {
		return target.hasDenormalizedTables();
	}

	@Override
	public boolean isAbstract() {
		return target.isAbstract();
	}

	@Override
	public boolean isAbstractUnionTable() {
		return target.isAbstractUnionTable();
	}

	@Override
	public boolean isPhysicalTable() {
		return target.isPhysicalTable();
	}
	
	@Override
	public boolean equals(Object o) {
		boolean result = false;
		if (o != null & o.getClass() == getClass()) {
			result = ((TableProxy)o).getTarget().equals(getTarget());
		}
		return result;
	}

}
