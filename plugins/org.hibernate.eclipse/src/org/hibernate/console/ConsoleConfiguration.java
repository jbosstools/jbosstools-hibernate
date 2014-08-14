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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.osgi.util.NLS;
import org.hibernate.console.execution.DefaultExecutionContext;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.console.execution.ExecutionContextHolder;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.console.preferences.PreferencesClassPathUtils;
import org.hibernate.eclipse.console.common.HibernateExtension;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IEnvironment;
import org.jboss.tools.hibernate.spi.ISessionFactory;
import org.jboss.tools.hibernate.spi.ISettings;

public class ConsoleConfiguration implements ExecutionContextHolder {

	private ExecutionContext executionContext;
	private ConsoleConfigClassLoader classLoader = null;

	private Map<String, FakeDelegatingDriver> fakeDrivers = new HashMap<String, FakeDelegatingDriver>();

	/* TODO: move this out to the actual users of the configuraiton/sf ? */
	private IConfiguration configuration;
	private ISessionFactory sessionFactory;
	
	//****************************** EXTENSION **********************
	private String hibernateVersion = "==<None>=="; //set to some unused value //$NON-NLS-1$
	
	private HibernateExtension extension;
	
	private void loadHibernateExtension(){
		extension = new HibernateExtension(prefs);
//		String version = hibernateVersion == null ? "3.5" : hibernateVersion;//3.5 is a default version //$NON-NLS-1$
//		HibernateExtensionDefinition def = HibernateExtensionManager.findHibernateExtensionDefinition(version);
//		if (def != null){
//			HibernateExtension hibernateExtension = def.createHibernateExtensionInstance();
//			hibernateExtension.setConsoleConfigurationPreferences(prefs);
//			extension = hibernateExtension;
//		} else {
//			throw new IllegalArgumentException("Can't find definition for hibernate version " + version); //$NON-NLS-1$
//		}
	}
	
	private void updateHibernateVersion(String hibernateVersion){
		if (!equals(this.hibernateVersion, hibernateVersion)){
			this.hibernateVersion = hibernateVersion;
			loadHibernateExtension();
		}
	}
	
    private boolean equals(String str1, String str2) {
        return (str1 == null ? str2 == null : str1.equals(str2) );
    }

    public HibernateExtension getHibernateExtension(){
		updateHibernateVersion(prefs.getHibernateVersion());//reinit if necessary
		return this.extension;
	}

	//****************************** EXTENSION **********************
	
	public ConsoleConfiguration(ConsoleConfigurationPreferences config) {
		prefs = config;
	}

	/** Unique name for this configuration */
	public String getName() {
		return prefs.getName();
	}

	public synchronized Object execute(Command c) {
		if (executionContext != null) {
			return executionContext.execute(c);
		}
		final String msg = NLS.bind(ConsoleMessages.ConsoleConfiguration_null_execution_context, getName());
		throw new HibernateConsoleRuntimeException(msg);
	}

	public ConsoleConfigurationPreferences prefs = null;

	/**
	 * Reset configuration, session factory, class loader and execution context.
	 *
	 */
	public synchronized boolean reset() {
		boolean resetted = false;
		// resetting state
		if (getHibernateExtension() != null ) {
			getHibernateExtension().reset();
			getHibernateExtension().closeSessionFactory();
		}
		if (configuration != null) {
			configuration = null;
			resetted = true;
		}
		resetted = resetted | closeSessionFactory() | cleanUpClassLoader();

		if (resetted) {
			fireConfigurationReset();
		}
		executionContext = null;
		return resetted;
	}

