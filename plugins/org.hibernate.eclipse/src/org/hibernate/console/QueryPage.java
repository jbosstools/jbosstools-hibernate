/*
 * Created on 27-09-2003
 *
 */
package org.hibernate.console;

import java.beans.PropertyChangeListener;
import java.util.List;

import org.hibernate.Session;

/**
 * @author MAX
 *
 */
public interface QueryPage {
		
    public abstract List getList();
    /**
     * @param i
     */
    public abstract void setId(int i);
    public abstract List getPathNames();
    public abstract void release();
    /**
     * @return
     */
    public abstract int getID();
    /**
     * @return
     */
    public abstract boolean isSticky();
    /**
     * @return
     */
    public abstract List getExceptions();
    /**
     * @return
     */
    public abstract String getQueryString();
    /**
     * @param b
     */
    public abstract void setSticky(boolean b);
    
    public Session getSession();
    public void setSession(Session session);
    
	public ConsoleConfiguration getConsoleConfiguration();
	
    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void addPropertyChangeListener(
    		String propertyName,
			PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(
    		String propertyName,
			PropertyChangeListener listener);
	
    public void addException(Throwable he);
    
}