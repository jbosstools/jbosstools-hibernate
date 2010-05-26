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

import org.eclipse.jpt.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.core.context.persistence.PersistenceUnit.Property;
import org.eclipse.jpt.utility.model.event.PropertyChangeEvent;
import org.eclipse.jpt.utility.model.value.ListValueModel;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnitProperties;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateBasic extends HibernatePersistenceUnitProperties 
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
	public HibernateBasic(PersistenceUnit parent, ListValueModel<Property> propertyListAdapter) {
		super(parent, propertyListAdapter);
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
	

	// ********** behavior **********
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

	public void propertyChanged(PropertyChangeEvent event) {
		String aspectName = event.getAspectName();
		if (aspectName.equals(CONFIG_FILE_PROPERTY)) {
			this.configurationFileChanged(event);
		} else if (aspectName.equals(DIALECT_PROPERTY)) {
			this.dialectChanged(event);
		} else if (aspectName.equals(DRIVER_PROPERTY)) {
			this.driverChanged(event);
		} else if (aspectName.equals(URL_PROPERTY)) {
			this.urlChanged(event);
		} else if (aspectName.equals(SCHEMA_DEFAULT_PROPERTY)) {
			this.schemaDefaultChanged(event);
		} else if (aspectName.equals(CATALOG_DEFAULT_PROPERTY)) {
			this.catalogDefaultChanged(event);
		} else if (aspectName.equals(USERNAME_PROPERTY)) {
			this.usernameChanged(event);
		} else if (aspectName.equals(PASSWORD_PROPERTY)) {
			this.passwordChanged(event);
		}
	}
	
	// ********** Configuration File **********
	public String getDefaultConfigurationFile() {
		return DEFAULT_CONFIG_FILE;
	}
	
	public String getConfigurationFile() {
		return this.configFile;
	}
	
	public void setConfigurationFile(String newConfigFile) {
		if (!valueWasChanged(configFile, newConfigFile)) return;
		String old = this.configFile;
		this.configFile = newConfigFile;
		this.putProperty(CONFIG_FILE_PROPERTY, newConfigFile);
		this.firePropertyChanged(CONFIG_FILE_PROPERTY, old, newConfigFile);		
	}
	
	private void configurationFileChanged(PropertyChangeEvent event) {
		String newFile = (event.getNewValue() == null) ? null : ((Property) event.getNewValue()).getValue();
		String old = this.configFile;
		this.configFile = newFile;
		this.firePropertyChanged(event.getAspectName(), old, newFile);
	}
	
	// ********** Dialect **********
	public String getDefaultDialect() {
		return DEFAULT_DIALECT;
	}

	public String getDialect() {
		return this.dialect;
	}

	public void setDialect(String newDialect) {
		if (!valueWasChanged(dialect, newDialect)) return;
		String old = this.dialect;
		this.dialect = newDialect;
		this.putProperty(DIALECT_PROPERTY, newDialect);
		this.firePropertyChanged(DIALECT_PROPERTY, old, newDialect);
	}

	private void dialectChanged(PropertyChangeEvent event) {
		String newDialect = (event.getNewValue() == null) ? null : ((Property) event.getNewValue()).getValue();
		String old = this.dialect;
		this.dialect = newDialect;
		this.firePropertyChanged(event.getAspectName(), old, newDialect);
	}

	// ********** Driver **********
	public String getDefaultDriver() {
		return DEFAULT_DRIVER;
	}

	public String getDriver() {
		return this.driver;
	}	

	public void setDriver(String newDriver) {
		if (!valueWasChanged(driver, newDriver)) return;
		String old = this.driver;
		this.driver = newDriver;
		this.putProperty(DRIVER_PROPERTY, newDriver);
		this.firePropertyChanged(DRIVER_PROPERTY, old, newDriver);		
	}
	
	private void driverChanged(PropertyChangeEvent event) {
		String newDriver = (event.getNewValue() == null) ? null : ((Property) event.getNewValue()).getValue();
		String old = this.driver;
		this.driver = newDriver;
		this.firePropertyChanged(event.getAspectName(), old, newDriver);
	}
	
	// ********** Url **********
	public String getDefaultUrl() {
		return DEFAULT_URL;
	}

	public String getUrl() {
		return this.url;
	}	

	public void setUrl(String newUrl) {
		if (!valueWasChanged(url, newUrl)) return;
		String old = this.url;
		this.url = newUrl;
		this.putProperty(URL_PROPERTY, newUrl);
		this.firePropertyChanged(URL_PROPERTY, old, newUrl);	
	}
	
	private void urlChanged(PropertyChangeEvent event) {
		String newUrl = (event.getNewValue() == null) ? null : ((Property) event.getNewValue()).getValue();
		String old = this.url;
		this.url = newUrl;
		this.firePropertyChanged(event.getAspectName(), old, newUrl);
	}
	
	// ********** Default schema **********
	public String getDefaultSchemaDefault() {
		return DEFAULT_SCHEMA_DEFAULT;
	}
	
	public String getSchemaDefault() {
		return schemaDefault;
	}
	
	public void setSchemaDefault(String newSchemaDefault) {
		if (!valueWasChanged(schemaDefault, newSchemaDefault)) return;
		String old = this.schemaDefault;
		this.schemaDefault = newSchemaDefault;
		this.putProperty(SCHEMA_DEFAULT_PROPERTY, newSchemaDefault);
		this.firePropertyChanged(SCHEMA_DEFAULT_PROPERTY, old, newSchemaDefault);
	}
	
	private void schemaDefaultChanged(PropertyChangeEvent event) {
		String newSchemaDefault = (event.getNewValue() == null) ? null : ((Property) event.getNewValue()).getValue();
		String old = this.schemaDefault;
		this.schemaDefault = newSchemaDefault;
		this.firePropertyChanged(event.getAspectName(), old, newSchemaDefault);
	}
	
	// ********** Default catalog **********
	public String getDefaultCatalogDefault() {
		return DEFAULT_CATALOG_DEFAULT;
	}
	
	public String getCatalogDefault() {
		return catalogDefault;
	}
	
	public void setCatalogDefault(String newCatalogDefault) {
		if (!valueWasChanged(catalogDefault, newCatalogDefault)) return;
		String old = this.catalogDefault;
		this.catalogDefault = newCatalogDefault;
		this.putProperty(CATALOG_DEFAULT_PROPERTY, newCatalogDefault);
		this.firePropertyChanged(CATALOG_DEFAULT_PROPERTY, old, newCatalogDefault);	
	}
	
	private void catalogDefaultChanged(PropertyChangeEvent event) {
		String newCatalogDefault = (event.getNewValue() == null) ? null : ((Property) event.getNewValue()).getValue();
		String old = this.catalogDefault;
		this.catalogDefault = newCatalogDefault;
		this.firePropertyChanged(event.getAspectName(), old, newCatalogDefault);
	}
	
	// ********** Username **********
	public String getDefaultUsername() {
		return DEFAULT_USERNAME;
	}

	public String getUsername() {
		return this.username;
	}	

	public void setUsername(String newUsername) {
		if (!valueWasChanged(username, newUsername)) return;
		String old = this.username;
		this.username = newUsername;
		this.putProperty(USERNAME_PROPERTY, newUsername);
		this.firePropertyChanged(USERNAME_PROPERTY, old, newUsername);	
	}
	
	private void usernameChanged(PropertyChangeEvent event) {
		String newUsername = (event.getNewValue() == null) ? null : ((Property) event.getNewValue()).getValue();
		String old = this.username;
		this.username = newUsername;
		this.firePropertyChanged(event.getAspectName(), old, newUsername);
	}
	
	// ********** Password **********
	public String getDefaultPassword() {
		return DEFAULT_PASSWORD;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String newPassword) {
		if (!valueWasChanged(password, newPassword)) return;
		String old = this.password;
		this.password = newPassword;
		this.putProperty(PASSWORD_PROPERTY, newPassword);
		this.firePropertyChanged(PASSWORD_PROPERTY, old, newPassword);
	}
	
	private void passwordChanged(PropertyChangeEvent event) {
		String newPassword = (event.getNewValue() == null) ? null : ((Property) event.getNewValue()).getValue();
		String old = this.password;
		this.password = newPassword;
		this.firePropertyChanged(event.getAspectName(), old, newPassword);
	}

	public void updateProperties() {
		setConfigurationFile(this.getStringValue(HIBERNATE_CONFIG_FILE));
		setDialect(this.getStringValue(HIBERNATE_DIALECT));
		setDriver(this.getStringValue(HIBERNATE_DRIVER));
		setUrl(this.getStringValue(HIBERNATE_URL));
		setSchemaDefault(this.getStringValue(HIBERNATE_SCHEMA_DEFAULT));
		setCatalogDefault(this.getStringValue(HIBERNATE_CATALOG));
		setUsername(this.getStringValue(HIBERNATE_USERNAME));
		setPassword(this.getStringValue(HIBERNATE_PASSWORD));
	}
	
	private boolean valueWasChanged(String oldValue, String newValue){
		return oldValue == null ? newValue != null
				: !oldValue.equals(newValue);
	}

}
