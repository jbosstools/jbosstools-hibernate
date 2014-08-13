package org.hibernate.console.ext;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
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
	
	protected abstract void reinitClassLoader();
	
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
		return getHibernateService().getClass().getClassLoader();
	}

}
