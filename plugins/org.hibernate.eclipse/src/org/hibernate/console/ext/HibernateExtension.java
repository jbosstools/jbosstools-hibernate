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

import org.hibernate.console.QueryInputModel;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;

/**
 * @author Dmitry Geraskov
 *
 */
public interface HibernateExtension {
	
	public void setConsoleConfigurationPreferences(ConsoleConfigurationPreferences prefs);
	
	public void build();
	
	public void buildMappings();
	
	public void buildSessionFactory();
	
	public boolean closeSessionFactory();
	
	public boolean reset();
	
	public String generateSQL(final String query);
	
	//TODO do we really need this?
	public String getHibernateVersion();
	
	public QueryResult executeHQLQuery(final String hql, final QueryInputModel queryParameters);
	
	public QueryResult executeCriteriaQuery(final String criteria, final QueryInputModel queryParameters);

	//FIXME remove this method
	public boolean hasConfiguration();
	/*
	//FIXME remove this method
	public Object getConfiguration();
	//FIXME remove this method
	public Object getSessionFactory();
	//FIXME remove this method
	*/

	public Object execute(Command command);

	public boolean isSessionFactoryCreated();
	
}
