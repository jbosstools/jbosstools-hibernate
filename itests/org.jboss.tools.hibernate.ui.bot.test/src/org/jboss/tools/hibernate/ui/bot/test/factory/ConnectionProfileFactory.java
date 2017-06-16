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

import org.jboss.reddeer.eclipse.datatools.ui.DatabaseProfile;
import org.jboss.reddeer.eclipse.datatools.ui.view.DataSourceExplorer;
import org.jboss.reddeer.eclipse.datatools.ui.wizard.ConnectionProfileWizard;
import org.jboss.reddeer.requirements.db.DatabaseConfiguration;
import org.jboss.reddeer.swt.api.TreeItem;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.swt.impl.button.YesButton;
import org.jboss.reddeer.swt.impl.menu.ContextMenu;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.core.matcher.TreeItemRegexMatcher;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitWhile;

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
		
		DataSourceExplorer dse = new DataSourceExplorer();
		dse.open();
		
		//TODO implement this in explorer
		//TODO fix explorer name
		DefaultTreeItem item = new DefaultTreeItem("Database Connections");
		item.expand(TimePeriod.NORMAL);
		List<TreeItem> items = item.getItems();
		for (TreeItem i : items) {
			i.select();
			new ContextMenu("Delete").select();
			new DefaultShell("Delete confirmation");
			new YesButton().click();
			new WaitWhile(new ShellWithTextIsAvailable("Delete confirmation"));				
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
		ConnectionProfileWizard cpw = new ConnectionProfileWizard();
		cpw.open();
		cpw.createDatabaseProfile(dbProfile);
	}
	
	/**
	 * Deletes connection profile 
	 * @param profileName profile name to delete
	 */
	@SuppressWarnings("unchecked")
	public static void deleteConnectionProfile(String profileName) {
		DataSourceExplorer explorer = new DataSourceExplorer();
		explorer.open();
		new DefaultTreeItem(new TreeItemRegexMatcher("Database Connections"), new TreeItemRegexMatcher(profileName + ".*")).select();
		new ContextMenu("Delete").select();
		new DefaultShell("Delete confirmation");
		new YesButton().click();
		new WaitWhile(new ShellWithTextIsAvailable("Delete confirmation"));
	}
	
	/***
	 * Method deletes all connection profiles via Data Source Explorer
	 */
	public static void deleteAllConnectionProfiles() {
		DataSourceExplorer dse = new DataSourceExplorer();
		dse.open();
		List<TreeItem> items = new DefaultTreeItem("Database Connections").getItems();
		for (TreeItem i : items) {
			i.select();
			new ContextMenu("Delete").select();;
			new DefaultShell("Delete confirmation");
			new YesButton().click();
			new WaitWhile(new ShellWithTextIsAvailable("Delete confirmation"));		
		}
	}	
}