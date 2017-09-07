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

import org.eclipse.reddeer.requirements.db.DatabaseConfiguration;
import org.eclipse.reddeer.swt.api.Shell;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.YesButton;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.eclipse.reddeer.core.matcher.TreeItemRegexMatcher;
import org.eclipse.reddeer.eclipse.datatools.connectivity.ui.dse.views.DataSourceExplorerView;
import org.eclipse.reddeer.eclipse.datatools.connectivity.ui.wizards.NewCPWizard;
import org.eclipse.reddeer.eclipse.datatools.ui.DatabaseProfile;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;

/**
 * Driver Definition Factory helps to create driver definition based on 
 * database configuration
 * 
 * @author Jiri Peterka
 *
 */
public class ConnectionProfileFactory {
	/**
	 * Creates Connection profile based on DatabaseRequirement configuration
	 * @param conf given database requirement configuration
	 */
	public static void createConnectionProfile(DatabaseConfiguration cfg) {
		
		DataSourceExplorerView dse = new DataSourceExplorerView();
		dse.open();
		
		//TODO implement this in explorer
		//TODO fix explorer name
		DefaultTreeItem item = new DefaultTreeItem("Database Connections");
		item.expand(TimePeriod.DEFAULT);
		List<TreeItem> items = item.getItems();
		for (TreeItem i : items) {
			i.select();
			new ContextMenuItem("Delete").select();
			Shell delete = new DefaultShell("Delete confirmation");
			new YesButton(delete).click();
			new WaitWhile(new ShellIsAvailable(delete));				
		}

		DatabaseProfile dbProfile = new DatabaseProfile();
		dbProfile.setDatabase(cfg.getProfileName());
		dbProfile.setDriverDefinition(DriverDefinitionFactory.getDriverDefinition(cfg));
		dbProfile.setHostname(cfg.getJdbcString());
		dbProfile.setName(cfg.getProfileName());
		dbProfile.setPassword(cfg.getPassword());
		dbProfile.setUsername(cfg.getUsername());
		dbProfile.setVendor(cfg.getDriverVendor());

		// Driver Definition creation
		NewCPWizard cpw = new NewCPWizard();
		cpw.open();
		cpw.createDatabaseProfile(dbProfile);
	}
	
	/**
	 * Deletes connection profile 
	 * @param profileName profile name to delete
	 */
	@SuppressWarnings("unchecked")
	public static void deleteConnectionProfile(String profileName) {
		DataSourceExplorerView explorer = new DataSourceExplorerView();
		explorer.open();
		new DefaultTreeItem(new TreeItemRegexMatcher("Database Connections"), new TreeItemRegexMatcher(profileName + ".*")).select();
		new ContextMenuItem("Delete").select();
		Shell delete =new DefaultShell("Delete confirmation");
		new YesButton(delete).click();
		new WaitWhile(new ShellIsAvailable(delete));
	}
	
	/***
	 * Method deletes all connection profiles via Data Source Explorer
	 */
	public static void deleteAllConnectionProfiles() {
		DataSourceExplorerView dse = new DataSourceExplorerView();
		dse.open();
		List<TreeItem> items = new DefaultTreeItem("Database Connections").getItems();
		for (TreeItem i : items) {
			i.select();
			new ContextMenuItem("Delete").select();;
			Shell delete = new DefaultShell("Delete confirmation");
			new YesButton(delete).click();
			new WaitWhile(new ShellIsAvailable(delete));		
		}
	}	
}