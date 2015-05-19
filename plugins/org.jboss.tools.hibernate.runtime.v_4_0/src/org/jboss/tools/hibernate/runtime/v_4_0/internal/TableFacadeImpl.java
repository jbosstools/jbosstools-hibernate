package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import java.util.Iterator;

import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.AbstractTableFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public class TableFacadeImpl extends AbstractTableFacade {
	
	private IValue identifierValue = null;
		
	public TableFacadeImpl(
			IFacadeFactory facadeFactory,
			Table table) {
		super(facadeFactory, table);
	}

	public Table getTarget() {
		return (Table)super.getTarget();
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
			result = ((TableFacadeImpl)o).getTarget().equals(getTarget());
		}
		return result;
	}

	@Override
	public IValue getIdentifierValue() {
		if (identifierValue == null && getTarget().getIdentifierValue() != null) {
			identifierValue = getFacadeFactory().createValue(getTarget().getIdentifierValue());
		}
		return identifierValue;
	}

}
