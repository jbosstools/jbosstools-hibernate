/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
    protected List<Object> list;
    protected long queryTime = -1;				//shows how long query runs
    protected boolean sticky = true;
    private List<Throwable> exceptions = new ArrayList<Throwable>();
    protected String tabName;
    protected QueryInputModel model;

    /**
     * @param i
     */
    public void setId(int i) {
    	id = i;    
    }

    public int getResultSize() {
		if(list==null) { 
			return -1;
		} else {
			return list.size();
		}
	}

	public AbstractQueryPage(ConsoleConfiguration cfg, QueryInputModel model) {
		this.cfg = cfg;
		this.model = model;
	}
    /**
     * 
     */
    public List<Throwable> getExceptions() {
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
		pcs.firePropertyChange("exceptions", null, exceptions); //$NON-NLS-1$
	}

	public ConsoleConfiguration getConsoleConfiguration() {
		return cfg;
	}
	
	public long getQueryTime(){
		return queryTime;
	}

	public String getTabName() {
		return tabName;
	}
	public void setTabName(String tabName) {
		String oldValue = this.tabName;
		this.tabName = tabName;
		pcs.firePropertyChange("tabName", oldValue, tabName); //$NON-NLS-1$
	}

	public void setModel(QueryInputModel model) {
		this.model = model;
	}
}
