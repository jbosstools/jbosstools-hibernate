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
package org.jboss.tools.hibernate3_5;

import java.util.Iterator;

import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.QLFormatHelper;
import org.hibernate.util.xpl.StringHelper;
import org.jboss.tools.hibernate.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.spi.IQueryTranslator;
import org.jboss.tools.hibernate.spi.ISessionFactory;
import org.jboss.tools.hibernate.spi.IType;
import org.jboss.tools.hibernate.util.HibernateHelper;

/**
 * @author Dmitry Geraskov
 *
 */
public class QueryHelper {
	
	
	
	static String generateSQL(ExecutionContext executionContext, final ISessionFactory sessionFactory, final String query) {

		if(StringHelper.isEmpty(query)) return ""; //$NON-NLS-1$

		String result = (String) executionContext.execute(new ExecutionContext.Command() {
			public Object execute() {
				try {
					StringBuffer str = new StringBuffer(256);
					IHQLQueryPlan plan = HibernateHelper.INSTANCE.getHibernateService().newHQLQueryPlan(query, false, sessionFactory);
					IQueryTranslator[] translators = plan.getTranslators();
					for (int i = 0; i < translators.length; i++) {
						IQueryTranslator translator = translators[i];
						if(translator.isManipulationStatement()) {
							str.append(HibernateConsoleMessages.DynamicSQLPreviewView_manipulation_of + i + ":"); //$NON-NLS-1$
							Iterator<?> iterator = translator.getQuerySpaces().iterator();
							while ( iterator.hasNext() ) {
								Object qspace = iterator.next();
								str.append(qspace);
								if(iterator.hasNext()) { str.append(", "); } //$NON-NLS-1$
							}

						} else {
							IType[] returnTypes = translator.getReturnTypes();
							str.append(i +": "); //$NON-NLS-1$
							for (int j = 0; j < returnTypes.length; j++) {
								IType returnType = returnTypes[j];
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
