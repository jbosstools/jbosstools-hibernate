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
import java.net.MalformedURLException;
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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
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

public class ConsoleConfiguration implements ExecutionContextHolder {

	private ExecutionContext executionContext;
	private ConsoleConfigClassLoader classLoader = null;

	private Map<String, FakeDelegatingDriver> fakeDrivers = new HashMap<String, FakeDelegatingDriver>();

	/* TODO: move this out to the actual users of the configuraiton/sf ? */
	private Configuration configuration;
	private SessionFactory sessionFactory;
	
	// internal flag to prohibit long time profile refresh
	private boolean rejectProfileRefresh = false;

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
		if (!rejectProfileRefresh) {
			ConnectionProfileUtil.refreshProfile(profile);
		}
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
		} else {
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
		Configuration result = (Configuration)execute(new Command() {
			public Object execute() {
				ConfigurationFactory csf = new ConfigurationFactory(prefs, fakeDrivers, rejectProfileRefresh);
				return csf.createConfiguration(cfg, includeMappings);
			}
		});
		return result;
	}

	protected ClassLoader getParentClassLoader() {
		return ConsoleConfiguration.class.getClassLoader();
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
	List<ConsoleConfigurationListener> consoleCfgListeners = new ArrayList<ConsoleConfigurationListener>();

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
		if (sessionFactory != null) {
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

	public boolean isRejectProfileRefresh() {
		return rejectProfileRefresh;
	}

	public void setRejectProfileRefresh(boolean rejectProfileRefresh) {
		this.rejectProfileRefresh = rejectProfileRefresh;
	}

}