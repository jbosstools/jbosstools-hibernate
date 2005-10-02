/*
 * Created on 2004-10-12
 *
 */
package org.hibernate.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.DOMWriter;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
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
import org.hibernate.util.XMLHelper;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

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
						true );
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
					EntityResolver entityResolver = XMLHelper.DEFAULT_DTD_RESOLVER;
					if(StringHelper.isNotEmpty(prefs.getEntityResolverName())) {
						try {
							entityResolver = (EntityResolver) ReflectHelper.classForName(prefs.getEntityResolverName()).newInstance();
						} catch (Exception c) {
							throw new HibernateConsoleRuntimeException("Could not configure entity resolver " + prefs.getEntityResolverName(), c);
						}
					}
					localCfg.setEntityResolver(entityResolver);
					
					if (prefs.getConfigXMLFile() != null) {
						localCfg = loadConfigurationXML( localCfg, includeMappings, entityResolver );
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

	private Configuration loadConfigurationXML(Configuration localCfg, boolean includeMappings, EntityResolver entityResolver) {
		if(!includeMappings) {
			org.dom4j.Document doc;
			XMLHelper xmlHelper = new XMLHelper();
			String resourceName = prefs.getConfigXMLFile().toString();
			InputStream stream;
			try {
				stream = new FileInputStream( prefs.getConfigXMLFile() );
			}
			catch (FileNotFoundException e1) {
				throw new HibernateConsoleRuntimeException("Could not access " + prefs.getConfigXMLFile(), e1);
			}
			
			try {
				List errors = new ArrayList();
				
				doc = xmlHelper.createSAXReader( resourceName, errors, entityResolver )
				.read( new InputSource( stream ) );
				if ( errors.size() != 0 ) {
					throw new MappingException(
							"invalid configuration",
							(Throwable) errors.get( 0 )
					);
				}
				
				
				List list = doc.getRootElement().element("session-factory").elements("mapping"); 
				Iterator iterator = list.iterator();
				while ( iterator.hasNext() ) {
					Node element = (Node) iterator.next();
					element.getParent().remove(element);				
				}
				
				DOMWriter dw = new DOMWriter();
				Document document = dw.write(doc);
				return localCfg.configure( document );
		
			}
			catch (DocumentException e) {
				throw new HibernateException(
						"Could not parse configuration: " + resourceName,
						e
				);
			}
			finally {
				try {
					stream.close();
				}
				catch (IOException ioe) {
					//log.warn( "could not close input stream for: " + resourceName, ioe );
				}
			}
		} else {
			return localCfg.configure(prefs.getConfigXMLFile());
		}
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