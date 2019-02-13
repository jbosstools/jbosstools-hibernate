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
package org.hibernate.eclipse.console.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.console.AbstractQueryPage;
import org.hibernate.console.ConsoleMessages;
import org.hibernate.console.QueryInputModel;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.ISession;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * @author MAX
 *
 *
 */
public class JavaPage extends AbstractQueryPage {

    private String criteriaCode;

    ICriteria criteria = null;

    private Interpreter ip;

    /**
     * @param model
     * @param session2
     */
    public JavaPage(HibernateExtension extension, String criteriaCode, QueryInputModel model) {
		super(extension, model);
        this.criteriaCode =  criteriaCode;
		setTabName(getQueryString().replace('\n', ' ').replace('\r', ' '));
    }

    @SuppressWarnings("unchecked")
    @Override
	public void setSession(ISession s) {
		super.setSession(s);
        try {
        	if(criteriaCode.indexOf( "System.exit" )>=0) { // TODO: externalize run so we don't need this bogus check! //$NON-NLS-1$
        		list = Collections.emptyList();
        		addException( new IllegalArgumentException(ConsoleMessages.JavaPage_not_allowed) );
        		return;
        	}
            ip = setupInterpreter(getSession());
            Object o =  ip.eval(criteriaCode);
            // ugly! TODO: make un-ugly!
            if(o instanceof ICriteria) {
                criteria = (ICriteria) o;
                if(model.getMaxResults()!=null) {
                	criteria.setMaxResults( model.getMaxResults().intValue() );
                }
            } else if (o instanceof List<?>) {
                list = (List<Object>) o;
                if(model.getMaxResults()!=null) {
                	list = list.subList( 0, Math.min( list.size(), model.getMaxResults().intValue() ) );
                }
            } else {
                list = new ArrayList<Object>();
                list.add(o);
            }
        }
        catch (EvalError e) {
            addException(e);
        }
        catch (RuntimeException e) {
        	addException(e);
        }
	}

	private Interpreter setupInterpreter(ISession session) throws EvalError, RuntimeException {
        Interpreter interpreter = new Interpreter();

        interpreter.set("session", session); //$NON-NLS-1$
        interpreter.setClassLoader( Thread.currentThread().getContextClassLoader() );

        Map<String, ?> map = session.getSessionFactory().getAllClassMetadata();

        Iterator<String> iterator = map.keySet().iterator();
        //TODO: filter non classes.
        String imports = ""; //$NON-NLS-1$
        while (iterator.hasNext() ) {
            String element =  iterator.next();
            imports += "import " + element + ";\n"; //$NON-NLS-1$ //$NON-NLS-2$
        }

        imports += "import org.hibernate.criterion.*;\n"; //$NON-NLS-1$
        imports += "import org.hibernate.*;\n"; //$NON-NLS-1$
        // TODO: expose the parameters as values to be used in the code.
        interpreter.eval(imports);

        return interpreter;
    }

	public List<Object> getList() {
        if(list!=null) return list;
        try {
            if(criteria!=null) {
            	long startTime = System.currentTimeMillis();
                list = criteria.list();
                queryTime = System.currentTimeMillis() - startTime;
            }
            else {
                return Collections.emptyList();
            }
        }
        catch (RuntimeException e) {
        	list = Collections.emptyList();
            addException(e);
        }
        return list;
    }

	public List<String> getPathNames() {
        List<String> l = new ArrayList<String>();
        l.add(ConsoleMessages.JavaPage_no_info);
        return l;
    }

    public String getQueryString() {
        return criteriaCode;
    }
	public void setQueryString(String queryString) {
		this.criteriaCode = queryString;
		list = null;
		ip = null;
	}

	 public void release() {
    	if (((ISession)getSession()).isOpen() ) {
    		try {
    			((ISession)getSession()).close();
    		} 
    		catch (RuntimeException e) {
    			exceptions.add(e);
    		}
    	}    	
    }
}
