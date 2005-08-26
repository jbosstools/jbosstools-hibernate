/*
 * Created on 27-09-2003
 *
 */
package org.hibernate.console;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * @author MAX
 *
 */
public abstract class AbstractQueryPage implements QueryPage {

	PropertyChangeSupport pcs = new PropertyChangeSupport(this);    
    private int id;
    private Session session;
	private final ConsoleConfiguration cfg;
    protected List list;
    List pathNames;
    protected boolean sticky = true;
    private List exceptions = new ArrayList();

    /**
     * @param i
     */
    public void setId(int i) {
    	id = i;    
    }

	public AbstractQueryPage(ConsoleConfiguration cfg) {
		this.cfg = cfg;
	}
    /**
     * 
     */
    public List getExceptions() {
    	return exceptions;
    }

    public void release() {
    	if (getSession().isOpen() ) {
    		try {
    			getSession().close();
    		} 
    		catch (HibernateException e) {
    			exceptions.add(e);
    		}
    	}    	
    }

    /**
     * @return
     */
    public int getID() {
        return id;
    }

    /**
     * @return
     */
    public boolean isSticky() {
        return sticky;
    }

    /**
     * @param sticky
     */
    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

	public Session getSession() {
		return session;
	}

	public void setSession(Session s) {
		session = s;		
	}

	// currently notifications for exceptions and for list execution (hql)
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(
		String propertyName,
		PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(
		String propertyName,
		PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(propertyName, listener);
	}

	public void addException(Throwable e) {
		exceptions.add(e);
		release();
		pcs.firePropertyChange("exceptions", null, exceptions);
	}

	public ConsoleConfiguration getConsoleConfiguration() {
		return cfg;
	}
}
