/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.reddeer.console;

/**
 * Hibernate Configuration (part of Hibernate console configurations view)
 * @author jpeterka
 *
 */
public class HibernateConfiguration {

	private String project;
	private String name;
	private String databaseConnection;
	private String configurationFile;
	private String persistenceUnit;

	/**
	 * Database connection options
	 */
	public class DatabaseConnection {
		public static final String hibernateConfiguredConection = "[Hibernate configured connection]";
		public static final String jpaProjectConfiguredConnetion = "[JPA Project Configured Connection]";
	}

	/**
	 * Gets configuration name
	 * @return configuration name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets configuration name
	 * @param name given configuration name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets database connection name
	 * @return db connection name
	 */
	public String getDatabaseConnection() {
		return databaseConnection;
	}

	/**
	 * Sets database connection name
	 * @param databaseConnection
	 */
	public void setDatabaseConnection(String databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	/**
	 * Get hibernate configuration file name
	 * @return hibernate configuration file name 
	 */
	public String getConfigurationFile() {
		return configurationFile;
	}

	/**
	 * Sets hibernate configuration file name
	 * @param configurationFile given hibernate configuration file name
	 */
	public void setConfigurationFile(String configurationFile) {
		this.configurationFile = configurationFile;
	}

	/**
	 * Get persistence unit for hibernate configuration
	 * @return persistence unit for hibernate configuration
	 */
	public String getPersistenceUnit() {
		return persistenceUnit;
	}

	/**
	 * Sets persistence unit for hibernate configuration
	 * @param persistenceUnit given persistence unit
	 */
	public void setPersistenceUnit(String persistenceUnit) {
		this.persistenceUnit = persistenceUnit;
	}

	/**
	 * Gets project for hibernate configuration
	 * @return given project for hibernate configuration
	 */
	public String getProject() {
		return project;
	}

	/**
	 * Sets project for hibernate configuration
	 * @param project given project for hibernate configuration
	 */
	public void setProject(String project) {
		this.project = project;
	}

}
