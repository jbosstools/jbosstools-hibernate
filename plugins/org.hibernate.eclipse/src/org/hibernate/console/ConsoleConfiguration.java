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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
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
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.osgi.util.NLS;
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
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.resolver.DialectFactory;
import org.hibernate.util.ConfigHelper;
import org.hibernate.util.ReflectHelper;
import org.hibernate.util.StringHelper;
import org.hibernate.util.XMLHelper;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class ConsoleConfiguration implements ExecutionContextHolder {

	private ExecutionContext executionContext;
	private ConsoleConfigClassLoader classLoader = null;

	private Map<String, FakeDelegatingDriver> fakeDrivers = new HashMap<String, FakeDelegatingDriver>();

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
		if (executionContext != null) {
			executionContext.execute(new ExecutionContext.Command() {
	
				public Object execute() {
					Iterator<FakeDelegatingDriver> it = fakeDrivers.values().iterator();
					while (it.hasNext()) {
						try {
							DriverManager.deregisterDriver(it.next());
						} catch (SQLException e) {
							// ignore
						}
					}
					return null;
				}
			});
		}
		fakeDrivers.clear();
		cleanUpClassLoader();
		fireConfigurationReset();
		executionContext = null;
	}

	public void cleanUpClassLoader() {
		ClassLoader classLoaderTmp = classLoader;
		while (classLoaderTmp != null) {
			if (classLoaderTmp instanceof ConsoleConfigClassLoader) {
				((ConsoleConfigClassLoader)classLoaderTmp).close();
			}
			classLoaderTmp = classLoaderTmp.getParent();
		}
		classLoader = null;
	}

	public void build() {
		configuration = buildWith(null, true);
		fireConfigurationBuilt();
	}

	private Configuration buildJPAConfiguration(String persistenceUnit, Properties properties, String entityResolver, boolean includeMappings) {
		if(StringHelper.isEmpty( persistenceUnit )) {
			persistenceUnit = null;
		}
		try {
			Map<Object,Object> overrides = new HashMap<Object,Object>();
			if(properties!=null) {
				overrides.putAll( properties );
			}
			if(StringHelper.isNotEmpty( prefs.getNamingStrategy())) {
				overrides.put( "hibernate.ejb.naming_strategy", prefs.getNamingStrategy() ); //$NON-NLS-1$
			}
			
			if(StringHelper.isNotEmpty( prefs.getDialectName())) {
				overrides.put( "hibernate.dialect", prefs.getDialectName() ); //$NON-NLS-1$
			}

			if(!includeMappings) {
				overrides.put( "hibernate.archive.autodetection", "none" );  //$NON-NLS-1$//$NON-NLS-2$
			}

			Class<?> clazz = ReflectHelper.classForName("org.hibernate.ejb.Ejb3Configuration", ConsoleConfiguration.class); //$NON-NLS-1$
			Object ejb3cfg = clazz.newInstance();

			if(StringHelper.isNotEmpty(entityResolver)) {
				Class<?> resolver = ReflectHelper.classForName(entityResolver, this.getClass());
				Object object = resolver.newInstance();
				Method method = clazz.getMethod("setEntityResolver", new Class[] { EntityResolver.class });//$NON-NLS-1$
				method.invoke(ejb3cfg, new Object[] { object } );
			}

			Method method = clazz.getMethod("configure", new Class[] { String.class, Map.class }); //$NON-NLS-1$
			if ( method.invoke(ejb3cfg, new Object[] { persistenceUnit, overrides } ) == null ) {
				String out = NLS.bind(ConsoleMessages.ConsoleConfiguration_persistence_unit_not_found, persistenceUnit);
				throw new HibernateConsoleRuntimeException(out);
			}

			method = clazz.getMethod("getHibernateConfiguration", new Class[0]);//$NON-NLS-1$
			Configuration invoke = (Configuration) method.invoke(ejb3cfg, (Object[])null);
			invoke = configureConnectionProfile(invoke);

			return invoke;
		}
		catch (HibernateConsoleRuntimeException he) {
			throw he;
		}
		catch (Exception e) {
			throw new HibernateConsoleRuntimeException(ConsoleMessages.ConsoleConfiguration_could_not_create_jpa_based_configuration,e);
		}
	}

	@SuppressWarnings("unchecked")
	private Configuration buildAnnotationConfiguration() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<Configuration> clazz = ReflectHelper
				.classForName( "org.hibernate.cfg.AnnotationConfiguration" ); //$NON-NLS-1$
		Configuration newInstance = clazz.newInstance();
		return newInstance;
	}

	protected void refreshProfile(IConnectionProfile profile) {
		// refresh profile (refresh jpa connection):
		// get fresh information about current db structure and update error markers  
		if (profile.getConnectionState() == IConnectionProfile.CONNECTED_STATE){
			profile.disconnect(null);
		}
		profile.connect(null);
	}

	/*
	 * try get a path to the sql driver jar file from DTP connection profile
	 */
	protected String getConnectionProfileDriverURL() {
		String connectionProfile = prefs.getConnectionProfileName();
		if (connectionProfile == null) {
			return null;
		}
		IConnectionProfile profile = ProfileManager.getInstance().getProfileByName(connectionProfile);
		if (profile == null) {
			return null;
		}
		refreshProfile(profile);
		//
		Properties cpProperties = profile.getProperties(profile.getProviderId());
		String driverJarPath = cpProperties.getProperty("jarList"); //$NON-NLS-1$
		return driverJarPath;
	}

	/*
	 * get custom classpath URLs 
	 */
	protected URL[] getCustomClassPathURLs() {
		URL[] customClassPathURLsTmp = prefs.getCustomClassPathURLS();
		URL[] customClassPathURLs = null;
		String driverURL = getConnectionProfileDriverURL();
		URL url = null;
		if (driverURL != null) {
			try {
				url = new URL("file:/" + driverURL); //$NON-NLS-1$
			} catch (MalformedURLException e) {
				// just ignore
			}
		}
		// should DTP connection profile driver jar file be inserted
		boolean insertFlag = ( url != null );
		if (insertFlag) {
			for (int i = 0; i < customClassPathURLsTmp.length; i++) {
				if (url.equals(customClassPathURLsTmp[i])) {
					insertFlag = false;
					break;
				}
			}
		}
		if (insertFlag) {
			customClassPathURLs = new URL[customClassPathURLsTmp.length + 1];
	        System.arraycopy(customClassPathURLsTmp, 0, 
	        		customClassPathURLs, 0, customClassPathURLsTmp.length);
	        // insert DTP connection profile driver jar file URL after the default classpath entries
			customClassPathURLs[customClassPathURLsTmp.length] = url;
		}
		else {
			customClassPathURLs = customClassPathURLsTmp;
		}
		return customClassPathURLs;
	}
	
	protected ConsoleConfigClassLoader createClassLoader() {
		final URL[] customClassPathURLs = getCustomClassPathURLs();
		ConsoleConfigClassLoader classLoader = AccessController.doPrivileged(new PrivilegedAction<ConsoleConfigClassLoader>() {
			public ConsoleConfigClassLoader run() {
				return new ConsoleConfigClassLoader(customClassPathURLs, getParentClassLoader()) {
					protected Class<?> findClass(String name) throws ClassNotFoundException {
						try {
							return super.findClass(name);
						} catch (ClassNotFoundException cnfe) {
							throw cnfe;
						}
					}
		
					protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
						try {
							return super.loadClass(name, resolve);
						} catch (ClassNotFoundException cnfe) {
							throw cnfe;
						}
					}
		
					public Class<?> loadClass(String name) throws ClassNotFoundException {
						try {
							return super.loadClass(name);
						} catch (ClassNotFoundException cnfe) {
							throw cnfe;
						}
					}
				};
			}
		});
		return classLoader;
	}

	/**
	 * @return
	 *
	 */
	public Configuration buildWith(final Configuration cfg, final boolean includeMappings) {
			if (classLoader == null) {
				classLoader = createClassLoader();
			}
			executionContext = new DefaultExecutionContext(getName(), classLoader);

			Configuration result = (Configuration) executionContext.execute(new ExecutionContext.Command() {

				public Object execute() {
					Configuration localCfg = cfg;

					Properties properties = prefs.getProperties();
					// to fix: JBIDE-5839 - setup this property: false is default value 
					// to make hibernate tools diff hibernate versions compatible
					if (properties.getProperty("hibernate.search.autoregister_listeners") == null) { //$NON-NLS-1$
						properties.setProperty("hibernate.search.autoregister_listeners", "false"); //$NON-NLS-1$ //$NON-NLS-2$
					}

					if(properties!=null) {
						// in case the transaction manager is empty then we need to inject a faketm since hibernate will still try and instantiate it.
						String str = properties.getProperty( "hibernate.transaction.manager_lookup_class" ); //$NON-NLS-1$
						if(str != null && StringHelper.isEmpty( str )) {
							properties.setProperty( "hibernate.transaction.manager_lookup_class", "org.hibernate.console.FakeTransactionManagerLookup"); //$NON-NLS-1$ //$NON-NLS-2$
							//properties.setProperty( "hibernate.transaction.factory_class", "");
						}
					}


					if(localCfg==null) {
						localCfg = buildConfiguration( properties, includeMappings );
					} else {
						//Properties origProperties = cfg.getProperties();
						//origProperties.putAll(properties);
						//cfg.setProperties(origProperties);
						// TODO: this is actually only for jdbc reveng...
						//localCfg = configureStandardConfiguration( includeMappings, localCfg, properties );
					}

					// here both setProperties and configxml have had their chance to tell which databasedriver is needed.
					registerFakeDriver(localCfg.getProperty(Environment.DRIVER) );
					//autoConfigureDialect(localCfg); Disabled for now since it causes very looong timeouts for non-running databases + i havent been needed until now...

					// TODO: jpa configuration ?
					if(includeMappings) {
						File[] mappingFiles = prefs.getMappingFiles();

						for (int i = 0; i < mappingFiles.length; i++) {
							File hbm = mappingFiles[i];
							localCfg = localCfg.addFile(hbm);
						}
					}
                    // TODO: HBX-
					localCfg.setProperty( "hibernate.temp.use_jdbc_metadata_defaults", "false" );  //$NON-NLS-1$//$NON-NLS-2$
					localCfg.setProperty( Environment.HBM2DDL_AUTO, "false" ); //$NON-NLS-1$

					return localCfg;
				}

				private void autoConfigureDialect(Configuration localCfg) {
					if (localCfg.getProperty(Environment.DIALECT) == null){
						String url = localCfg.getProperty(Environment.URL);
						String user = localCfg.getProperty(Environment.USER);
						String pass = localCfg.getProperty(Environment.PASS);
						Connection connection = null;
						try {
							connection = DriverManager.getConnection(url, user, pass); 
							//SQL Dialect:
							Dialect dialect = DialectFactory.buildDialect( localCfg.getProperties(), connection );
							localCfg.setProperty(Environment.DIALECT, dialect.toString());
						} catch (SQLException e) {
						//can't determine dialect
						}
						if (connection != null) {
							try {
								connection.close();
							} catch (SQLException e) {
								// ignore
							}
						}
					}
				}

			});


		return result;
	}

	@SuppressWarnings("unchecked")
	private Configuration loadConfigurationXML(Configuration localCfg, boolean includeMappings, EntityResolver entityResolver) {
		File configXMLFile = prefs.getConfigXMLFile();
		if(!includeMappings) {
			org.dom4j.Document doc;
			XMLHelper xmlHelper = new XMLHelper();
			InputStream stream = null;
			String resourceName = "<unknown>"; //$NON-NLS-1$
			if(configXMLFile!=null) {
				resourceName = configXMLFile.toString();
				try {
					stream = new FileInputStream( configXMLFile );
				}
				catch (FileNotFoundException e1) {
					throw new HibernateConsoleRuntimeException(ConsoleMessages.ConsoleConfiguration_could_not_access + configXMLFile, e1);
				}
			} else {
				resourceName = "/hibernate.cfg.xml"; //$NON-NLS-1$
				if (checkHibernateResoureExistence(resourceName)) {
					stream = ConfigHelper.getResourceAsStream( resourceName ); // simulate hibernate's default look up
				}
				else {
					return localCfg;
				}
			}

			try {
				List<Throwable> errors = new ArrayList<Throwable>();

				doc = xmlHelper.createSAXReader( resourceName, errors, entityResolver )
				.read( new InputSource( stream ) );
				if ( errors.size() != 0 ) {
					throw new MappingException(
							ConsoleMessages.ConsoleConfiguration_invalid_configuration,
							errors.get( 0 )
					);
				}


				List<Node> list = doc.getRootElement().element("session-factory").elements("mapping");  //$NON-NLS-1$ //$NON-NLS-2$
				for (Node element : list) {
					element.getParent().remove(element);
				}
				
				DOMWriter dw = new DOMWriter();
				Document document = dw.write(doc);
				return localCfg.configure( document );

			}
			catch (DocumentException e) {
				throw new HibernateException(
						ConsoleMessages.ConsoleConfiguration_could_not_parse_configuration + resourceName, e
				);
			}
			finally {
				try {
					if (stream!=null) stream.close();
				}
				catch (IOException ioe) {
					//log.warn( "could not close input stream for: " + resourceName, ioe );
				}
			}
		} else {
			if(configXMLFile!=null) {
				return localCfg.configure(configXMLFile);
			} else {
				Configuration resultCfg = localCfg;
				if (checkHibernateResoureExistence("/hibernate.cfg.xml")) { //$NON-NLS-1$
					resultCfg = localCfg.configure();
				}
				return resultCfg;
			}
		}
	}
	
	protected boolean checkHibernateResoureExistence(String resource) {
		InputStream is = null;
		try {
			is = ConfigHelper.getResourceAsStream(resource);
		}
		catch (HibernateException e) {
			// just ignore
		}
		finally {
			try {
				if (is != null) is.close();
			}
			catch (IOException e) {
				// ignore
			}
		}
		return (is != null);
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
	@SuppressWarnings("unchecked")
	private void registerFakeDriver(String driverClassName) {

		if(driverClassName!=null) {
			try {
				Class<Driver> driverClass = ReflectHelper.classForName(driverClassName);
				if(!fakeDrivers.containsKey(driverClassName) ) { // To avoid "double registration"
					FakeDelegatingDriver fakeDelegatingDriver = new FakeDelegatingDriver( driverClass.newInstance() );
					DriverManager.registerDriver(fakeDelegatingDriver);
					fakeDrivers.put(driverClassName,fakeDelegatingDriver);
				}
			}
			catch (ClassNotFoundException e) {
				String out = NLS.bind(ConsoleMessages.ConsoleConfiguration_problems_while_loading_database_driverclass, driverClassName);
				throw new HibernateConsoleRuntimeException(out, e);
			}
			catch (InstantiationException e) {
				String out = NLS.bind(ConsoleMessages.ConsoleConfiguration_problems_while_loading_database_driverclass, driverClassName);
				throw new HibernateConsoleRuntimeException(out, e);
			}
			catch (IllegalAccessException e) {
				String out = NLS.bind(ConsoleMessages.ConsoleConfiguration_problems_while_loading_database_driverclass, driverClassName);
				throw new HibernateConsoleRuntimeException(out, e);
			}
			catch (SQLException e) {
				String out = NLS.bind(ConsoleMessages.ConsoleConfiguration_problems_while_loading_database_driverclass, driverClassName);
				throw new HibernateConsoleRuntimeException(out, e);
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
					throw new HibernateConsoleRuntimeException(ConsoleMessages.ConsoleConfiguration_factory_not_closed_before_build_new_factory);
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
	List<ConsoleConfigurationListener> consoleCfgListeners = new ArrayList<ConsoleConfigurationListener>();

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
	
	private void fireConfigurationBuilt() {
		for (ConsoleConfigurationListener view : consoleCfgListeners) {
			view.configurationBuilt(this);
		}
	}	

	private void fireConfigurationReset() {
		for (ConsoleConfigurationListener view : consoleCfgListeners) {
			view.configurationReset(this);
		}
	}	

	private void fireQueryPageCreated(QueryPage qp) {
		for (ConsoleConfigurationListener view : consoleCfgListeners) {
			view.queryPageCreated(qp);
		}
	}

	private void fireFactoryBuilt() {
		for (ConsoleConfigurationListener view : consoleCfgListeners) {
			view.sessionFactoryBuilt(this, sessionFactory);
		}
	}

	private void fireFactoryClosing(SessionFactory sessionFactory2) {
		for (ConsoleConfigurationListener view : consoleCfgListeners) {
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
		return consoleCfgListeners.toArray(new ConsoleConfigurationListener[consoleCfgListeners.size()]);
	}


	public boolean isSessionFactoryCreated() {
		return sessionFactory!=null;
	}

	public ConsoleConfigurationPreferences getPreferences() {
		return prefs;
	}
	
	public File getConfigXMLFile() {
		File configXMLFile = null;
		if (prefs != null) {
			configXMLFile = prefs.getConfigXMLFile();
		}
		if (configXMLFile == null && classLoader != null) {
			URL url = classLoader.findResource("hibernate.cfg.xml"); //$NON-NLS-1$
			if (url != null) {
				URI uri = null;
				try {
					uri = url.toURI();
					configXMLFile = new File(uri);
				} catch (URISyntaxException e) {
					// ignore
				}
			}
		}
		if (configXMLFile == null) {
			URL url = Environment.class.getClassLoader().getResource("hibernate.cfg.xml"); //$NON-NLS-1$
			if (url != null) {
				URI uri = null;
				try {
					uri = url.toURI();
					configXMLFile = new File(uri);
				} catch (URISyntaxException e) {
					// ignore
				}
			}
		}
		return configXMLFile;
	}

	public String toString() {
		return getClass().getName() + ":" + getName(); //$NON-NLS-1$
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
			catch (HibernateConsoleRuntimeException he) {
				throw he;
			}
			catch (Exception e) {
				throw new HibernateConsoleRuntimeException(ConsoleMessages.ConsoleConfiguration_could_not_load_annotationconfiguration,e);
			}
		} else if(prefs.getConfigurationMode().equals( ConfigurationMode.JPA )) {
			try {
				localCfg = buildJPAConfiguration( getPreferences().getPersistenceUnitName(), properties, prefs.getEntityResolverName(), includeMappings );
			}
			catch (HibernateConsoleRuntimeException he) {
				throw he;
			}
			catch (Exception e) {
				throw new HibernateConsoleRuntimeException(ConsoleMessages.ConsoleConfiguration_could_not_load_jpa_configuration,e);
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
				throw new HibernateConsoleRuntimeException(ConsoleMessages.ConsoleConfiguration_could_not_configure_entity_resolver + prefs.getEntityResolverName(), c);
			}
		}
		localCfg.setEntityResolver(entityResolver);

		if(StringHelper.isNotEmpty( prefs.getNamingStrategy())) {
			try {
				NamingStrategy ns = (NamingStrategy) ReflectHelper.classForName(prefs.getNamingStrategy()).newInstance();
				localCfg.setNamingStrategy( ns );
			} catch (Exception c) {
				throw new HibernateConsoleRuntimeException(ConsoleMessages.ConsoleConfiguration_could_not_configure_naming_strategy + prefs.getNamingStrategy(), c);
			}
		}
		
		localCfg = loadConfigurationXML( localCfg, includeMappings, entityResolver );
		localCfg = configureConnectionProfile(localCfg);
		
		// replace dialect if it is set in preferences
		if(StringHelper.isNotEmpty( prefs.getDialectName())) {
			localCfg.setProperty("hibernate.dialect", prefs.getDialectName()); //$NON-NLS-1$
		}

		return localCfg;
	}

	private Configuration configureConnectionProfile(Configuration localCfg) {
		String connectionProfile = prefs.getConnectionProfileName();
		if(connectionProfile==null) {
			return localCfg;
		}
		
		IConnectionProfile profile = ProfileManager.getInstance().getProfileByName(connectionProfile);
		if (profile != null) {
			refreshProfile(profile);
			//
			final Properties invokeProperties = localCfg.getProperties();
			// set this property to null!
			invokeProperties.remove(Environment.DATASOURCE);
			localCfg.setProperties(invokeProperties);
			Properties cpProperties = profile.getProperties(profile.getProviderId());
			// seems we should not setup dialect here
			//String dialect = "org.hibernate.dialect.HSQLDialect";//cpProperties.getProperty("org.eclipse.datatools.connectivity.db.driverClass");
			//invoke.setProperty(Environment.DIALECT, dialect);
			String driver = cpProperties.getProperty("org.eclipse.datatools.connectivity.db.driverClass"); //$NON-NLS-1$
			localCfg.setProperty(Environment.DRIVER, driver);
			// TODO:
			String driverJarPath = cpProperties.getProperty("jarList"); //$NON-NLS-1$
			String url = cpProperties.getProperty("org.eclipse.datatools.connectivity.db.URL"); //$NON-NLS-1$
			//url += "/";// +  cpProperties.getProperty("org.eclipse.datatools.connectivity.db.databaseName");
			localCfg.setProperty(Environment.URL, url);
			String user = cpProperties.getProperty("org.eclipse.datatools.connectivity.db.username"); //$NON-NLS-1$
			if (null != user && user.length() > 0) {
				localCfg.setProperty(Environment.USER, user);
			}
			String pass = cpProperties.getProperty("org.eclipse.datatools.connectivity.db.password"); //$NON-NLS-1$
			if (null != pass && pass.length() > 0) {
				localCfg.setProperty(Environment.PASS, pass);
			}
		} else {
			String out = NLS.bind(ConsoleMessages.ConsoleConfiguration_connection_profile_not_found, connectionProfile);
			throw new HibernateConsoleRuntimeException(out);			
		}
		return localCfg;
	}

}