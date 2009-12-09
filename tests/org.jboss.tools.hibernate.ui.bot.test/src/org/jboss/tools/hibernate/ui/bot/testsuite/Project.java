 /*******************************************************************************
  * Copyright (c) 2007-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.ui.bot.testsuite;

/**
 * Hibernate Test Suite properties. Properties for all tests and test prerequisites
 * @author jpeterka
 *
 */
public class Project {
	public static final String CLASS1 = "Customer";
	public static final String CLASS2 = "Order";
	public static final String PACKAGE_NAME = "org.jboss.hibernate.test";	
	public static final String PROJECT_NAME = "HibernateTest";
	public static final String DB_DIALECT = "HSQL";
	public static final String DRIVER_CLASS = "org.hsqldb.jdbcDriver";
	public static final String JDBC_STRING = "jdbc:hsqldb:hsql://localhost/xdb";
	public static final String HSQLDB_PATH = "/home/jpeterka/lib/hsqldb";
	public static final String CONF_FILE_NAME2 = "hibernate2.cfg.xml";

}
