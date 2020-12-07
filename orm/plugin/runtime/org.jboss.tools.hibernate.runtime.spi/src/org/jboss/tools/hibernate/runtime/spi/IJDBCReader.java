package org.jboss.tools.hibernate.runtime.spi;


public interface IJDBCReader {

	IDatabaseCollector readDatabaseSchema(IProgressListener progressListener);
	
}
