package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.TableIdentifier;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITableIdentifier;

public class TableIdentifierProxy implements ITableIdentifier {
	
	private TableIdentifier target = null;

	public TableIdentifierProxy(
			IFacadeFactory facadeFactory, 
			TableIdentifier tableIdentifier) {
		target = tableIdentifier;
	}

	@Override
	public String getName() {
		return target.getName();
	}

}
