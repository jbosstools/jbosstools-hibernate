/*******************************************************************************
  * Copyright (c) 2008 Red Hat, Inc.
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
		static final String CONFIG_FILE_PROPERTY = "confFileProperty"; //$NON-NLS-1$
		// Hibernate key string
		static final String HIBERNATE_CONFIG_FILE = "hibernate.ejb.cfgfile"; //$NON-NLS-1$
		static final String DEFAULT_CONFIG_FILE = ""; //$NON-NLS-1$
	
	String getDefaultDialect();
	String getDialect();
	void setDialect(String newDialect);
		static final String DIALECT_PROPERTY = "dialectProperty"; //$NON-NLS-1$
		// Hibernate key string
		static final String HIBERNATE_DIALECT = "hibernate.dialect"; //$NON-NLS-1$
		static final String DEFAULT_DIALECT = ""; //$NON-NLS-1$
		
	String getDefaultDriver();
	String getDriver();
	void setDriver(String newDriver);
		static final String DRIVER_PROPERTY = "driverProperty"; //$NON-NLS-1$
		// Hibernate key string
		static final String HIBERNATE_DRIVER = "hibernate.connection.driver_class"; //$NON-NLS-1$
		static final String DEFAULT_DRIVER = ""; //$NON-NLS-1$
		
	String getDefaultUrl();
	String getUrl();
	void setUrl(String newUrl);
		static final String URL_PROPERTY = "urlProperty"; //$NON-NLS-1$
		// Hibernate key string
		static final String HIBERNATE_URL = "hibernate.connection.url"; //$NON-NLS-1$
		static final String DEFAULT_URL = ""; //$NON-NLS-1$
		
	String getDefaultSchemaDefault();
	String getSchemaDefault();
	void setSchemaDefault(String newSchemaDefault);
		static final String SCHEMA_DEFAULT_PROPERTY = "schemaDefaultProperty"; //$NON-NLS-1$
		// Hibernate key string
		static final String HIBERNATE_SCHEMA_DEFAULT = "hibernate.default_schema"; //$NON-NLS-1$
		static final String DEFAULT_SCHEMA_DEFAULT = ""; //$NON-NLS-1$
		
	String getDefaultCatalogDefault();
	String getCatalogDefault();
	void setCatalogDefault(String newCatalogDefault);
		static final String CATALOG_DEFAULT_PROPERTY = "catalogDefaultProperty"; //$NON-NLS-1$
		// Hibernate key string
		static final String HIBERNATE_CATALOG = "hibernate.default_catalog"; //$NON-NLS-1$
		static final String DEFAULT_CATALOG_DEFAULT = ""; //$NON-NLS-1$
		
	String getDefaultUsername();
	String getUsername();
	void setUsername(String newUsername);
		static final String USERNAME_PROPERTY = "usernameProperty"; //$NON-NLS-1$
		// Hibernate key string
		static final String HIBERNATE_USERNAME = "hibernate.connection.username"; //$NON-NLS-1$
		static final String DEFAULT_USERNAME = ""; //$NON-NLS-1$
		
	String getDefaultPassword();
	String getPassword();
	void setPassword(String newPassword);
		static final String PASSWORD_PROPERTY = "passwordProperty"; //$NON-NLS-1$
		// Hibernate key string
		static final String HIBERNATE_PASSWORD = "hibernate.connection.password"; //$NON-NLS-1$
		static final String DEFAULT_PASSWORD = ""; //$NON-NLS-1$

}
