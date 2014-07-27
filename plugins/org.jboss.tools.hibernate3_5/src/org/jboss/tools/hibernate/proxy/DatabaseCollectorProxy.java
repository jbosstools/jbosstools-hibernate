package org.jboss.tools.hibernate.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.spi.ITable;

public class DatabaseCollectorProxy implements IDatabaseCollector {
	
	private DefaultDatabaseCollector target = null;
	private HashMap<String, List<ITable>> qualifierEntries = null;
	
	public DatabaseCollectorProxy(DefaultDatabaseCollector dbc) {
		target = dbc;
	}
	
	DefaultDatabaseCollector getTarget() {
		return target;
	}

	@Override
	public Iterator<Entry<String, List<ITable>>> getQualifierEntries() {
		if (qualifierEntries == null) {
			initializeQualifierEntries();
		}
		return qualifierEntries.entrySet().iterator();
	}
	
	@SuppressWarnings("unchecked")
	private void initializeQualifierEntries() {
		qualifierEntries = new HashMap<String, List<ITable>>();
		Iterator<Entry<String, List<Table>>> origin = target.getQualifierEntries();
		while (origin.hasNext()) {
			Entry<String, List<Table>> entry = origin.next();
			ArrayList<ITable> list = new ArrayList<ITable>();
			for (Table table : entry.getValue()) {
				list.add(new TableProxy(table));
			}
			qualifierEntries.put(entry.getKey(), list);
		}
	}

}
