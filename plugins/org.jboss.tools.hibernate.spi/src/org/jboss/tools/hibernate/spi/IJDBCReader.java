package org.jboss.tools.hibernate.spi;


public interface IJDBCReader {

	IMetaDataDialect getMetaDataDialect();
	
	void readDatabaseSchema(
			IDatabaseCollector databaseCollector,
			String defaultCatalogName, 
			String defaultSchemaName,
			IProgressListener progressListener);

}
