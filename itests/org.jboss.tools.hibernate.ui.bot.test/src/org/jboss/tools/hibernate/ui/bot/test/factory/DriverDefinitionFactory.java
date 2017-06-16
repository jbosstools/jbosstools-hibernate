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

import java.util.List;

import org.jboss.reddeer.eclipse.datatools.ui.DriverDefinition;
import org.jboss.reddeer.eclipse.datatools.ui.DriverTemplate;
import org.jboss.reddeer.eclipse.datatools.ui.preference.DriverDefinitionPreferencePage;
import org.jboss.reddeer.eclipse.datatools.ui.wizard.DriverDefinitionPage;
import org.jboss.reddeer.eclipse.datatools.ui.wizard.DriverDefinitionWizard;
import org.jboss.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.reddeer.requirements.db.DatabaseConfiguration;
import org.jboss.reddeer.swt.api.Shell;
import org.jboss.reddeer.swt.api.TableItem;
import org.jboss.reddeer.swt.condition.ShellIsAvailable;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.button.YesButton;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.table.DefaultTable;
import org.jboss.reddeer.swt.impl.table.DefaultTableItem;
import org.jboss.reddeer.common.wait.WaitWhile;

/**
 * Driver Definition Factory helps to create driver definition based on 
 * database configuration
 * 
 * @author Jiri Peterka
 *
 */
public class DriverDefinitionFactory {
	/**
	 * Creates Driver definition based on DatabaseRequirement configuration
	 * @param conf given database requirement configuration
	 */
	public static void createDatabaseDriverDefinition(DatabaseConfiguration cfg) {

		DriverTemplate dt = getDriverTemplate(cfg);
		DriverDefinition dd = getDriverDefinition(cfg);
		 
		// Driver Definition creation
		WorkbenchPreferenceDialog preferenceDialog = new WorkbenchPreferenceDialog();
		preferenceDialog.open();
		DriverDefinitionPreferencePage preferencePage = new DriverDefinitionPreferencePage();
		preferenceDialog.select(preferencePage);
		
		
		//TODO implement this in preference page
		//TODO dont create new driver def if it already exists
		List<TableItem> items = new DefaultTable().getItems();
		for (int i = 0; i < items.size(); i++) {
			new DefaultTableItem(0).select();
			new PushButton("Remove").click();
			Shell confirm = new DefaultShell("Confirm Driver Removal");
			new YesButton().click();
			new WaitWhile(new ShellIsAvailable(confirm));
			new DefaultShell("Preferences");
		}
		
		
		DriverDefinitionWizard ddw = preferencePage.addDriverDefinition();
		DriverDefinitionPage page = new DriverDefinitionPage();
		page.selectDriverTemplate(dt.getType(),dt.getVersion());
		page.setName(cfg.getDriverName());
		page.addDriverLibrary(dd.getDriverLibrary());
		page.setDriverClass(cfg.getDriverClass());

		ddw.finish();
		preferenceDialog.ok();
	}

	/**
	 * Returns Driver Template instance based on configuration
	 * @param cfg given configuration
	 * @return driver template
	 */
	public static DriverTemplate getDriverTemplate(DatabaseConfiguration cfg) {
		DriverTemplate dt = new DriverTemplate(cfg.getDriverType(),cfg.getDriverTypeVersion());          
		return dt;
	}

	/**
	 * Returns Driver Definition instance based on configuration
	 * @param cfg given configuration
	 * @return driver definition
	 */
	public static DriverDefinition getDriverDefinition(DatabaseConfiguration cfg) {
		// Driver definition      
		DriverDefinition dd = new DriverDefinition();
		dd.setDriverClass(cfg.getDriverClass());
		dd.setDriverLibrary(cfg.getDriverPath());
		dd.setDriverName(cfg.getDriverName());
		dd.setDriverTemplate(getDriverTemplate(cfg));

		return dd;
	}
}
