/*
 * Created on 12-08-2004
 *
 */
package org.hibernate.console;

import org.hibernate.SessionFactory;


/**
 * 
 */
public interface ConsoleConfigurationListener {
	
	public void queryPageCreated(QueryPage qp);
	
	/** 
	 * called when the factory has just been created 
	 * @param builtSessionFactory TODO
	 */	 
	public void sessionFactoryBuilt(ConsoleConfiguration ccfg, SessionFactory builtSessionFactory);
	
	/**
	 * Called when this sessionFactory is about to be closed. Used for listeners to clean up resources related to this sessionfactory (such as closing sessions)
	 * @param configuration
	 * @param closedSessionFactory TODO
	 */
	public void sessionFactoryClosing(ConsoleConfiguration configuration, SessionFactory aboutToCloseFactory);
	
}
