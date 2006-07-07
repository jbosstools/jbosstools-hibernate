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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.Type;


public class HQLQueryPage extends AbstractQueryPage {

	private Query query;
	private String queryString;
	private final ConsoleQueryParameter[] queryParameters;
	public List getList() {
		if (query==null) return Collections.EMPTY_LIST;
		if (list == null) {
			try {
				
				//list = query.list();
				list = new ArrayList();
				setupParameters(query, queryParameters);
				Iterator iter = query.list().iterator(); // need to be user-controllable to toggle between iterate, scroll etc.
				while (iter.hasNext() ) {
					Object element = iter.next();
					list.add(element);
				}
				pcs.firePropertyChange("list", null, list);
			} 
			catch (HibernateException e) {
				list = Collections.EMPTY_LIST;
				addException(e);				                
			} catch (IllegalArgumentException e) {
				list = Collections.EMPTY_LIST;
				addException(e);
			}
		}
		return list;
	}

	private void setupParameters(Query query2, ConsoleQueryParameter[] queryParameters2) {
		for (int i = 0; i < queryParameters2.length; i++) {
			ConsoleQueryParameter parameter = queryParameters2[i];
			try {
				int pos = Integer.parseInt(parameter.getName());
				query2.setParameter(pos, parameter.getValue(), parameter.getType());
			} catch(NumberFormatException nfe) {
				query2.setParameter(parameter.getName(), parameter.getValue(), parameter.getType());	
			}			
		}		
	}

	/**
	 * @param session
	 * @param string
	 * @param queryParameters 
	 */
	public HQLQueryPage(ConsoleConfiguration cfg, String string, ConsoleQueryParameter[] queryParameters) {
		super(cfg);
		queryString = string;
		this.queryParameters = queryParameters;
	}

	public void setSession(Session s) {
		super.setSession(s);
		try {			             
			query = this.getSession().createQuery(queryString);
		} catch (HibernateException e) {
			addException(e);			
		}
	}
	
    /**
     * @return
     */
    public String getQueryString() {
    	return queryString; // cannot use query since it might be null because of an error!    
    }

    public List getPathNames() {
    	List l = Collections.EMPTY_LIST;
    
    	try {
    		if(query==null) return l;
    		if(query.getReturnAliases()==null) {
    		Type[] t = query.getReturnTypes();
    		l = new ArrayList(t.length);
    
    		for (int i = 0; i < t.length; i++) {
    			Type type = t[i];
    			if(type==null) {
    			    l.add("<multiple types>");	
    			} else {
    				l.add(type.getName() );
    			}
    		}
    		} else {
    			String[] t = query.getReturnAliases();
        		l = new ArrayList(t.length);
        
        		for (int i = 0; i < t.length; i++) {
        			l.add(t[i]);
        		}			
    		}
    	} catch (HibernateException he) {
    		addException(he);           
    	}
    
    	return l;
    }
}