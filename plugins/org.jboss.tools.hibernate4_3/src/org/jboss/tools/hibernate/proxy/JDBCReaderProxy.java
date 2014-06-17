package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.ProgressListener;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.jboss.tools.hibernate.spi.IJDBCReader;

public class JDBCReaderProxy implements IJDBCReader {
	
	private JDBCReader target = null;

	public JDBCReaderProxy(JDBCReader reader) {
		target = reader;
	}

	@Override
	public MetaDataDialect getMetaDataDialect() {
		return target.getMetaDataDialect();
	}

	@Override
	public void readDatabaseSchema(
			DefaultDatabaseCollector databaseCollector,
			String defaultCatalogName, 
			String defaultSchemaName,
			ProgressListener progressListener) {
		target.readDatabaseSchema(
				databaseCollector, 
				defaultCatalogName, 
				defaultSchemaName,
				progressListener);
	}

}
