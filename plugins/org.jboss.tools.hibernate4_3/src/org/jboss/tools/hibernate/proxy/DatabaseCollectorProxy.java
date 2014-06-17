package org.jboss.tools.hibernate.proxy;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.spi.IDatabaseCollector;

public class DatabaseCollectorProxy implements IDatabaseCollector {
	
	private DefaultDatabaseCollector target = null;
	
	public DatabaseCollectorProxy(DefaultDatabaseCollector dbc) {
		target = dbc;
	}
	
	DefaultDatabaseCollector getTarget() {
		return target;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Entry<String, List<Table>>> getQualifierEntries() {
		return target.getQualifierEntries();
	}

}
