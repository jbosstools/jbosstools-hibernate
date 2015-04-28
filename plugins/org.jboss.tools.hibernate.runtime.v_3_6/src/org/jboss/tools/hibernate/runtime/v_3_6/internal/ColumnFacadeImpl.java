package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.mapping.Column;
import org.jboss.tools.hibernate.runtime.common.AbstractColumnFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class ColumnFacadeImpl extends AbstractColumnFacade {
	
	public ColumnFacadeImpl(
			IFacadeFactory facadeFactory, 
			Column column) {
		super(facadeFactory, column);
	}	

	protected String getMappingClassName() {
		return "org.hibernate.engine.Mapping";
	}
	
}
