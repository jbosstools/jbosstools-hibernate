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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;

import org.eclipse.osgi.util.NLS;
import org.hibernate.cfg.Settings;
import org.hibernate.console.ConfigurationFactory;
import org.hibernate.console.ConsoleConfigClassLoader;
import org.hibernate.console.ConsoleMessages;
import org.hibernate.console.FakeDelegatingDriver;
import org.hibernate.console.QueryInputModel;
import org.hibernate.console.QueryPage;
import org.hibernate.console.execution.DefaultExecutionContext;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.console.ext.AbstractHibernateExtension;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.console.preferences.PreferencesClassPathUtils;
import org.hibernate.eclipse.console.common.HQLQueryPage;
import org.hibernate.eclipse.console.common.JavaPage;
import org.hibernate.eclipse.console.common.QueryHelper;
import org.hibernate.service.ServiceRegistry;
import org.jboss.tools.hibernate.spi.HibernateException;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IService;
import org.jboss.tools.hibernate.spi.ISession;
import org.jboss.tools.hibernate.util.ServiceLookup;

/**
 * 
 * @author Dmitry Geraskov
 *
 */
public class HibernateExtension4_3 extends AbstractHibernateExtension {
	
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
				sessionFactory = configuration.buildSessionFactory();
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
		return QueryHelper.generateSQL(executionContext, sessionFactory, query, getHibernateService());
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
	
	@Override
	public IService getHibernateService() {
		return ServiceLookup.service();
	}

}