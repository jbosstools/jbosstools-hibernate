package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.AbstractTableFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
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
	public IValue getIdentifierValue() {
		if (identifierValue == null && getTarget().getIdentifierValue() != null) {
			identifierValue = getFacadeFactory().createValue(getTarget().getIdentifierValue());
		}
		return identifierValue;
	}

}
