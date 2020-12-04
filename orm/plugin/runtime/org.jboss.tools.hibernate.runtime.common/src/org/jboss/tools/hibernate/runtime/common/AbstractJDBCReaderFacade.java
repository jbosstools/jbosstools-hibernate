package org.jboss.tools.hibernate.runtime.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

import org.jboss.tools.hibernate.runtime.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.runtime.spi.IJDBCReader;
import org.jboss.tools.hibernate.runtime.spi.IProgressListener;

public abstract class AbstractJDBCReaderFacade 
extends AbstractFacade 
implements IJDBCReader {
	
	private IDatabaseCollector databaseCollector = null;

	public AbstractJDBCReaderFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public IDatabaseCollector readDatabaseSchema(IProgressListener progressListener) {
		Object databaseCollectorTarget = Util.invokeMethod(
				databaseCollector, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Properties properties = (Properties)Util.invokeMethod(
				getEnvironmentClass(), 
				"getProperties", 
				new Class[] {}, 
				new Object[] {});
		String defaultCatalog = (String)Util.getFieldValue(
				getEnvironmentClass(), 
				"DEFAULT_CATALOG", 
				null);
		String defaultSchema = (String)Util.getFieldValue(
				getEnvironmentClass(), 
				"DEFAULT_SCHEMA", 
				null);
		Util.invokeMethod(
				getTarget(), 
				"readDatabaseSchema", 
				new Class[] { 
						getDatabaseCollectorClass(), 
						String.class,
						String.class,
						getProgressListenerClass() }, 
				new Object[] {
						databaseCollectorTarget,
						properties.getProperty(defaultCatalog),
						properties.getProperty(defaultSchema),
						createProgressListener(progressListener)
				});
		return databaseCollector;
	}
	
	public void setDatabaseCollector(IDatabaseCollector databaseCollector) {
		this.databaseCollector = databaseCollector;
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
	
	protected Class<?> getEnvironmentClass() {
		return Util.getClass(
				getEnvironmentClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	protected String getEnvironmentClassName() {
		return "org.hibernate.cfg.Environment";
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
