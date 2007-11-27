/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.wizard.hibernateconnection;




import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author sushko
 * interface for support of the database connection
 * 
 */

/**
 * interface for support of the database connection
 */
public interface ITestConnection {
	public Connection TestConnection(String driverName, String url,String username,String password) throws ClassNotFoundException ,SQLException;
	public Connection TestConnection( String driverName,String url) throws ClassNotFoundException ,SQLException;
}
