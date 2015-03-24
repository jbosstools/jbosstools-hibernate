package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.TableIdentifier;
import org.jboss.tools.hibernate.runtime.common.AbstractTableIdentifierFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class TableIdentifierProxy extends AbstractTableIdentifierFacade {
	
	private TableIdentifier target = null;

	public TableIdentifierProxy(
			IFacadeFactory facadeFactory, 
			TableIdentifier tableIdentifier) {
		super(facadeFactory, tableIdentifier);
		target = tableIdentifier;
	}

	@Override
	public String getName() {
		return target.getName();
	}

}
