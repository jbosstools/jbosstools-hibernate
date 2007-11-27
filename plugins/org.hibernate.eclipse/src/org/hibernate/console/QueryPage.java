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