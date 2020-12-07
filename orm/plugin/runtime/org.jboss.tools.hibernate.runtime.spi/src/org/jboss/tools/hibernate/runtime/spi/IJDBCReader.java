package org.jboss.tools.hibernate.runtime.spi;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public interface IJDBCReader {

	Iterator<Entry<String, List<ITable>>> collectDatabaseTables(IProgressListener progressListener);
	
}
