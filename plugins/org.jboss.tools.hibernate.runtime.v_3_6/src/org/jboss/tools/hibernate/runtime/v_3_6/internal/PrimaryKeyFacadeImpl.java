package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.mapping.PrimaryKey;
import org.jboss.tools.hibernate.runtime.common.AbstractPrimaryKeyFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;

public class PrimaryKeyFacadeImpl extends AbstractPrimaryKeyFacade {

	private ITable table = null;

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

	@Override
	public String getName() {
		return getTarget().getName();
	}

}
