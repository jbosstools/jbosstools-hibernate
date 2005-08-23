/*
 * Created on 2004-10-28 by max
 * 
 */
package org.hibernate.console.views;

import org.hibernate.console.ConsoleConfiguration;

/**
 * @author max
 *
 */
public interface ConfigurationChangedListener {

	public void configurationChanged(ConsoleConfiguration newConf);
}
