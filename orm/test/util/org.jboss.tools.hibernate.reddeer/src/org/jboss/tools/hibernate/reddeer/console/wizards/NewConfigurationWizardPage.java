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
package org.jboss.tools.hibernate.reddeer.console.wizards;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.api.Link;
import org.eclipse.reddeer.swt.impl.button.CheckBox;
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import org.eclipse.reddeer.swt.impl.link.DefaultLink;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

/**
 * New Hibernate Configuration Wizard page for Hibernate Configuration File
 * @author Jiri Peterka
 *
 */
public class NewConfigurationWizardPage extends WizardPage {
	
	public NewConfigurationWizardPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	private final String CONNECTION_URL = "Connection URL:";
	private final String DRIVER_CLASS = "Driver class:";
	private final String USERNAME = "Username:";
	private final String PASSWORD = "Password:";
	private final String HIBERNATE_VERSION = "Hibernate version:";
	
	
	/**
	 * Sets hibernate version for Hibernate Configuration file
	 * @param hibernateVersion hibernate version for Hibernate configuration
	 */
	public void setHibernateVersion(String hibernateVersion) {
		new LabeledCombo(referencedComposite, HIBERNATE_VERSION).setSelection(hibernateVersion);
	}
	
	/**
	 * Sets hibernate version for Hibernate Configuration file
	 * @return hibernate version string
	 */
	public String getHibernateVersion() {
		String version = new LabeledCombo(referencedComposite, HIBERNATE_VERSION).getSelection();
		return version;
	}
	
	public SelectConnectionProfileDialog getValuesFromConnection(){
		Link link = new DefaultLink(referencedComposite, "Get values from Connection");
		link.click();
		return new SelectConnectionProfileDialog();
	}
	
	
	/**
	 * Sets datbase dialect
	 * @param dialect given dialect
	 */
	public void setDatabaseDialect(String dialect) {
		new LabeledCombo(referencedComposite, "Database dialect:").setText(dialect);
	}
	
	/**
	 * Sets driver classs
	 * @param driverClass given driver class
	 */
	public void setDriverClass(String driverClass) {
		new LabeledCombo(referencedComposite, DRIVER_CLASS).setText(driverClass);
	}
	
	/**
	 * Sets connection URL
	 * @param url given connection url
	 */
	public void setConnectionURL(String url) {
		new LabeledCombo(referencedComposite, CONNECTION_URL).setText(url);
	}
	
	/**
	 * Sets username
	 * @param username given database username
	 */
	public void setUsername(String username) {
		new LabeledText(referencedComposite, USERNAME).setText(username);
	}
	
	/**
	 * Sets password 	
	 * @param username given connection password
	 */
	public void setPassword(String username) {
		new LabeledText(referencedComposite, PASSWORD).setText(username);
	}	
	
	/**
	 * Checks if console configuration should be created
	 * @param create if true configuration will be created
	 */
	public void setCreateConsoleConfiguration(boolean create) {
		CheckBox cb = new CheckBox(referencedComposite);
		boolean status = cb.isChecked();
		if (status != create) {
			cb.click();
		}
	}	
	
	/**
	 * Returns db driver class
	 * @return db driver class
	 */
	public String getDriveClass() {
		String driveClass = new LabeledCombo(referencedComposite, DRIVER_CLASS).getSelection();
		return driveClass;		
	}
	
	/**
	 * Returns db connection jdbc string
	 * @return db connection jdbc string
	 */
	public String getConnectionURL() {
		String url  = new LabeledCombo(referencedComposite, CONNECTION_URL).getText();
		return url;
	}
	
	/**
	 * Returns db username
	 * @return db username
	 */
	public String getUsername() {
		String username = new LabeledText(referencedComposite, USERNAME).getText();
		return username;
	}
	
	/**
	 * Returns db password
	 * @return db password
	 */
	public String getPassword() {
		String password = new LabeledText(referencedComposite, PASSWORD).getText();
		return password;
	}
}
