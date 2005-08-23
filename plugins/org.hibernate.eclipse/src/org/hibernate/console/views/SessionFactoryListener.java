/*
 * Created on 12-08-2004
 *
 */
package org.hibernate.console.views;

import org.hibernate.console.ConsoleConfiguration;

/**
 * 
 */
public interface SessionFactoryListener {
	
	/** called when the factory has just been created 
	 *
	 */	 
	public void factoryCreated(ConsoleConfiguration ccfg);
	
	/** called when something on the factory has changed (e.g. for updating statistics) 
	 * TODO: maybe add more specific listeners (like query executed or something ?) 
	 * @param ccfg TODO
	 **/
	public void factoryUpdated(ConsoleConfiguration ccfg);

	/**
	 * @param configuration
	 */
	public void factoryClosed(ConsoleConfiguration configuration);
	
}
