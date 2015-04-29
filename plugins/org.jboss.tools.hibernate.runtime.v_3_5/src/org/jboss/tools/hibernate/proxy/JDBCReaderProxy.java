package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.DatabaseCollector;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.ProgressListener;
import org.jboss.tools.hibernate.runtime.common.AbstractJDBCReaderFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IMetaDataDialect;
import org.jboss.tools.hibernate.runtime.spi.IProgressListener;

public class JDBCReaderProxy extends AbstractJDBCReaderFacade {
	
	private IMetaDataDialect metaDataDialect = null;

	public JDBCReaderProxy(IFacadeFactory facadeFactory, JDBCReader reader) {
		super(facadeFactory, reader);
	}
	
	public JDBCReader getTarget() {
		return (JDBCReader)super.getTarget();
	}

	@Override
	public IMetaDataDialect getMetaDataDialect() {
		if (metaDataDialect == null) {
			metaDataDialect = getFacadeFactory().createMetaDataDialect(
					getTarget().getMetaDataDialect());
		}
		return metaDataDialect;
	}

	@Override
	public void readDatabaseSchema(
			IDatabaseCollector databaseCollector,
			String defaultCatalogName, 
			String defaultSchemaName,
			IProgressListener progressListener) {
		assert databaseCollector instanceof IFacade;
		getTarget().readDatabaseSchema(
				(DatabaseCollector)((IFacade)databaseCollector).getTarget(), 
				defaultCatalogName, 
				defaultSchemaName,
				new ProgressListenerImpl(progressListener));
	}
	
	private class ProgressListenerImpl implements ProgressListener {
		private IProgressListener target;
		public ProgressListenerImpl(IProgressListener progressListener) {
			target = progressListener;
		}
		@Override
		public void startSubTask(String name) {
			target.startSubTask(name);
		}		
	}

}
