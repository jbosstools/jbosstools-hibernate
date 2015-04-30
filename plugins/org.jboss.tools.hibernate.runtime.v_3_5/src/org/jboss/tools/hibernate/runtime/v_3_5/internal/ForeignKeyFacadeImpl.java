package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.jboss.tools.hibernate.runtime.common.AbstractForeignKeyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;

public class ForeignKeyFacadeImpl extends AbstractForeignKeyFacade {
	
	private HashSet<IColumn> columns = null;
	private List<IColumn> referencedColumns = null;

	public ForeignKeyFacadeImpl(
			IFacadeFactory facadeFactory,
			ForeignKey foreignKey) {
		super(facadeFactory, foreignKey);
	}
	
	public ForeignKey getTarget() {
		return (ForeignKey)super.getTarget();
	}

	@Override
	public ITable getReferencedTable() {
		if (referencedTable == null && getTarget().getReferencedTable() != null) {
			referencedTable = getFacadeFactory().createTable(getTarget().getReferencedTable());
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
		Iterator<?> origin = getTarget().columnIterator();
		while (origin.hasNext()) {
			columns.add(getFacadeFactory().createColumn(origin.next()));
		}
	}

	@Override
	public boolean isReferenceToPrimaryKey() {
		return getTarget().isReferenceToPrimaryKey();
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
		for (Object column : getTarget().getReferencedColumns()) {
			referencedColumns.add(getFacadeFactory().createColumn(column));
		}
	}

	@Override
	public boolean containsColumn(IColumn column) {
		assert column instanceof IFacade;
		return getTarget().containsColumn((Column)((IFacade)column).getTarget());
	}

}
