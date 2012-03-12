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
package org.jboss.tools.hibernate4_0;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.console.ConsoleMessages;
import org.hibernate.console.ConsoleQueryParameter;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.QueryInputModel;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.console.ext.HibernateException;
import org.hibernate.console.ext.HibernateExtension;
import org.hibernate.console.ext.QueryResult;
import org.hibernate.console.ext.QueryResultImpl;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.QLFormatHelper;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.type.Type;
import org.hibernate.util.xpl.StringHelper;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * @author Dmitry Geraskov
 *
 */
public class QueryHelper {
	
	public static QueryResult executeHQLQuery(HibernateExtension hibernateExtension, Session session, String hql,
			final QueryInputModel queryParameters) {

		final Query query = session.createQuery(hql);
		List<Object> list = Collections.emptyList();
		long queryTime = 0;

		list = new ArrayList<Object>();
		hibernateExtension.execute(new Command() {
			
			@Override
			public Object execute() {
				setupParameters(query, queryParameters);
				return null;
			}
		});
		
		long startTime = System.currentTimeMillis();
		QueryResultImpl result = new QueryResultImpl(list,
				queryTime);
		try {
			Iterator<?> iter = query.list().iterator(); // need to be user-controllable to toggle between iterate, scroll etc.
			queryTime = System.currentTimeMillis() - startTime;
			while (iter.hasNext() ) {
				Object element = iter.next();
				list.add(element);
			}
			result.setPathNames(getHQLPathNames(query)); 
		} catch (HibernateException e){
			result.addException(e);
		}
		return result;
		
	}
	
	public static QueryResult executeCriteriaQuery(Session session, String criteriaCode,
			QueryInputModel model) {
	
		try {
			List<Object> list = Collections.emptyList();
			long queryTime = 0;
			if (criteriaCode.indexOf("System.exit") >= 0) { // TODO: externalize run so we don't need this bogus check! //$NON-NLS-1$
				return new QueryResultImpl(new IllegalArgumentException(
						ConsoleMessages.JavaPage_not_allowed));
			} else {
				Interpreter ip = setupInterpreter(session);
				Object o = ip.eval(criteriaCode);
				// ugly! TODO: make un-ugly!
				if (o instanceof Criteria) {
					Criteria criteria = (Criteria) o;
					if (model.getMaxResults() != null) {
						criteria.setMaxResults(model.getMaxResults().intValue());
					}

	            	long startTime = System.currentTimeMillis();
	                list = criteria.list();
	                queryTime = System.currentTimeMillis() - startTime;
				} else if (o instanceof List<?>) {
					list = (List<Object>) o;
					if (model.getMaxResults() != null) {
						list = list.subList(0, Math.min(list.size(), model
								.getMaxResults().intValue()));
					}
				} else {
					list = new ArrayList<Object>();
					list.add(o);
				}
			}
			return new QueryResultImpl(list,
					Collections.singletonList(ConsoleMessages.JavaPage_no_info), queryTime);
		} catch (EvalError e) {
			return new QueryResultImpl(e);
		} catch (HibernateException e) {
			return new QueryResultImpl(e);
		}
	}
	
	private static List<String> getHQLPathNames(Query query) {
    	List<String> l = Collections.emptyList();
    
		if(query==null) return l;
		String[] returnAliases = null;
		try {
			returnAliases = query.getReturnAliases();
		} catch(NullPointerException e) {
			// ignore - http://opensource.atlassian.com/projects/hibernate/browse/HHH-2188
		}
		if(returnAliases==null) {
		Type[] t;
		try {
		t = query.getReturnTypes();
		} catch(NullPointerException npe) {
			t = new Type[] { null };
			// ignore - http://opensource.atlassian.com/projects/hibernate/browse/HHH-2188
		}
		l = new ArrayList<String>(t.length);

		for (int i = 0; i < t.length; i++) {
			Type type = t[i];
			if(type==null) {
			    l.add("<multiple types>");	 //$NON-NLS-1$
			} else {
				l.add(type.getName() );
			}
		}
		} else {
			String[] t = returnAliases;
    		l = new ArrayList<String>(t.length);
    
    		for (int i = 0; i < t.length; i++) {
    			l.add(t[i]);
    		}			
		}
    
    	return l;
    }
	
