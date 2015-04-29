package org.jboss.tools.hibernate.runtime.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.jboss.tools.hibernate.runtime.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;

public abstract class AbstractDatabaseCollectorFacade 
extends AbstractFacade 
implements IDatabaseCollector {

	private HashMap<String, List<ITable>> qualifierEntries = null;
	
	public AbstractDatabaseCollectorFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
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
		Iterator<Entry<String, List<?>>> origin = 
				(Iterator<Entry<String, List<?>>>)Util.invokeMethod(
						getTarget(), 
						"getQualifierEntries", 
						new Class[] {}, 
						new Object[] {});
		while (origin.hasNext()) {
			Entry<String, List<?>> entry = origin.next();
			ArrayList<ITable> list = new ArrayList<ITable>();
			for (Object table : entry.getValue()) {
				list.add(getFacadeFactory().createTable(table));
			}
			qualifierEntries.put(entry.getKey(), list);
		}
	}

}
