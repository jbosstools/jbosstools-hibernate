/*
 * Created on 2004-10-12
 *
 */
package org.hibernate.console;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.console.execution.DefaultExecutionContext;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.console.execution.ExecutionContextHolder;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.util.ReflectHelper;
import org.hibernate.util.StringHelper;
import org.xml.sax.EntityResolver;

public class ConsoleConfiguration implements ExecutionContextHolder {

	private ExecutionContext executionContext;
	
	private Map fakeDrivers = new HashMap();
	
	/* TODO: move this out to the actual users of the configuraiton/sf ? */ 
	private Configuration configuration;
	private SessionFactory sessionFactory;
	
	
	
	/** Unique name for this configuration */
	public String getName() {
		return prefs.getName();
	}
	
	public ConsoleConfiguration(ConsoleConfigurationPreferences config) {
		prefs = config;
	}
	
	public Object execute(Command c) {
		return executionContext.execute(c);
	}
	
	
	public ConsoleConfigurationPreferences prefs = null;
	
	
	/**
	 * Reset so a new configuration or sessionfactory is needed.
	 *
	 */
	public void reset() {
		// reseting state 
		configuration = null;
		closeSessionFactory();
	}
	
	public void build() {
		
		if(prefs.useAnnotations()) {
			try {
				Class clazz = ReflectHelper
						.classForName( "org.hibernate.cfg.AnnotationConfiguration" );
				configuration = buildWith( (Configuration) clazz.newInstance(),
						false );
			}
			catch (Exception e) {
				throw new HibernateConsoleRuntimeException("Could not load AnnotationConfiguration",e);
			}
		} else {
			configuration = buildWith(new Configuration(),true);	
		}
		
		
	}
	
	/**
	 * @return
	 * 
	 */
	public Configuration buildWith(final Configuration cfg, final boolean includeMappings) {
			URL[] customClassPathURLS = prefs.getCustomClassPathURLS();
			executionContext = new DefaultExecutionContext( new URLClassLoader( customClassPathURLS, getParentClassLoader() ) );							
			
			Configuration result = (Configuration) executionContext.execute(new ExecutionContext.Command() {
			
				public Object execute() {
					Configuration localCfg = cfg;
					Properties properties = prefs.getProperties();
					if(properties!=null) {
						localCfg = localCfg.setProperties(properties);
					}
					if(StringHelper.isNotEmpty(prefs.getEntityResolverName())) {
						try {
							localCfg.setEntityResolver((EntityResolver) ReflectHelper.classForName(prefs.getEntityResolverName()).newInstance());
						} catch (Exception c) {
							throw new HibernateConsoleRuntimeException("Could not configure entity resolver " + prefs.getEntityResolverName(), c);
						}
					}
					if (prefs.getConfigXMLFile() != null) {
						localCfg = localCfg.configure(prefs.getConfigXMLFile() );
					}
					
					// here both setProperties and configxml have had their chance to tell which databasedriver is needed. 
					registerFakeDriver(cfg.getProperty(Environment.DRIVER) );
					
					if(includeMappings) {
						File[] mappingFiles = prefs.getMappingFiles();
						
						for (int i = 0; i < mappingFiles.length; i++) {
							File hbm = mappingFiles[i];
							localCfg = cfg.addFile(hbm);
						}
					}
					
					return localCfg;
				}
			
			});
			
		
		return result;
	}

	
	/**
	 * DriverManager checks what classloader a class is loaded from thus
	 * we register a FakeDriver that we know is loaded "properly" which delegates all it class 
	 * to the real driver.
	 * 
	 * By doing so we can convince DriverManager that we can use any dynamically loaded driver.
	 * 
	 * @param driverClassName
	 */
	private void registerFakeDriver(String driverClassName) {

		if(driverClassName!=null) {
			try {
				Class driverClass = ReflectHelper.classForName(driverClassName);
				if(!fakeDrivers.containsKey(driverClassName) ) { // To avoid "double registration"
					FakeDelegatingDriver fakeDelegatingDriver = new FakeDelegatingDriver( (Driver) driverClass.newInstance() );
					DriverManager.registerDriver(fakeDelegatingDriver);
					fakeDrivers.put(driverClassName,fakeDelegatingDriver);
				}
			} 
			catch (ClassNotFoundException e) {
				throw new HibernateConsoleRuntimeException("Problems while loading database driverclass (" + driverClassName + ")", e);					
			} 
			catch (InstantiationException e) {
				throw new HibernateConsoleRuntimeException("Problems while loading database driverclass (" + driverClassName + ")", e);
			} 
			catch (IllegalAccessException e) {
				throw new HibernateConsoleRuntimeException("Problems while loading database driverclass (" + driverClassName + ")", e);
			} 
			catch (SQLException e) {
				throw new HibernateConsoleRuntimeException("Problems while loading database driverclass (" + driverClassName + ")", e);	
			}
		}
	}
	protected ClassLoader getParentClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
	public Configuration getConfiguration() {
		return configuration;
	}
	/**
	 * @return
	 */
	public boolean hasConfiguration() {
		return configuration!=null;
	}

