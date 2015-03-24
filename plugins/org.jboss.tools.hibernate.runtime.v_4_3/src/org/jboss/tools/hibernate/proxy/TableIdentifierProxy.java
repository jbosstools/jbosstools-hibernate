package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.TableIdentifier;
import org.jboss.tools.hibernate.runtime.common.AbstractTableIdentifierFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class TableIdentifierProxy extends AbstractTableIdentifierFacade {
	
	public TableIdentifierProxy(
			IFacadeFactory facadeFactory, 
			TableIdentifier tableIdentifier) {
		super(facadeFactory, tableIdentifier);
	}

}
