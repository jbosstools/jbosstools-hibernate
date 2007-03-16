/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
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
import java.util.Map.Entry;

import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.DOMWriter;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.cfg.Settings;
import org.hibernate.console.execution.DefaultExecutionContext;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.console.execution.ExecutionContextHolder;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences.ConfigurationMode;
import org.hibernate.util.ConfigHelper;
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
		configuration = buildWith(null, true);				
	}

	private Configuration buildJPAConfiguration(String persistenceUnit, Properties properties, String entityResolver) {
		if(StringHelper.isEmpty( persistenceUnit )) {
			persistenceUnit = null;
		}
		try {
			Map overrides = new HashMap();
			if(properties!=null) {
				overrides.putAll( properties );
			}
			if(StringHelper.isNotEmpty( prefs.getNamingStrategy())) {
				overrides.put( "hibernate.ejb.naming_strategy", prefs.getNamingStrategy() );
			}
			
			Class clazz = ReflectHelper.classForName("org.hibernate.ejb.Ejb3Configuration", ConsoleConfiguration.class);
			Object ejb3cfg = clazz.newInstance();
			
			if(StringHelper.isNotEmpty(entityResolver)) {
				Class resolver = ReflectHelper.classForName(entityResolver, this.getClass());
				Object object = resolver.newInstance();
				Method method = clazz.getMethod("setEntityResolver", new Class[] { EntityResolver.class });
				method.invoke(ejb3cfg, new Object[] { object } );
			}
			
			Method method = clazz.getMethod("configure", new Class[] { String.class, Map.class });
			if ( method.invoke(ejb3cfg, new Object[] { persistenceUnit, overrides } ) == null ) {
				throw new HibernateConsoleRuntimeException("Persistence unit not found: '" + persistenceUnit + "'.");
			}
			
			method = clazz.getMethod("getHibernateConfiguration", new Class[0]);
			Configuration invoke = (Configuration) method.invoke(ejb3cfg, (Object[])null);
			return invoke;
		}
		catch (Exception e) {
			throw new HibernateConsoleRuntimeException("Could not create JPA based Configuration",e);
		}
	}
	
	private Configuration buildAnnotationConfiguration() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class clazz = ReflectHelper
				.classForName( "org.hibernate.cfg.AnnotationConfiguration" );
		Configuration newInstance = (Configuration) clazz.newInstance();
		return newInstance;
	}
	
	/**
	 * @return
	 * 
	 */
	public Configuration buildWith(final Configuration cfg, final boolean includeMappings) {
			URL[] customClassPathURLS = prefs.getCustomClassPathURLS();
			executionContext = new DefaultExecutionContext( getName(), new URLClassLoader( customClassPathURLS, getParentClassLoader() ) {
				protected Class findClass(String name) throws ClassNotFoundException {
					try {
					return super.findClass( name );
					} catch(ClassNotFoundException cnfe) {
						throw cnfe;
					}
				}
				
				protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
					try {
						return super.loadClass( name, resolve );
					} catch(ClassNotFoundException cnfe) {
						throw cnfe;
					}
				}
				
				public Class loadClass(String name) throws ClassNotFoundException {
					try {
					return super.loadClass( name );
					} catch(ClassNotFoundException cnfe) {
						throw cnfe;
					}
				}
			});							
			
			Configuration result = (Configuration) executionContext.execute(new ExecutionContext.Command() {
			
				public Object execute() {
					Configuration localCfg = cfg;

					Properties properties = prefs.getProperties();
					
					if(properties!=null) {
						String str = properties.getProperty( "hibernate.transaction.manager_lookup_class" );
						if(str.trim().length()==0) {
							properties.setProperty( "hibernate.transaction.manager_lookup_class", "org.hibernate.console.FakeTransactionManagerLookup");
						}
					}
					
										
					if(localCfg==null) {
						localCfg = buildConfiguration( properties, includeMappings );
					} else {
						// TODO: this is actually only for jdbc reveng...
						//localCfg = configureStandardConfiguration( includeMappings, localCfg, properties );						
					}

					// here both setProperties and configxml have had their chance to tell which databasedriver is needed. 
					registerFakeDriver(localCfg.getProperty(Environment.DRIVER) );
					
					// TODO: jpa configuration ?
					if(includeMappings) {
						File[] mappingFiles = prefs.getMappingFiles();
						
						for (int i = 0; i < mappingFiles.length; i++) {
							File hbm = mappingFiles[i];
							localCfg = localCfg.addFile(hbm);
						}
					}
                    // TODO: HBX-
					localCfg.setProperty( "hibernate.temp.use_jdbc_metadata_defaults", "false" );
					localCfg.setProperty( Environment.HBM2DDL_AUTO, "false" );
					
					return localCfg;
				}
			
			});
			
		
		return result;
	}

	private Configuration loadConfigurationXML(Configuration localCfg, boolean includeMappings, EntityResolver entityResolver) {
		File configXMLFile = prefs.getConfigXMLFile();
		if(!includeMappings) {
			org.dom4j.Document doc;
			XMLHelper xmlHelper = new XMLHelper();			
			InputStream stream;
			String resourceName = "<unknown>";
			try {
				if(configXMLFile!=null) {
					resourceName = configXMLFile.toString();
					stream = new FileInputStream( configXMLFile );
				} else {
					resourceName = "/hibernate.cfg.xml";
					stream = ConfigHelper.getResourceAsStream( resourceName ); // simulate hibernate's default look up
				}
			}
			catch (FileNotFoundException e1) {
				throw new HibernateConsoleRuntimeException("Could not access " + configXMLFile, e1);
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
						"Could not parse configuration: " + resourceName, e
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
			if(configXMLFile!=null) {
				return localCfg.configure(configXMLFile);
			} else {
				return localCfg.configure();
			}
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
		return executeHQLQuery(hql, new QueryInputModel());
	}
	
	public QueryPage executeHQLQuery(final String hql, final QueryInputModel queryParameters) {
		
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
	
	public QueryPage executeBSHQuery(final String queryString, final QueryInputModel model) {
		return (QueryPage) executionContext.execute(new ExecutionContext.Command() {
			
			public Object execute() {
				Session session = getSessionFactory().openSession();
				QueryPage qp = new JavaPage(ConsoleConfiguration.this,queryString,model);
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
	
	public ConsoleConfigurationListener[] getConsoleConfigurationListeners() {
		return (ConsoleConfigurationListener[]) consoleCfgListeners.toArray(new ConsoleConfigurationListener[consoleCfgListeners.size()]);
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

	public Settings getSettings(final Configuration cfg) {
		return (Settings) execute(new Command() {
		
			public Object execute() {
				return cfg.buildSettings();
			}
		
		}); 
	}

	// TODO: delegate to some extension point
	private Configuration buildConfiguration(Properties properties, boolean includeMappings) {
		Configuration localCfg = null;		
		if(prefs.getConfigurationMode().equals( ConfigurationMode.ANNOTATIONS )) {
			try {
				localCfg = buildAnnotationConfiguration();
				localCfg = configureStandardConfiguration( includeMappings, localCfg, properties );
			}
			catch (Exception e) {
				throw new HibernateConsoleRuntimeException("Could not load AnnotationConfiguration",e);
			}
		} else if(prefs.getConfigurationMode().equals( ConfigurationMode.JPA )) {
			try {
				localCfg = buildJPAConfiguration( getPreferences().getPersistenceUnitName(), properties, prefs.getEntityResolverName() );
			}
			catch (Exception e) {
				throw new HibernateConsoleRuntimeException("Could not load JPA Configuration",e);
			}
		} else {
			localCfg = new Configuration();			
			localCfg = configureStandardConfiguration( includeMappings, localCfg, properties );
		}	
		return localCfg;
	}

	private Configuration configureStandardConfiguration(final boolean includeMappings, Configuration localCfg, Properties properties) {
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

		if(StringHelper.isNotEmpty( prefs.getNamingStrategy())) {			
			try {
				NamingStrategy ns = (NamingStrategy) ReflectHelper.classForName(prefs.getNamingStrategy()).newInstance();
				localCfg.setNamingStrategy( ns );
			} catch (Exception c) {
				throw new HibernateConsoleRuntimeException("Could not configure naming strategy " + prefs.getNamingStrategy(), c);
			}
		}
		
		localCfg = loadConfigurationXML( localCfg, includeMappings, entityResolver );
		
		return localCfg;
	}
	


}