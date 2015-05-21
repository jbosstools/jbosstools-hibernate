package org.jboss.tools.hibernate.runtime.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.jboss.tools.hibernate.runtime.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IJDBCReader;
import org.jboss.tools.hibernate.runtime.spi.IMetaDataDialect;
import org.jboss.tools.hibernate.runtime.spi.IProgressListener;

public abstract class AbstractJDBCReaderFacade 
extends AbstractFacade 
implements IJDBCReader {

	private IMetaDataDialect metaDataDialect = null;

	public AbstractJDBCReaderFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public IMetaDataDialect getMetaDataDialect() {
		if (metaDataDialect == null) {
			Object targetMetaDataDialect = Util.invokeMethod(
					getTarget(), 
					"getMetaDataDialect", 
					new Class[] {}, 
					new Object[] {});
			if (targetMetaDataDialect != null) {
				metaDataDialect = 
						getFacadeFactory().createMetaDataDialect(
								targetMetaDataDialect);
			}
		}
		return metaDataDialect;
	}

	@Override
	public void readDatabaseSchema(
			IDatabaseCollector databaseCollector,
			String defaultCatalogName, 
			String defaultSchemaName,
			IProgressListener progressListener) {
		Object databaseCollectorTarget = Util.invokeMethod(
				databaseCollector, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(
				getTarget(), 
				"readDatabaseSchema", 
				new Class[] { 
						getDatabaseCollectorClass(), 
						getProgressListenerClass() }, 
				new Object[] {
						databaseCollectorTarget,
						createProgressListener(progressListener)
				});
	}
	
	public Class<?> getProgressListenerClass() {
		return Util.getClass(
				getProgressListenerClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	public Class<?> getDatabaseCollectorClass() {
		return Util.getClass(
				getDatabaseCollectorClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	public String getProgressListenerClassName() {
		return "org.hibernate.cfg.reveng.ProgressListener";
	}
	
	public String getDatabaseCollectorClassName() {
		return "org.hibernate.cfg.reveng.DatabaseCollector";
	}
	
	private Object createProgressListener(IProgressListener progressListener) {
		return Proxy.newProxyInstance(
				getFacadeFactoryClassLoader(), 
				new Class[] { getProgressListenerClass() }, 
				new ProgressListenerInvocationHandler(progressListener));
	}
	
	private class ProgressListenerInvocationHandler implements InvocationHandler {		
		public Object target;		
		public ProgressListenerInvocationHandler(Object target) {
			this.target = target;
		}
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if ("startSubTask".equals(method.getName())) {
				Util.invokeMethod(target, "startSubTask", new Class[] { String.class }, args);
			}
			return null;
		}		
	}
		
}
