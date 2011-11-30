/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
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
 * @author Dmitry Geraskov
 *
 */
public class JavaPage extends AbstractQueryPage {

    private String criteriaCode;
    private QueryResult queryResult;

    /**
     * @param model
     * @param session2
     */
    public JavaPage(ConsoleConfiguration cfg, String criteriaCode, QueryInputModel model) {
		super(cfg, model);
        this.criteriaCode =  criteriaCode;
		setTabName(getQueryString().replace('\n', ' ').replace('\r', ' '));
    }

	public List<Object> getList() {
		if (criteriaCode==null) return Collections.emptyList();
		if (list == null) {
			updateQueryResults();
		}
		return list;
	}
	
	private void updateQueryResults(){
		try {
			list = new ArrayList<Object>();
			queryResult = getConsoleConfiguration().getHibernateExtension()
					.executeCriteriaQuery(criteriaCode, model);
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
	
	public void release() {
		//session is not set, nothing to release
    }

    public List<String> getPathNames() {
    	try {
    		return queryResult == null ? Collections.<String>emptyList() : queryResult.getPathNames();
    	} catch (HibernateException e) {
			addException(e);				                
		}
    	return null;
    }
    

	public void setSession(Session s) {
		//do nothing
	}

    public String getQueryString() {
        return criteriaCode;
    }

	@Override
	public void setQueryString(String criteriaCode) {
		this.criteriaCode = criteriaCode;
		list = null;
		queryResult = null;
	}

}