	private static void setupParameters(Query query2, QueryInputModel model) {
		if(model.getMaxResults()!=null) {
			query2.setMaxResults( model.getMaxResults().intValue() );
		}
		ConsoleQueryParameter[] qp = model.getQueryParameters();
		for (int i = 0; i < qp.length; i++) {
			ConsoleQueryParameter parameter = qp[i];

			String typeName = parameter.getType().getClass().getName();
			try {
				int pos = Integer.parseInt(parameter.getName());
				//FIXME no method to set positioned list value
				query2.setParameter(pos, calcValue( parameter ), convertToType(typeName));
			} catch(NumberFormatException nfe) {
				Object value = parameter.getValue();
				if (value != null && value.getClass().isArray()){
					Object[] values = (Object[])value;
					query2.setParameterList(parameter.getName(), Arrays.asList(values), convertToType(typeName));
				} else {
					query2.setParameter(parameter.getName(), calcValue( parameter ), convertToType(typeName));
				}
			}
		}		
	}
	
	/**
	 * Method converts Hibernate3 to Hibernate4 classes
	 * @param typeClassName
	 * @return
	 */
	private static Type convertToType(String typeClassName){
		try {
			return (Type) ReflectHelper.classForName(typeClassName).newInstance();
		} catch (Exception e) {
			throw new HibernateConsoleRuntimeException("Can't instantiate hibernate type " + typeClassName, e);
		}
	}
	
	private static Object calcValue(ConsoleQueryParameter parameter) {
		return parameter.getValueForQuery();				
	}
	
	
	private static Interpreter setupInterpreter(Session session) throws EvalError, HibernateException {
        Interpreter interpreter = new Interpreter();

        interpreter.set("session", session); //$NON-NLS-1$
        interpreter.setClassLoader( Thread.currentThread().getContextClassLoader() );
        SessionImplementor si = (SessionImplementor)session;

        Map<String, ?> map = si.getFactory().getAllClassMetadata();

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
	
	static String generateSQL(ExecutionContext executionContext, final SessionFactory sessionFactory, final String query) {

		if(StringHelper.isEmpty(query)) return ""; //$NON-NLS-1$

		String result = (String) executionContext.execute(new ExecutionContext.Command() {
			public Object execute() {
				try {
					SessionFactoryImpl sfimpl = (SessionFactoryImpl) sessionFactory; // hack - to get to the actual queries..
					StringBuffer str = new StringBuffer(256);
					HQLQueryPlan plan = new HQLQueryPlan(query, false, Collections.EMPTY_MAP, sfimpl);

					QueryTranslator[] translators = plan.getTranslators();
					for (int i = 0; i < translators.length; i++) {
						QueryTranslator translator = translators[i];
						if(translator.isManipulationStatement()) {
							str.append(HibernateConsoleMessages.DynamicSQLPreviewView_manipulation_of + i + ":"); //$NON-NLS-1$
							Iterator<?> iterator = translator.getQuerySpaces().iterator();
							while ( iterator.hasNext() ) {
								Object qspace = iterator.next();
								str.append(qspace);
								if(iterator.hasNext()) { str.append(", "); } //$NON-NLS-1$
							}

						} else {
							Type[] returnTypes = translator.getReturnTypes();
							str.append(i +": "); //$NON-NLS-1$
							for (int j = 0; j < returnTypes.length; j++) {
								Type returnType = returnTypes[j];
								str.append(returnType.getName());
								if(j<returnTypes.length-1) { str.append(", "); }							 //$NON-NLS-1$
							}
						}
						str.append("\n-----------------\n"); //$NON-NLS-1$
						Iterator<?> sqls = translator.collectSqlStrings().iterator();
						while ( sqls.hasNext() ) {
							String sql = (String) sqls.next();
							str.append(QLFormatHelper.formatForScreen(sql));
							str.append("\n\n");	 //$NON-NLS-1$
						}
					}
					return str.toString();
				} catch(Throwable t) {
					StringBuffer msgs = new StringBuffer();

					Throwable cause = t;
					while(cause!=null) {
						msgs.append(t);
						if(cause.getCause()==cause) {
							cause=null;
						} else {
							cause = cause.getCause();
							if(cause!=null) msgs.append(HibernateConsoleMessages.DynamicSQLPreviewView_caused_by);
						}
					}
					return msgs.toString();
				}

			}
		});

		return result;
	}


}
