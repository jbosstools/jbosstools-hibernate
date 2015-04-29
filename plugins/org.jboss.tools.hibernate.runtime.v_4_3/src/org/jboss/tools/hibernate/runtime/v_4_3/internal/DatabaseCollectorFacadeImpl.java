package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.jboss.tools.hibernate.runtime.common.AbstractDatabaseCollectorFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;

public class DatabaseCollectorFacadeImpl extends AbstractDatabaseCollectorFacade {
	
	public DatabaseCollectorFacadeImpl(
			IFacadeFactory facadeFactory,
			DefaultDatabaseCollector dbc) {
		super(facadeFactory, dbc);
	}
	
	public DefaultDatabaseCollector getTarget() {
		return (DefaultDatabaseCollector)super.getTarget();
	}

	@Override
	public Iterator<Entry<String, List<ITable>>> getQualifierEntries() {
		if (qualifierEntries == null) {
			initializeQualifierEntries();
		}
		return qualifierEntries.entrySet().iterator();
	}
	
}
