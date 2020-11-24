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

import org.eclipse.reddeer.eclipse.datatools.connectivity.ui.dialogs.DriverDialog;
import org.eclipse.reddeer.eclipse.datatools.connectivity.ui.preferences.DriverPreferences;
import org.eclipse.reddeer.eclipse.datatools.ui.DriverDefinition;
import org.eclipse.reddeer.eclipse.datatools.ui.DriverTemplate;
import org.eclipse.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.eclipse.reddeer.requirements.db.DatabaseConfiguration;
import org.eclipse.reddeer.swt.api.Shell;
import org.eclipse.reddeer.swt.api.TableItem;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.button.YesButton;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.table.DefaultTable;
import org.eclipse.reddeer.swt.impl.table.DefaultTableItem;
import org.eclipse.reddeer.common.wait.WaitWhile;

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
		DriverPreferences preferencePage = new DriverPreferences(preferenceDialog);
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
		
		
		DriverDialog ddw = preferencePage.addDriverDefinition();
		ddw.selectDriverTemplate(dt.getType(),dt.getVersion());
		ddw.setName(cfg.getDriverName());
		ddw.addDriverLibrary(dd.getDriverLibrary());
		ddw.setDriverClass(cfg.getDriverClass());
		
		ddw.setFocus();
		ddw.ok();
		preferenceDialog.activate();
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
