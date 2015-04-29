package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.jboss.tools.hibernate.runtime.common.AbstractDatabaseCollectorFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class DatabaseCollectorFacadeImpl extends AbstractDatabaseCollectorFacade {
	
	public DatabaseCollectorFacadeImpl(
			IFacadeFactory facadeFactory,
			DefaultDatabaseCollector dbc) {
		super(facadeFactory, dbc);
	}
	
	public DefaultDatabaseCollector getTarget() {
		return (DefaultDatabaseCollector)super.getTarget();
	}

}
