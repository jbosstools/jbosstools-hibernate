package org.hibernate.console.ext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.console.AbstractQueryPage;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.QueryInputModel;

/**
 * 
 * @author Dmitry Geraskov
 *
 */
public class HQLQueryPage extends AbstractQueryPage {

	private String queryString;
	private QueryResult queryResult;
	
	/**
	 * @param session
	 * @param string
	 * @param queryParameters 
	 */
	public HQLQueryPage(ConsoleConfiguration cfg, String string, QueryInputModel model) {
		super(cfg, model);
		queryString = string;
		setTabName(getQueryString().replace('\n', ' ').replace('\r', ' '));
	}
	
	public List<Object> getList() {
		if (queryString==null) return Collections.emptyList();
		if (list == null) {
			updateQueryResults();
		}
		return list;
	}
	
	private void updateQueryResults(){
		try {
			list = new ArrayList<Object>();
			queryResult = getConsoleConfiguration().getHibernateExtension()
					.executeHQLQuery(queryString, model);
			if (!queryResult.hasExceptions()){
				Iterator<?> iter = queryResult.list().iterator(); // need to be user-controllable to toggle between iterate, scroll etc.
				queryTime = queryResult.getQueryTime();
				while (iter.hasNext() ) {
					Object element = iter.next();
					list.add(element);
				}
				pcs.firePropertyChange("list", null, list); //$NON-NLS-1$
			} else {
				for (Throwable e : queryResult.getExceptions()) {
					addException(e);
				}
			}
		} catch (HibernateException e) {
			addException(e);				                
		} catch (IllegalArgumentException e) {
			addException(e);
		}
	}
	
    public List<String> getPathNames() {
    	try {
	    	if (queryResult == null){
	    		updateQueryResults();
	    	}
    	} catch (HibernateException e) {
			addException(e);				                
		}
    	
    	return queryResult == null ? Collections.<String>emptyList() : queryResult.getPathNames();
    }
		
	public void setSession(Session s) {
		//do nothing - remove this method
	}
	
	@Override
	public void release() {
		//session is not set, nothing to release
	}

    public String getQueryString() {
    	return queryString;
    }

	public void setQueryString(String queryString) {
		this.queryString = queryString;
		list = null;
		queryResult = null;
	}

}