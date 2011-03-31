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

import java.util.Map;

import org.eclipse.jpt.jpa.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.jpa.core.internal.context.persistence.AbstractPersistenceUnitProperties;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernatePersistenceUnitProperties extends AbstractPersistenceUnitProperties 
				implements BasicHibernateProperties {
	
	// ********** Hibernate properties **********
	private String configFile;
	private String dialect;
	private String driver;
	private String url;
	private String schemaDefault;
	private String catalogDefault;
	private String username;
	private String password;
	
	// ********** constructors **********
	public HibernatePersistenceUnitProperties(PersistenceUnit parent) {
		super(parent);
	}
	
	// ********** initialization **********
	/**
	 * Initializes properties with values from the persistence unit.
	 */
	@Override
	protected void initializeProperties() {
		this.configFile = 
			this.getStringValue(HIBERNATE_CONFIG_FILE);
		this.dialect = 
			this.getStringValue(HIBERNATE_DIALECT);
		this.driver = 
			this.getStringValue(HIBERNATE_DRIVER);
		this.url = 
			this.getStringValue(HIBERNATE_URL);
		this.schemaDefault = 
			this.getStringValue(HIBERNATE_SCHEMA_DEFAULT);
		this.catalogDefault = 
			this.getStringValue(HIBERNATE_CATALOG);
		this.username = 
			this.getStringValue(HIBERNATE_USERNAME);
		this.password = 
			this.getStringValue(HIBERNATE_PASSWORD);
	}
	
	public void propertyValueChanged(String propertyName, String newValue) {
		if (propertyName.equals(HIBERNATE_CONFIG_FILE)) {
			this.configurationFileChanged(newValue);
		} else if (propertyName.equals(HIBERNATE_DIALECT)) {
			this.dialectChanged(newValue);
		} else if (propertyName.equals(HIBERNATE_DRIVER)) {
			this.driverChanged(newValue);
		} else if (propertyName.equals(HIBERNATE_URL)) {
			this.urlChanged(newValue);
		} else if (propertyName.equals(HIBERNATE_SCHEMA_DEFAULT)) {
			this.schemaDefaultChanged(newValue);
		} else if (propertyName.equals(HIBERNATE_CATALOG)) {
			this.catalogDefaultChanged(newValue);
		} else if (propertyName.equals(HIBERNATE_USERNAME)) {
			this.usernameChanged(newValue);
		} else if (propertyName.equals(HIBERNATE_PASSWORD)) {
			this.passwordChanged(newValue);
		}
	}
	
	public void propertyRemoved(String propertyName) {
		if (propertyName.equals(HIBERNATE_CONFIG_FILE)) {
			this.configurationFileChanged(null);
		} else if (propertyName.equals(HIBERNATE_DIALECT)) {
			this.dialectChanged(null);
		} else if (propertyName.equals(HIBERNATE_DRIVER)) {
			this.driverChanged(null);
		} else if (propertyName.equals(HIBERNATE_URL)) {
			this.urlChanged(null);
		} else if (propertyName.equals(HIBERNATE_SCHEMA_DEFAULT)) {
			this.schemaDefaultChanged(null);
		} else if (propertyName.equals(HIBERNATE_CATALOG)) {
			this.catalogDefaultChanged(null);
		} else if (propertyName.equals(HIBERNATE_USERNAME)) {
			this.usernameChanged(null);
		} else if (propertyName.equals(HIBERNATE_PASSWORD)) {
			this.passwordChanged(null);
		}
	}
	
	@Override
	protected void addPropertyNames(Map<String, String> propertyNames) {
		propertyNames.put(
				HIBERNATE_CONFIG_FILE,
				CONFIG_FILE_PROPERTY);
		propertyNames.put(
				HIBERNATE_DIALECT,
				DIALECT_PROPERTY);		
		propertyNames.put(
				HIBERNATE_DRIVER,
				DRIVER_PROPERTY);
		propertyNames.put(
				HIBERNATE_URL,
				URL_PROPERTY);
		propertyNames.put(
				HIBERNATE_SCHEMA_DEFAULT,
				SCHEMA_DEFAULT_PROPERTY);
		propertyNames.put(
				HIBERNATE_CATALOG,
				CATALOG_DEFAULT_PROPERTY);		
		propertyNames.put(
				HIBERNATE_USERNAME,
				USERNAME_PROPERTY);		
		propertyNames.put(
				HIBERNATE_PASSWORD,
				PASSWORD_PROPERTY);
	}

	
	// ********** Configuration File **********
	public String getDefaultConfigurationFile() {
		return DEFAULT_CONFIG_FILE;
	}
	
	public String getConfigurationFile() {
		return this.configFile;
	}
	
	public void setConfigurationFile(String newConfigFile) {
		String old = this.configFile;
		this.configFile = newConfigFile;
		this.putProperty(CONFIG_FILE_PROPERTY, newConfigFile);
		this.firePropertyChanged(CONFIG_FILE_PROPERTY, old, newConfigFile);		
	}
	
	private void configurationFileChanged(String newFile) {
		String old = this.configFile;
		this.configFile = newFile;
		this.firePropertyChanged(CONFIG_FILE_PROPERTY, old, newFile);
	}
	
	// ********** Dialect **********
	public String getDefaultDialect() {
		return DEFAULT_DIALECT;
	}

	public String getDialect() {
		return this.dialect;
	}

	public void setDialect(String newDialect) {
		String old = this.dialect;
		this.dialect = newDialect;
		this.putProperty(DIALECT_PROPERTY, newDialect);
		this.firePropertyChanged(DIALECT_PROPERTY, old, newDialect);
	}

	private void dialectChanged(String newDialect) {
		String old = this.dialect;
		this.dialect = newDialect;
		this.firePropertyChanged(DIALECT_PROPERTY, old, newDialect);
	}

	// ********** Driver **********
	public String getDefaultDriver() {
		return DEFAULT_DRIVER;
	}

	public String getDriver() {
		return this.driver;
	}	

	public void setDriver(String newDriver) {
		String old = this.driver;
		this.driver = newDriver;
		this.putProperty(DRIVER_PROPERTY, newDriver);
		this.firePropertyChanged(DRIVER_PROPERTY, old, newDriver);		
	}
	
	private void driverChanged(String newDriver) {
		String old = this.driver;
		this.driver = newDriver;
		this.firePropertyChanged(DRIVER_PROPERTY, old, newDriver);
	}
	
	// ********** Url **********
	public String getDefaultUrl() {
		return DEFAULT_URL;
	}

	public String getUrl() {
		return this.url;
	}	

	public void setUrl(String newUrl) {
		String old = this.url;
		this.url = newUrl;
		this.putProperty(URL_PROPERTY, newUrl);
		this.firePropertyChanged(URL_PROPERTY, old, newUrl);	
	}
	
	private void urlChanged(String newUrl) {
		String old = this.url;
		this.url = newUrl;
		this.firePropertyChanged(URL_PROPERTY, old, newUrl);
	}
	
	// ********** Default schema **********
	public String getDefaultSchemaDefault() {
		return DEFAULT_SCHEMA_DEFAULT;
	}
	
	public String getSchemaDefault() {
		return schemaDefault;
	}
	
	public void setSchemaDefault(String newSchemaDefault) {
		String old = this.schemaDefault;
		this.schemaDefault = newSchemaDefault;
		this.putProperty(SCHEMA_DEFAULT_PROPERTY, newSchemaDefault);
		this.firePropertyChanged(SCHEMA_DEFAULT_PROPERTY, old, newSchemaDefault);
	}
	
	private void schemaDefaultChanged(String newSchemaDefault) {
		String old = this.schemaDefault;
		this.schemaDefault = newSchemaDefault;
		this.firePropertyChanged(SCHEMA_DEFAULT_PROPERTY, old, newSchemaDefault);
	}
	
	// ********** Default catalog **********
	public String getDefaultCatalogDefault() {
		return DEFAULT_CATALOG_DEFAULT;
	}
	
	public String getCatalogDefault() {
		return catalogDefault;
	}
	
	public void setCatalogDefault(String newCatalogDefault) {
		String old = this.catalogDefault;
		this.catalogDefault = newCatalogDefault;
		this.putProperty(CATALOG_DEFAULT_PROPERTY, newCatalogDefault);
		this.firePropertyChanged(CATALOG_DEFAULT_PROPERTY, old, newCatalogDefault);	
	}
	
	private void catalogDefaultChanged(String newCatalogDefault) {
		String old = this.catalogDefault;
		this.catalogDefault = newCatalogDefault;
		this.firePropertyChanged(CATALOG_DEFAULT_PROPERTY, old, newCatalogDefault);
	}
	
	// ********** Username **********
	public String getDefaultUsername() {
		return DEFAULT_USERNAME;
	}

	public String getUsername() {
		return this.username;
	}	

	public void setUsername(String newUsername) {
		String old = this.username;
		this.username = newUsername;
		this.putProperty(USERNAME_PROPERTY, newUsername);
		this.firePropertyChanged(USERNAME_PROPERTY, old, newUsername);	
	}
	
	private void usernameChanged(String newUsername) {
		String old = this.username;
		this.username = newUsername;
		this.firePropertyChanged(USERNAME_PROPERTY, old, newUsername);
	}
	
	// ********** Password **********
	public String getDefaultPassword() {
		return DEFAULT_PASSWORD;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String newPassword) {
		String old = this.password;
		this.password = newPassword;
		this.putProperty(PASSWORD_PROPERTY, newPassword);
		this.firePropertyChanged(PASSWORD_PROPERTY, old, newPassword);
	}
	
	private void passwordChanged(String newPassword) {
		String old = this.password;
		this.password = newPassword;
		this.firePropertyChanged(PASSWORD_PROPERTY, old, newPassword);
	}

}
