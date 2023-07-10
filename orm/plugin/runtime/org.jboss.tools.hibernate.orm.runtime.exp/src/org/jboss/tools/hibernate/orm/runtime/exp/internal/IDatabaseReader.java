package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import java.util.List;
import java.util.Map;

import org.jboss.tools.hibernate.runtime.spi.ITable;

public interface IDatabaseReader {
	
	Map<String, List<ITable>> collectDatabaseTables();

}
