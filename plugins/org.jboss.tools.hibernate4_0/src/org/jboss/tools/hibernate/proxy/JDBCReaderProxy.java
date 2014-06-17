package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.JDBCReader;
import org.jboss.tools.hibernate.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.spi.IJDBCReader;
import org.jboss.tools.hibernate.spi.IMetaDataDialect;
import org.jboss.tools.hibernate.spi.IProgressListener;

public class JDBCReaderProxy implements IJDBCReader {
	
	private JDBCReader target = null;
	private IMetaDataDialect metaDataDialect = null;

	public JDBCReaderProxy(JDBCReader reader) {
		target = reader;
	}

	@Override
	public IMetaDataDialect getMetaDataDialect() {
		if (metaDataDialect == null) {
			metaDataDialect = new MetaDataDialectProxy(target.getMetaDataDialect());
		}
		return metaDataDialect;
	}

	@Override
	public void readDatabaseSchema(
			IDatabaseCollector databaseCollector,
			String defaultCatalogName, 
			String defaultSchemaName,
			IProgressListener progressListener) {
		assert databaseCollector instanceof DatabaseCollectorProxy;
		assert progressListener instanceof ProgressListenerProxy;
		target.readDatabaseSchema(
				((DatabaseCollectorProxy)databaseCollector).getTarget(), 
				defaultCatalogName, 
				defaultSchemaName,
				((ProgressListenerProxy)progressListener).getTarget());
	}

}
