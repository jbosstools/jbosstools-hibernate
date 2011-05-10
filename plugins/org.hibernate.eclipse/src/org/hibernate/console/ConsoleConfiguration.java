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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.Settings;
import org.hibernate.console.execution.DefaultExecutionContext;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.console.execution.ExecutionContextHolder;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.console.preferences.PreferencesClassPathUtils;

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
		if (executionContext != null) {
			return executionContext.execute(c);
		}
		final String msg = NLS.bind(ConsoleMessages.ConsoleConfiguration_null_execution_context, getName());
		throw new HibernateConsoleRuntimeException(msg);
	}

	public ConsoleConfigurationPreferences prefs = null;

	/**
	 * Reset so a new configuration or sessionfactory is needed.
	 *
	 */
	public boolean reset() {
		boolean res = false;
		// reseting state
		if (configuration != null) {
			configuration = null;
			res = true;
		}
		boolean tmp = closeSessionFactory();
		res = res || tmp;
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
			res = true;
		}
		tmp = cleanUpClassLoader();
		res = res || tmp;
		if (res) {
			fireConfigurationReset();
		}
		executionContext = null;
		return res;
	}

	protected boolean cleanUpClassLoader() {
		boolean res = false;
		ClassLoader classLoaderTmp = classLoader;
		while (classLoaderTmp != null) {
			if (classLoaderTmp instanceof ConsoleConfigClassLoader) {
				((ConsoleConfigClassLoader)classLoaderTmp).close();
				res = true;
			}
			classLoaderTmp = classLoaderTmp.getParent();
		}
		if (classLoader != null) {
			classLoader = null;
			res = true;
		}
		return res;
	}
	
	/**
	 * Create class loader - so it uses the original urls list from preferences. 
	 */
	protected void reinitClassLoader() {
		boolean recreateFlag = true;
		final URL[] customClassPathURLs = PreferencesClassPathUtils.getCustomClassPathURLs(prefs);
		if (classLoader != null) {
			// check -> do not recreate class loader in case if urls list is the same
			final URL[] oldURLS = classLoader.getURLs();
			if (customClassPathURLs.length == oldURLS.length) {
				int i = 0;
				for (; i < oldURLS.length; i++) {
					if (!customClassPathURLs[i].sameFile(oldURLS[i])) {
						break;
					}
				}
				if (i == oldURLS.length) {
					recreateFlag = false;
				}
			}
		}
		if (recreateFlag) {
			reset();
			classLoader = createClassLoader(customClassPathURLs);
		}
	}

	public void build() {
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
	public Configuration buildWith(final Configuration cfg, final boolean includeMappings) {
		reinitClassLoader();
		executionContext = new DefaultExecutionContext(getName(), classLoader);
		Configuration result = (Configuration)execute(new Command() {
			public Object execute() {
				ConfigurationFactory csf = new ConfigurationFactory(prefs, fakeDrivers);
				return csf.createConfiguration(cfg, includeMappings);
			}
		});
		return result;
	}

	protected ClassLoader getParentClassLoader() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		//ClassLoader cl = ConsoleConfiguration.class.getClassLoader();
		return cl;
	}
	
	public Configuration getConfiguration() {
		return configuration;
	}
	/**
	 * @return
	 */
	public boolean hasConfiguration() {
		return configuration != null;
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
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}


	int execcount;
	ArrayList<ConsoleConfigurationListener> consoleCfgListeners = new ArrayList<ConsoleConfigurationListener>();

	public QueryPage executeHQLQuery(final String hql) {
		return executeHQLQuery(hql, new QueryInputModel());
	}

	public QueryPage executeHQLQuery(final String hql, final QueryInputModel queryParameters) {
		return (QueryPage)execute(new Command() {
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
		return (QueryPage)execute(new Command() {
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

	private void fireFactoryClosing(SessionFactory sessionFactory2) {
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
		File configXMLFile = null;
		if (prefs != null) {
			configXMLFile = prefs.getConfigXMLFile();
		}
		if (configXMLFile == null) {
			URL url = null;
			reinitClassLoader();
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

	public boolean closeSessionFactory() {
		boolean res = false;
		if (sessionFactory != null) {
			fireFactoryClosing(sessionFactory);
			sessionFactory.close();
			sessionFactory = null;
			res = true;
		}
		return res;
	}

	public Settings getSettings(final Configuration cfg) {
		return (Settings) execute(new Command() {
			public Object execute() {
				return cfg.buildSettings();
			}
		});
	}
}