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
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * @author sushko
 * Class factory for support of the database connection
 * 
 */


/**
 * Class factory for support of the database connection
 */
public class TestConnectionFactory implements ITestConnection{



/**
 * Method TestConnection of the TestConnectionFactory
 * @param  driverName
 * @param  url
 * @param  username
 * @param  password
 * @throws ClassNotFoundException 
 * @throws SQLException 
 * 
 */
public Connection TestConnection(String driverName, String url,String username,String password) throws ClassNotFoundException, SQLException 
{
		Connection con=null;
			Class.forName(driverName);
			
			con = DriverManager.getConnection(url, username, password);
//		con.close();
		return con;
};
public Connection TestConnection( String driverName,String url) throws ClassNotFoundException, SQLException 
{
		Connection con=null;
			Class.forName(driverName);
			
			con = DriverManager.getConnection(url);
//		con.close();
		return con;
};

}
