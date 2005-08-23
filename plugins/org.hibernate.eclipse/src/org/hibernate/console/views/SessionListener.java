/*
 * Created on 12-08-2004
 *
 * 
 */
package org.hibernate.console.views;

import org.hibernate.Session;
import org.hibernate.console.ConsoleConfiguration;


/**
 * @author MAX
 *
 */
public interface SessionListener extends QueryListener {
	
	public void objectUpdated(ConsoleConfiguration config, Session session, Object o);
	
}
