/**
 * 
 */
package org.hibernate.console;

import org.hibernate.SessionFactory;


public interface KnownConfigurationsListener {
	public void configurationAdded(ConsoleConfiguration root);
	
	public void sessionFactoryBuilt(ConsoleConfiguration ccfg, SessionFactory builtFactory);
	
	public void sessionFactoryClosing(ConsoleConfiguration configuration, SessionFactory closingFactory);
	
	public void configurationRemoved(ConsoleConfiguration root);
}