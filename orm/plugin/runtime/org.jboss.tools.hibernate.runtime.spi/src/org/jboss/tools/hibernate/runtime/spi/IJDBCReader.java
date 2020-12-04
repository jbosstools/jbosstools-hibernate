package org.jboss.tools.hibernate.runtime.spi;


public interface IJDBCReader {

	IDatabaseCollector readDatabaseSchema(
			IDatabaseCollector databaseCollector,
			IProgressListener progressListener);
	
}
