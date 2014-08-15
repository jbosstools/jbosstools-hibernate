package org.hibernate.eclipse.console.common;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.osgi.util.NLS;
import org.hibernate.console.ConfigurationFactory;
import org.hibernate.console.ConsoleConfigClassLoader;
import org.hibernate.console.ConsoleMessages;
import org.hibernate.console.FakeDelegatingDriver;
import org.hibernate.console.QueryInputModel;
import org.hibernate.console.QueryPage;
import org.hibernate.console.execution.DefaultExecutionContext;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.console.preferences.PreferencesClassPathUtils;
import org.jboss.tools.hibernate.spi.HibernateException;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IService;
import org.jboss.tools.hibernate.spi.ISession;
import org.jboss.tools.hibernate.spi.ISessionFactory;
import org.jboss.tools.hibernate.spi.ISettings;
import org.jboss.tools.hibernate.spi.ServiceLookup;

public class HibernateExtension {

	private IConfiguration configuration;
	
	private ConsoleConfigClassLoader classLoader = null;

	private ExecutionContext executionContext;
	
	private ConsoleConfigurationPreferences prefs;
	
	private ISessionFactory sessionFactory;
	
	private Map<String, FakeDelegatingDriver> fakeDrivers = new HashMap<String, FakeDelegatingDriver>();
	
	private ConsoleExtension consoleExtension;
	
	public HibernateExtension(ConsoleConfigurationPreferences prefs) {
		this.prefs = prefs;
		consoleExtension = new ConsoleExtension();
		consoleExtension.setHibernateExtention(this);
	}
	
	public ConsoleExtension getConsoleExtension() {
		return consoleExtension;
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
	
	private ClassLoader getParentClassLoader() {
		return getHibernateService().getClassLoader();
	}

	public String getHibernateVersion() {
		String result = prefs.getHibernateVersion();
		// return hibernate 3.5 by default
		// TODO do something smarter here
		return result != null ? result : "3.5"; //$NON-NLS-1$
	}

	public QueryPage executeHQLQuery(final String hql,
			final QueryInputModel queryParameters) {
		return (QueryPage)execute(new Command() {
			public Object execute() {
				ISession session = sessionFactory.openSession();
				QueryPage qp = new HQLQueryPage(HibernateExtension.this, hql,queryParameters);
				qp.setSession(session);
				return qp;
			}
		});
	}

	public QueryPage executeCriteriaQuery(final String criteriaCode,
			final QueryInputModel model) {
		return (QueryPage)execute(new Command() {
			public Object execute() {
				ISession session = sessionFactory.openSession();
				QueryPage qp = new JavaPage(HibernateExtension.this,criteriaCode,model);
				qp.setSession(session);
				return qp;
			}
		});
	}

	public void build() {
		configuration = buildWith(null, true);
	}

	public void buildSessionFactory() {
		execute(new Command() {
			public Object execute() {
				if (sessionFactory != null) {
					throw new HibernateException("Factory was not closed before attempt to build a new Factory"); //$NON-NLS-1$
				}
				sessionFactory = configuration.buildSessionFactory();
				return null;
			}
		});
	}

	public boolean closeSessionFactory() {
		boolean res = false;
		if (sessionFactory != null) {
			sessionFactory.close();
			sessionFactory = null;
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
	
	public String getName() {
		return prefs.getName();
	}
	
	public Object execute(Command c) {
		if (executionContext != null) {
			return executionContext.execute(c);
		}
		final String msg = NLS.bind(ConsoleMessages.ConsoleConfiguration_null_execution_context, getName());
		throw new HibernateException(msg);
	}

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

	public boolean hasConfiguration() {
		return configuration != null;
	}

	/**
	 * @return
	 */
	public IConfiguration getConfiguration() {
		return configuration;
	}
	
	public ISettings getSettings(final IConfiguration cfg) {
		return (ISettings) execute(new Command() {
			public Object execute() {
				return cfg.buildSettings();
			}
		});
	}

	public boolean isSessionFactoryCreated() {
		return sessionFactory != null;
	}
	
	public String generateSQL(final String query) {
		return QueryHelper.generateSQL(executionContext, sessionFactory, query, getHibernateService());
	}

	public void buildMappings() {
		execute(new Command() {
			public Object execute() {
				getConfiguration().buildMappings();
				return null;
			}
		});
	}

	public boolean hasExecutionContext() {
		return executionContext != null;
	}

	public String getConsoleConfigurationName() {
		return prefs.getName();
	}

	public IService getHibernateService() {
		return ServiceLookup.findService(getHibernateVersion());
	}

}
