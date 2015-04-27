package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import org.hibernate.mapping.Column;
import org.jboss.tools.hibernate.runtime.common.AbstractColumnFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class ColumnFacadeImpl extends AbstractColumnFacade {
	
	public ColumnFacadeImpl(
			IFacadeFactory facadeFactory, 
			Column column) {
		super(facadeFactory, column);
	}	

	public Column getTarget() {
		return (Column)super.getTarget();
	}

	@Override
	public void setSqlType(String sqlType) {
		getTarget().setSqlType(sqlType);
	}

}
