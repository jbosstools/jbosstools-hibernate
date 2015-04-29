package org.jboss.tools.hibernate.runtime.common;

import java.util.HashMap;
import java.util.List;

import org.jboss.tools.hibernate.runtime.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;

public abstract class AbstractDatabaseCollectorFacade 
extends AbstractFacade 
implements IDatabaseCollector {

	protected HashMap<String, List<ITable>> qualifierEntries = null;
	
	public AbstractDatabaseCollectorFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
