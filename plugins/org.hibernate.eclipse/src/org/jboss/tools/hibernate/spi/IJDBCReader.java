package org.jboss.tools.hibernate.spi;

import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.ProgressListener;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;

public interface IJDBCReader {

	MetaDataDialect getMetaDataDialect();
	
	void readDatabaseSchema(
			DefaultDatabaseCollector databaseCollector,
			String defaultCatalogName, 
			String defaultSchemaName,
			ProgressListener progressListener);

}