	public void buildSessionFactory() {
		execute(new ExecutionContext.Command() {
			public Object execute() {
				if(sessionFactory!=null) {
					throw new HibernateConsoleRuntimeException("Factory were not closed before attempting to built a new factory.");
				}
				sessionFactory = getConfiguration().buildSessionFactory();
				fireFactoryBuilt();
				return null;
			}
		});
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	
	int execcount;
	List queryListeners = new ArrayList();
	List consoleCfgListeners = new ArrayList();
		
	public QueryPage executeHQLQuery(final String hql) {
		return executeHQLQuery(hql, new ConsoleQueryParameter[0]);
	}
	
	public QueryPage executeHQLQuery(final String hql, final ConsoleQueryParameter[] queryParameters) {
		
		return (QueryPage) executionContext.execute(new ExecutionContext.Command() {
			
			public Object execute() {
				Session session = getSessionFactory().openSession();
				QueryPage qp = new HQLQueryPage(ConsoleConfiguration.this,hql,queryParameters);
				qp.setSession(session);
				
				qp.setId(++execcount);				
				fireQueryPageCreated(qp);			
				return qp;
			}
		
		});		
	}	
	
	private void fireQueryPageCreated(QueryPage qp) {
		Iterator i = consoleCfgListeners.iterator(); 
		while (i.hasNext() ) {
			ConsoleConfigurationListener view = (ConsoleConfigurationListener) i.next();
			view.queryPageCreated(qp);
		}		
	}
	

	private void fireFactoryBuilt() {
		Iterator i = consoleCfgListeners.iterator();
		while (i.hasNext() ) {
			ConsoleConfigurationListener view = (ConsoleConfigurationListener) i.next();
			view.sessionFactoryBuilt(this, sessionFactory);
		}
	}

	private void fireFactoryClosing(SessionFactory sessionFactory2) {
		Iterator i = consoleCfgListeners.iterator();
		while (i.hasNext() ) {
			ConsoleConfigurationListener view = (ConsoleConfigurationListener) i.next();
			view.sessionFactoryClosing(this, sessionFactory2);
		}
	}

	public void addConsoleConfigurationListener(ConsoleConfigurationListener v) {
		consoleCfgListeners.add(v);		
	}
	
	public void removeConsoleConfigurationListener(ConsoleConfigurationListener sfListener) {
		consoleCfgListeners.remove(sfListener);		
	}
		
	public void executeJavaQuery(final String text) {
		execute(new ExecutionContext.Command() {
			public Object execute() {
				Session session = getSessionFactory().openSession();		        
		        QueryPage qp = new JavaPage(ConsoleConfiguration.this,text);
		        qp.setSession(session);
		        qp.setId(++execcount);
		        fireQueryPageCreated(qp);
				return qp;
			}
		});
		
	}

	public boolean isSessionFactoryCreated() {
		return sessionFactory!=null;
	}

	public ConsoleConfigurationPreferences getPreferences() {
		return prefs;
	}	
	
	public String toString() {
		return getClass().getName() + ":" + getName();
	}

	public ExecutionContext getExecutionContext() {
		return executionContext;
	}

	public void closeSessionFactory() {
		if(sessionFactory!=null) {
			fireFactoryClosing(sessionFactory);
			sessionFactory.close();			
			sessionFactory = null;
		}		
	}

	

	
}