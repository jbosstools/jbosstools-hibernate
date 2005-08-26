/*
 * Created on 27-09-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
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
			query2.setParameter(parameter.getName(), parameter.getValue(), parameter.getType());
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
    	return query.getQueryString();    
    }

    public List getPathNames() {
    	List l = Collections.EMPTY_LIST;
    
    	try {
    		if(query.getReturnAliases()==null) {
    		Type[] t = query.getReturnTypes();
    		l = new ArrayList(t.length);
    
    		for (int i = 0; i < t.length; i++) {
    			Type type = t[i];
    			l.add(type.getName() );
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