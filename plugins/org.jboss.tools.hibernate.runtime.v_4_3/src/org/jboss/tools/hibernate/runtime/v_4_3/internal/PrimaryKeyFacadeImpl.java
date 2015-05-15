package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.mapping.PrimaryKey;
import org.jboss.tools.hibernate.runtime.common.AbstractPrimaryKeyFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;

public class PrimaryKeyFacadeImpl extends AbstractPrimaryKeyFacade {

	public PrimaryKeyFacadeImpl(
			IFacadeFactory facadeFactory,
			PrimaryKey primaryKey) {
		super(facadeFactory, primaryKey);
	}

	public PrimaryKey getTarget() {
		return (PrimaryKey)super.getTarget();
	}

	@Override
	public ITable getTable() {
		if (table == null && getTarget().getTable() != null) {
			table = getFacadeFactory().createTable(getTarget().getTable());
		}
		return table;
	}

}
