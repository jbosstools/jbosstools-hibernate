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

import org.jboss.reddeer.swt.impl.button.RadioButton;
import org.jboss.reddeer.swt.impl.combo.DefaultCombo;
import org.jboss.reddeer.swt.impl.combo.LabeledCombo;
import org.jboss.reddeer.swt.impl.group.DefaultGroup;
import org.jboss.reddeer.swt.impl.text.DefaultText;

/**
 * Hibernate Console Configuration Main Page
 * @author jpeterka
 *
 */
public class EditConfigurationMainPage {

	public interface PredefinedConnection {
	    String HIBERNATE_CONFIGURED_CONNECTION = "[Hibernate configured connection]";
	    String JPA_PROJECT_CONFIGURED_CONNECTION = "[JPA Project Configured Connection]";
	}
	
	/**
	 * Sets project for Hibernate Console Configuration 
	 * @param project given project name
	 */
	public void setProject(String project) {
		DefaultGroup g = new DefaultGroup("Project:");
		new DefaultText(g,0).setText(project);
	}
	
	/**
	 * Sets Hibernate version for Hibernate Console Configuration
	 * @param version given version, acceptable values are "3.5","3.6" and "4.0"
	 */
	public void setHibernateVersion(String version) {
		new LabeledCombo("Hibernate Version:").setSelection(version);
	}
	
	/**
	 * Sets database connection for Hibernate Console Configuration
	 * @param connection given connection
	 */
	public void setDatabaseConnection(String connection) {
		DefaultGroup g = new DefaultGroup("Database connection:");
		new DefaultCombo(g,0).setText(connection);
	}
	
	public void setType(String type){
		DefaultGroup g = new DefaultGroup("Type:");
		new RadioButton(g,type).click();
	}
	
	/**
	 * Set configuration file for Hibernate Console Configuration
	 * @param file given file path
	 */
	public void setConfigurationFile(String file) {
		DefaultGroup g = new DefaultGroup("Configuration file:");
		new DefaultText(g,0).setText(file);		
	}	
}
