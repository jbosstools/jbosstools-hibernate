package org.jboss.tools.hibernate.runtime.common;

import java.util.HashSet;
import java.util.Iterator;

import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.ITable;

public abstract class AbstractForeignKeyFacade 
extends AbstractFacade 
implements IForeignKey {

	protected ITable referencedTable = null;
	protected HashSet<IColumn> columns = null;

	public AbstractForeignKeyFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}	

	@Override
	public ITable getReferencedTable() {
		Object targetReferencedTable = Util.invokeMethod(
				getTarget(), 
				"getReferencedTable", 
				new Class[] {}, 
				new Object[] {});
		if (referencedTable == null && targetReferencedTable != null) {
			referencedTable = getFacadeFactory().createTable(targetReferencedTable);
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
	
	protected void initializeColumns() {
		columns = new HashSet<IColumn>();
		Iterator<?> origin = (Iterator<?>)Util.invokeMethod(
				getTarget(), 
				"columnIterator", 
				new Class[] {}, 
				new Object[] {});
		while (origin.hasNext()) {
			columns.add(getFacadeFactory().createColumn(origin.next()));
		}
	}

}
