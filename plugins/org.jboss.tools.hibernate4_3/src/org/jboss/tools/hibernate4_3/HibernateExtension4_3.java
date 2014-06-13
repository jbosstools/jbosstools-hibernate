/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate4_3;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.osgi.util.NLS;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.cfg.Settings;
import org.hibernate.console.ConsoleConfigClassLoader;
import org.hibernate.console.ConsoleMessages;
import org.hibernate.console.QueryInputModel;
import org.hibernate.console.QueryPage;
import org.hibernate.console.execution.DefaultExecutionContext;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.console.ext.HibernateException;
import org.hibernate.console.ext.HibernateExtension;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.console.preferences.PreferencesClassPathUtils;
import org.hibernate.service.ServiceRegistry;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.ISession;
import org.jboss.tools.hibernate.spi.ISessionFactory;

/**
 * 
 * @author Dmitry Geraskov
 *
 */
public class HibernateExtension4_3 implements HibernateExtension {
	
	private ConsoleConfigClassLoader classLoader = null;
	
	private ExecutionContext executionContext;
	
	private ConsoleConfigurationPreferences prefs;
	
	private IConfiguration configuration;
	
	private ISessionFactory sessionFactory;
	
	private ServiceRegistry serviceRegistry;
	
	private Map<String, FakeDelegatingDriver> fakeDrivers = new HashMap<String, FakeDelegatingDriver>();

	public HibernateExtension4_3() {
	}

	@Override
	public String getHibernateVersion() {
		return "4.3";
	}
	
	@Override
	public QueryPage executeHQLQuery(final String hql,
			final QueryInputModel queryParameters) {
		return (QueryPage)execute(new Command() {
			public Object execute() {
				ISession session = sessionFactory.openSession();
				QueryPage qp = new HQLQueryPage(HibernateExtension4_3.this, hql,queryParameters);
				qp.setSession(session);
				return qp;
			}
		});
	}

	@Override
	public QueryPage executeCriteriaQuery(final String criteriaCode,
			final QueryInputModel model) {
		return (QueryPage)execute(new Command() {
			public Object execute() {
				ISession session = sessionFactory.openSession();
				QueryPage qp = new JavaPage(HibernateExtension4_3.this,criteriaCode,model);
				qp.setSession(session);
				return qp;
			}
		});
	}

	/**
	 * @param ConsoleConfigurationPreferences the prefs to set
	 */
	public void setConsoleConfigurationPreferences(ConsoleConfigurationPreferences prefs) {
		this.prefs = prefs;
	}
	
	public void build() {
		configuration = buildWith(null, true);
	}

	@Override
	public void buildSessionFactory() {
		execute(new Command() {
			public Object execute() {
				if (sessionFactory != null) {
					throw new HibernateException("Factory was not closed before attempt to build a new Factory");
				}
				serviceRegistry =  new StandardServiceRegistryBuilder()
					.applySettings( configuration.getProperties())
					.build();
				sessionFactory = configuration.buildSessionFactory(serviceRegistry);
				return null;
			}
		});
	}

	@Override
	public boolean closeSessionFactory() {
		boolean res = false;
		if (sessionFactory != null) {
			sessionFactory.close();
			sessionFactory = null;
			( (StandardServiceRegistryImpl) serviceRegistry ).destroy();
			serviceRegistry = null;
			res = true;
		}
		return res;
	}

	public IConfiguration buildWith(final IConfiguration cfg, final boolean includeMappings) {
		reinitClassLoader();
		//TODO handle user libraries here
		executionContext = new DefaultExecutionContext(prefs.getName(), classLoader);
		IConfiguration result = (IConfiguration)execute(new Command() {
			public Object execute() {
				ConfigurationFactory cf = new ConfigurationFactory(prefs, fakeDrivers);
				return cf.createConfiguration(cfg, includeMappings);
			}
		});
		return result;
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
	
	protected ConsoleConfigClassLoader createClassLoader(final URL[] customClassPathURLs) {
		ConsoleConfigClassLoader classLoader = AccessController.doPrivileged(new PrivilegedAction<ConsoleConfigClassLoader>() {
			public ConsoleConfigClassLoader run() {
				return new ConsoleConfigClassLoader(customClassPathURLs, Thread.currentThread().getContextClassLoader()) {
					protected Class<?> findClass(String name) throws ClassNotFoundException {
						try {
							return super.findClass(name);
						} catch (ClassNotFoundException cnfe) {
							throw cnfe;
						} catch (IllegalStateException e){
							e.printStackTrace();
							throw e;
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

	public String getName() {
		return prefs.getName();
	}
	
	public ExecutionContext getExecutionContext() {
		return executionContext;
	}
	
	public Object execute(Command c) {
		if (executionContext != null) {
			return executionContext.execute(c);
		}
		final String msg = NLS.bind(ConsoleMessages.ConsoleConfiguration_null_execution_context, getName());
		throw new HibernateException(msg);
	}

	@Override
	public boolean reset() {
		boolean resetted = false;
		// reseting state
		if (configuration != null) {
			configuration = null;
			resetted = true;
		}
		
		resetted = resetted | closeSessionFactory() | cleanUpClassLoader();

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

	@Override
	public boolean hasConfiguration() {
		return configuration != null;
	}

	/**
	 * @return
	 */
	public IConfiguration getConfiguration() {
		return configuration;
	}
	
	public Settings getSettings(final IConfiguration cfg, final ServiceRegistry serviceRegistry) {
		return (Settings) execute(new Command() {
			public Object execute() {
				return cfg.buildSettings(serviceRegistry);
			}
		});
	}

	@Override
	public boolean isSessionFactoryCreated() {
		return sessionFactory != null;
	}
	
	public String generateSQL(final String query) {
		return QueryHelper.generateSQL(executionContext, sessionFactory, query);
	}

	@Override
	public void buildMappings() {
		execute(new Command() {
			public Object execute() {
				getConfiguration().buildMappings();
				return null;
			}
		});
	}
	
	@Override
	public boolean hasExecutionContext() {
		return executionContext != null;
	}
	
	@Override
	public String getConsoleConfigurationName() {
		return prefs.getName();
	}
}