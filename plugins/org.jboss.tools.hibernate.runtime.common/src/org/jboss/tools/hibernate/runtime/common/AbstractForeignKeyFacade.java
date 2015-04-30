package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.ITable;

public abstract class AbstractForeignKeyFacade 
extends AbstractFacade 
implements IForeignKey {

	protected ITable referencedTable = null;

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

}
