package org.jboss.tools.hibernate.runtime.spi;

import java.util.List;
import java.util.Map;

public interface IJDBCReader {

	Map<String, List<ITable>> collectDatabaseTables(IProgressListener progressListener);
	
}