	protected boolean cleanUpClassLoader() {
		boolean resetted = false;
		if (executionContext != null) {
			executionContext.execute(new Command() {
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
		if (fakeDrivers.size() > 0) {
			fakeDrivers.clear();
			resetted = true;
		}
		ClassLoader classLoaderTmp = classLoader;
		while (classLoaderTmp != null) {
			if (classLoaderTmp instanceof ConsoleConfigClassLoader) {
				((ConsoleConfigClassLoader)classLoaderTmp).close();
				resetted = true;
			}
			classLoaderTmp = classLoaderTmp.getParent();
		}
		if (classLoader != null) {
			classLoader = null;
			resetted = true;
		}
		return resetted;
	}
	
	/**
	 * Create class loader - so it uses the original urls list from preferences. 
	 */
	protected void reinitClassLoader() {
		//the class loader caches user's compiled classes
		//need to rebuild it on every console configuration rebuild to pick up latest versions.
		final URL[] customClassPathURLs = PreferencesClassPathUtils.getCustomClassPathURLs(prefs);
		cleanUpClassLoader();
		classLoader = createClassLoader(customClassPathURLs);
	}

	public void build() {
		reset();
		getHibernateExtension().build();
//		configuration = getHibernateExtension().getConfiguration();
//		reinitClassLoader();
//		executionContext = new DefaultExecutionContext(getName(), classLoader);
		configuration = buildWith(null, true);
		fireConfigurationBuilt();
	}
	
	protected ConsoleConfigClassLoader createClassLoader(final URL[] customClassPathURLs) {
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
					
					public URL getResource(String name) {
					      return super.getResource(name);
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
	public IConfiguration buildWith(final IConfiguration cfg, final boolean includeMappings) {
		reinitClassLoader();
		executionContext = new DefaultExecutionContext(getName(), classLoader);
		IConfiguration result = (IConfiguration)execute(new Command() {
			public Object execute() {
				ConfigurationFactory csf = new ConfigurationFactory(prefs, fakeDrivers);
				return csf.createConfiguration(cfg, includeMappings);
			}
		});
		return result;
	}

	protected ClassLoader getParentClassLoader() {
		HibernateExtension extension = getHibernateExtension();
		if (extension != null) {
			return extension.getHibernateService().getClassLoader();
		} else {
			return Thread.currentThread().getContextClassLoader();
		}
	}
	
	public IConfiguration getConfiguration() {
		return configuration;
	}
	/**
	 * @return
	 */
	public boolean hasConfiguration() {
		return configuration != null;
	}
	
	public void buildMappings(){
		execute(new Command() {
			public Object execute() {
				getConfiguration().buildMappings();
				return null;
			}
		});
		getHibernateExtension().buildMappings();
	}

	public void buildSessionFactory() {
		execute(new Command() {
			public Object execute() {
				if (sessionFactory != null) {
					throw new HibernateConsoleRuntimeException(ConsoleMessages.ConsoleConfiguration_factory_not_closed_before_build_new_factory);
				}
				sessionFactory = getConfiguration().buildSessionFactory();
				fireFactoryBuilt();
				return null;
			}
		});
		getHibernateExtension().buildSessionFactory();
	}

	public ISessionFactory getSessionFactory() {
		return sessionFactory;
	}


	int execcount;
	ArrayList<ConsoleConfigurationListener> consoleCfgListeners = new ArrayList<ConsoleConfigurationListener>();

	public QueryPage executeHQLQuery(final String hql) {
		return executeHQLQuery(hql, new QueryInputModel(extension.getHibernateService()));
	}

	public QueryPage executeHQLQuery(final String hql, final QueryInputModel queryParameters) {
		QueryPage qp = getHibernateExtension().executeHQLQuery(hql, queryParameters);
		qp.setId(++execcount);
		fireQueryPageCreated(qp);
		return qp;
	}

	public QueryPage executeBSHQuery(final String queryString, final QueryInputModel model) {
		QueryPage qp = getHibernateExtension().executeCriteriaQuery(queryString, model);
		qp.setId(++execcount);
		fireQueryPageCreated(qp);
		return qp;
	}
	
	@SuppressWarnings("unchecked")
	// clone listeners to thread safe iterate over array
	private ArrayList<ConsoleConfigurationListener> cloneConsoleCfgListeners() {
		return (ArrayList<ConsoleConfigurationListener>)consoleCfgListeners.clone();
	}

	private void fireConfigurationBuilt() {
		for (ConsoleConfigurationListener view : cloneConsoleCfgListeners()) {
			view.configurationBuilt(this);
		}
	}	

	private void fireConfigurationReset() {
		for (ConsoleConfigurationListener view : cloneConsoleCfgListeners()) {
			view.configurationReset(this);
		}
	}	

	private void fireQueryPageCreated(QueryPage qp) {
		for (ConsoleConfigurationListener view : cloneConsoleCfgListeners()) {
			view.queryPageCreated(qp);
		}
	}

	private void fireFactoryBuilt() {
		for (ConsoleConfigurationListener view : cloneConsoleCfgListeners()) {
			view.sessionFactoryBuilt(this, sessionFactory);
		}
	}

	private void fireFactoryClosing(ISessionFactory sessionFactory2) {
		for (ConsoleConfigurationListener view : cloneConsoleCfgListeners()) {
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
		return sessionFactory != null;
	}

	public ConsoleConfigurationPreferences getPreferences() {
		return prefs;
	}
	
	public File getConfigXMLFile() {
		IEnvironment environment = getHibernateExtension().getHibernateService().getEnvironment();
		File configXMLFile = null;
		if (prefs != null) {
			configXMLFile = prefs.getConfigXMLFile();
		}
		if (configXMLFile == null) {
			URL url = null;
			//reinitClassLoader();
			if (classLoader != null) {
				url = classLoader.findResource("hibernate.cfg.xml"); //$NON-NLS-1$
			}
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
			URL url = environment.getWrappedClass().getClassLoader().getResource("hibernate.cfg.xml"); //$NON-NLS-1$
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

	public boolean closeSessionFactory() {
		boolean resetted = false;
		if (sessionFactory != null) {
			fireFactoryClosing(sessionFactory);
			sessionFactory.close();
			sessionFactory = null;
			resetted = true;
		}
		if (getHibernateExtension() != null){
			getHibernateExtension().closeSessionFactory();
		}
		return resetted;
	}

	public ISettings getSettings(final IConfiguration cfg) {
		return (ISettings) execute(new Command() {
				public Object execute() {
					return cfg.buildSettings();
				}
			}
		);
	}
}
