package org.jboss.tools.hibernate.proxy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.ITable;

public class ForeignKeyProxy implements IForeignKey {
	
	private ForeignKey target = null;
	private ITable referencedTable = null;
	private HashSet<IColumn> columns = null;
	private List<IColumn> referencedColumns = null;

	public ForeignKeyProxy(ForeignKey foreignKey) {
		target = foreignKey;
	}

	public ForeignKeyProxy(
			IFacadeFactory facadeFactory,
			ForeignKey foreignKey) {
		target = foreignKey;
	}

	@Override
	public ITable getReferencedTable() {
		if (referencedTable == null && target.getReferencedTable() != null) {
			referencedTable = new TableProxy(target.getReferencedTable());
		}
		return referencedTable;
	}

	@Override
	public Iterator<IColumn> columnIterator() {
		if (columns == null) {
			initializeColumns();
		}
		return columns.iterator();
	}
	
	private void initializeColumns() {
		columns = new HashSet<IColumn>();
		Iterator<?> origin = target.columnIterator();
		while (origin.hasNext()) {
			columns.add(new ColumnProxy((Column)origin.next()));
		}
	}

	@Override
	public boolean isReferenceToPrimaryKey() {
		return target.isReferenceToPrimaryKey();
	}

	@Override
	public List<IColumn> getReferencedColumns() {
		if (referencedColumns == null) {
			initializeReferencedColumns();
		}
		return referencedColumns;
	}
	
	private void initializeReferencedColumns() {
		referencedColumns = new ArrayList<IColumn>();
		for (Object column : target.getReferencedColumns()) {
			referencedColumns.add(new ColumnProxy((Column)column));
		}
	}

	@Override
	public boolean containsColumn(IColumn column) {
		assert column instanceof ColumnProxy;
		return target.containsColumn(((ColumnProxy)column).getTarget());
	}

}
