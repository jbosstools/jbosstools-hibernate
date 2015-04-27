package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.mapping.Column;
import org.jboss.tools.hibernate.runtime.common.AbstractColumnFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class ColumnFacadeFactory extends AbstractColumnFacade {
	
	public ColumnFacadeFactory(
			IFacadeFactory facadeFactory, 
			Column column) {
		super(facadeFactory, column);
	}	

	public Column getTarget() {
		return (Column)super.getTarget();
	}

}
