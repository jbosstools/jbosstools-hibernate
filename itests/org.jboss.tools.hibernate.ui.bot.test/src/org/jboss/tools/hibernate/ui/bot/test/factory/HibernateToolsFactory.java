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
package org.jboss.tools.hibernate.ui.bot.test.factory;


import org.jboss.reddeer.requirements.db.DatabaseConfiguration;
import org.jboss.reddeer.swt.impl.button.OkButton;
import org.jboss.reddeer.swt.impl.combo.LabeledCombo;
import org.jboss.reddeer.workbench.impl.editor.DefaultEditor;
import org.jboss.tools.hibernate.reddeer.console.views.KnownConfigurationsView;
import org.jboss.tools.hibernate.reddeer.console.wizards.NewConfigurationFirstPage;
import org.jboss.tools.hibernate.reddeer.console.wizards.NewConfigurationWizard;
import org.jboss.tools.hibernate.reddeer.console.wizards.NewConfigurationWizardPage;

/**
 * Factory for common hibernate tools tasks
 * @author Jiri Peterka
 *
 */
public class HibernateToolsFactory {

	
	/**
	 * Create Hibernate Configuration file 
	 * @param cfg configuration
	 * @param project project name
	 * @param cfgFile hibernate configuration file
	 * @param generateConsole when true hibernate console configuration is generated
	 */
	public static void createConfigurationFile(DatabaseConfiguration cfg, String project, String cfgFile, boolean generateConsole) {		
		NewConfigurationWizard wizard = new NewConfigurationWizard();
		wizard.open();
		NewConfigurationFirstPage p1 = new NewConfigurationFirstPage();
		p1.setLocation(project,"src");		
		wizard.next();

		NewConfigurationWizardPage p2 = new NewConfigurationWizardPage();
		p2.setDatabaseDialect("H2");
		p2.setDriverClass(cfg.getDriverClass());
		p2.setConnectionURL(cfg.getJdbcString());
		p2.setUsername(cfg.getUsername());		
		
		if (generateConsole) {
			p2.setCreateConsoleConfiguration(generateConsole);
		}
		
		wizard.finish();
		
		new DefaultEditor(cfgFile);
		
	}

	/**
	 * Sets hibernate version to given console
	 * @param consoleName console name
	 * @param hibernateVersion hibernate version 
	 */
	public static void setHibernateVersion(String consoleName, String hbVersion) {
		KnownConfigurationsView v = new KnownConfigurationsView();
		v.open();
		v.openConsoleConfiguration(consoleName);
		new LabeledCombo("Hibernate Version:").setSelection(hbVersion);
		new OkButton().click();	
	}
}
