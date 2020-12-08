package org.jboss.tools.hibernate.runtime.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.jboss.tools.hibernate.runtime.common.internal.HibernateRuntimeCommon;
import org.jboss.tools.hibernate.runtime.spi.IJDBCReader;
import org.jboss.tools.hibernate.runtime.spi.IProgressListener;
import org.jboss.tools.hibernate.runtime.spi.ITable;

public abstract class AbstractJDBCReaderFacade 
extends AbstractFacade 
implements IJDBCReader {
	
	public AbstractJDBCReaderFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public Map<String, List<ITable>> collectDatabaseTables(IProgressListener progressListener) {
		Object databaseCollector = createDatabaseCollector();
		readDatabaseSchema(databaseCollector, progressListener);
		return collectDatabaseTables(databaseCollector);
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
	
	protected String getDefaultDatabaseCollectorClassName() {
		return "org.hibernate.cfg.reveng.DefaultDatabaseCollector";
	}
	
	public Class<?> getDefaultDatabaseCollectorClass() {
		return Util.getClass(
				getDefaultDatabaseCollectorClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	protected String getMetaDataDialectClassName() {
		return "org.hibernate.cfg.reveng.dialect.MetaDataDialect";
	}
	
	public Class<?> getMetaDataDialectClass() {
		return Util.getClass(
				getMetaDataDialectClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	private void readDatabaseSchema(
			Object databaseCollector,
			IProgressListener progressListener) {
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
						databaseCollector,
						properties.getProperty(defaultCatalog),
						properties.getProperty(defaultSchema),
						createProgressListener(progressListener)
				});
	}
	
	private Object createDatabaseCollector() {
		Object result = null;
		try {
			Object metadataDialect = Util.invokeMethod(
					getTarget(), 
					"getMetaDataDialect", 
					new Class[] {}, 
					new Object[] {});
			Constructor<?> constructor = getDefaultDatabaseCollectorClass()
					.getConstructor(getMetaDataDialectClass());
			result = constructor.newInstance(metadataDialect);
		} catch (NoSuchMethodException | 
				InvocationTargetException | 
				IllegalAccessException | 
				InstantiationException e) {
			HibernateRuntimeCommon.log(e);
			throw new RuntimeException(e);
		}
		return result;
	}
	
	private Map<String, List<ITable>> collectDatabaseTables(Object databaseCollector) {
		Map<String, List<ITable>> result = new HashMap<String, List<ITable>>();
		Iterator<?> origin = 
				(Iterator<?>)Util.invokeMethod(
						databaseCollector, 
						"getQualifierEntries", 
						new Class[] {}, 
						new Object[] {});
		while (origin.hasNext()) {
			Entry<?, ?> entry = (Entry<?, ?>)origin.next();
			ArrayList<ITable> list = new ArrayList<ITable>();
			for (Object table : (Iterable<?>)entry.getValue()) {
				list.add(getFacadeFactory().createTable(table));
			}
			result.put((String)entry.getKey(), list);
		}
		return result;
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
