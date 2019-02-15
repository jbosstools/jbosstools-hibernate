package org.jboss.tools.hibernate.runtime.spi;


public interface IJDBCReader {

	void readDatabaseSchema(
			IDatabaseCollector databaseCollector,
			IProgressListener progressListener);

}
