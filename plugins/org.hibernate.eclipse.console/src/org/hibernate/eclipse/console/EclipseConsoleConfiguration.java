/*
 * Created on 2004-11-01 by max
 * 
 */
package org.hibernate.eclipse.console;

import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ConsoleConfigurationPreferences;

/**
 * @author max
 *
 */
public class EclipseConsoleConfiguration extends ConsoleConfiguration {

	public EclipseConsoleConfiguration(ConsoleConfigurationPreferences config) {
		super(config);
	}

	protected ClassLoader getParentClassLoader() {				
		return HibernateConsolePlugin.getDefault().getClass().getClassLoader();
	}

}
