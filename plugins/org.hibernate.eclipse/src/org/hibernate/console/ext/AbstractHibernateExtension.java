package org.hibernate.console.ext;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.console.ConsoleConfigClassLoader;
import org.hibernate.console.FakeDelegatingDriver;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.ISessionFactory;


public abstract class AbstractHibernateExtension implements HibernateExtension {

	protected IConfiguration configuration;
	
	protected ConsoleConfigClassLoader classLoader = null;

	protected ExecutionContext executionContext;
	
	protected ConsoleConfigurationPreferences prefs;
	
	protected ISessionFactory sessionFactory;
	
	protected Map<String, FakeDelegatingDriver> fakeDrivers = new HashMap<String, FakeDelegatingDriver>();

	protected abstract boolean cleanUpClassLoader();
	
	protected abstract ConsoleConfigClassLoader createClassLoader(final URL[] customClassPathURLs);

	protected abstract void reinitClassLoader();
	
}
