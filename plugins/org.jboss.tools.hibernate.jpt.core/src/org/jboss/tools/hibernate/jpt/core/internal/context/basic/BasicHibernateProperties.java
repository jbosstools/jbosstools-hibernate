/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.basic;

import org.eclipse.jpt.utility.model.Model;
import org.eclipse.jpt.utility.model.listener.PropertyChangeListener;

/**
 * @author Dmitry Geraskov
 *
 */
public interface BasicHibernateProperties extends Model, PropertyChangeListener {
	
	String getDefaultConfigurationFile();
	String getConfigurationFile();
	void setConfigurationFile(String newConfigFile);
		static final String CONFIG_FILE_PROPERTY = "confFileProperty";
		// Hibernate key string
		static final String HIBERNATE_CONFIG_FILE = "hibernate.ejb.cfgfile";
		static final String DEFAULT_CONFIG_FILE = "";
	
	String getDefaultDialect();
	String getDialect();
	void setDialect(String newDialect);
		static final String DIALECT_PROPERTY = "dialectProperty";
		// Hibernate key string
		static final String HIBERNATE_DIALECT = "hibernate.dialect";
		static final String DEFAULT_DIALECT = "";
		
	String getDefaultDriver();
	String getDriver();
	void setDriver(String newDriver);
		static final String DRIVER_PROPERTY = "driverProperty";
		// Hibernate key string
		static final String HIBERNATE_DRIVER = "hibernate.connection.driver_class";
		static final String DEFAULT_DRIVER = "";
		
	String getDefaultUrl();
	String getUrl();
	void setUrl(String newUrl);
		static final String URL_PROPERTY = "urlProperty";
		// Hibernate key string
		static final String HIBERNATE_URL = "hibernate.connection.url";
		static final String DEFAULT_URL = "";
		
	String getDefaultSchemaDefault();
	String getSchemaDefault();
	void setSchemaDefault(String newSchemaDefault);
		static final String SCHEMA_DEFAULT_PROPERTY = "schemaDefaultProperty";
		// Hibernate key string
		static final String HIBERNATE_SCHEMA_DEFAULT = "hibernate.default_schema";
		static final String DEFAULT_SCHEMA_DEFAULT = "";
		
	String getDefaultCatalogDefault();
	String getCatalogDefault();
	void setCatalogDefault(String newCatalogDefault);
		static final String CATALOG_DEFAULT_PROPERTY = "catalogDefaultProperty";
		// Hibernate key string
		static final String HIBERNATE_CATALOG = "hibernate.default_catalog";
		static final String DEFAULT_CATALOG_DEFAULT = "";
		
	String getDefaultUsername();
	String getUsername();
	void setUsername(String newUsername);
		static final String USERNAME_PROPERTY = "usernameProperty";
		// Hibernate key string
		static final String HIBERNATE_USERNAME = "hibernate.connection.username";
		static final String DEFAULT_USERNAME = "";
		
	String getDefaultPassword();
	String getPassword();
	void setPassword(String newPassword);
		static final String PASSWORD_PROPERTY = "passwordProperty";
		// Hibernate key string
		static final String HIBERNATE_PASSWORD = "hibernate.connection.password";
		static final String DEFAULT_PASSWORD = "";

}
