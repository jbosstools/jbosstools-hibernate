package org.jboss.tools.hibernate.proxy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.jboss.tools.hibernate.runtime.common.AbstractForeignKeyFacade;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;

public class ForeignKeyProxy extends AbstractForeignKeyFacade {
	
	private ForeignKey target = null;
	private ITable referencedTable = null;
	private HashSet<IColumn> columns = null;
	private List<IColumn> referencedColumns = null;

	public ForeignKeyProxy(
			IFacadeFactory facadeFactory,
			ForeignKey foreignKey) {
		super(facadeFactory, foreignKey);
		target = foreignKey;
	}

	public ForeignKey getTarget() {
		return (ForeignKey)super.getTarget();
	}

	@Override
	public ITable getReferencedTable() {
		if (referencedTable == null && target.getReferencedTable() != null) {
			referencedTable = new TableProxy(getFacadeFactory(), target.getReferencedTable());
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
			columns.add(new ColumnProxy(getFacadeFactory(), (Column)origin.next()));
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
			referencedColumns.add(new ColumnProxy(getFacadeFactory(), (Column)column));
		}
	}

	@Override
	public boolean containsColumn(IColumn column) {
		assert column instanceof ColumnProxy;
		return target.containsColumn(((ColumnProxy)column).getTarget());
	}

}
