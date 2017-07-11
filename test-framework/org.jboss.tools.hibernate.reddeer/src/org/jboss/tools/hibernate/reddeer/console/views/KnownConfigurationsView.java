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
package org.jboss.tools.hibernate.reddeer.console.views;

import org.jboss.reddeer.swt.api.Shell;
import org.jboss.reddeer.swt.api.Tree;
import org.jboss.reddeer.swt.api.TreeItem;
import org.jboss.reddeer.swt.condition.ShellIsAvailable;
import org.jboss.reddeer.swt.impl.button.OkButton;
import org.jboss.reddeer.swt.impl.menu.ContextMenu;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.tree.DefaultTree;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;

import java.util.Arrays;
import java.util.List;

import org.jboss.reddeer.common.condition.AbstractWaitCondition;
import org.jboss.reddeer.common.exception.RedDeerException;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.workbench.impl.view.WorkbenchView;
import org.jboss.tools.hibernate.reddeer.condition.ConfigrationsAreLoaded;
import org.jboss.tools.hibernate.reddeer.console.EditConfigurationShell;

/** 
 * Hibernate configuration view implementation
 * @author jpeterka
 *
 */
public class KnownConfigurationsView extends WorkbenchView
{
	
	/**
	 * View implementation
	 */
	public KnownConfigurationsView() {
		super("Hibernate Configurations");
	}

	/**
	 * Add configuration
	 */
	public EditConfigurationShell addConfiguration() {
		open();
		new ContextMenu("Add Configuration...").select();
		return new EditConfigurationShell();
	}

	/**
	 * Selects console
	 * @param name given console name
	 */
	public void selectConsole(String name) {
		open();
		new DefaultTreeItem(name).select();
	}

	/**
	 * Open console configuration
	 * @param name given console name
	 * @return shell of the console
	 */
	public EditConfigurationShell openConsoleConfiguration(String name) {
		selectConsole(name);
		String title = "Edit Configuration";
		new ContextMenu(title).select();
		return new EditConfigurationShell();
	}

	/**
	 * Select tree under hibernate console configuration tree
	 * @param path given path starting with console name
	 */
	public void selectNode(String... path) {	
		for(int i=1; i< path.length; i++){
			String[] partialPath = Arrays.copyOf(path, i);
			new WaitUntil(new DatabaseTreeItemIsFound(partialPath));
		}
		
		new DefaultTreeItem(path).select();
	}
	
	
	/**
	 * Deletes hibernate console configuration
	 * @param console hibernate console configuration name
	 */
	public void deleteConsoleConfiguration(String console) {
		new DefaultTreeItem(console).select();
		new ContextMenu("Delete Configuration").select();
		Shell deleteShell = new DefaultShell("Delete console configuration");
		new OkButton().click();
		new WaitWhile(new ShellIsAvailable(deleteShell));
		new WaitWhile(new JobIsRunning());
	}
	
	public List<TreeItem> getConsoleConfigurations(){
		Tree tree = null;
		try {
			tree = new DefaultTree();
		} catch (RedDeerException e) {
			return null;
		}
		new WaitUntil(new ConfigrationsAreLoaded(tree));
		return tree.getItems();
	}
	
	private class DatabaseTreeItemIsFound extends AbstractWaitCondition {
		
		private String[] path;
		
		public DatabaseTreeItemIsFound(String... path) {
			this.path = path;
		}

		@Override
		public boolean test() {
			try{
				new DefaultTreeItem(path);
				return true;
			} catch (RedDeerException e) {
				return false;
			}
		}
		
		@Override
		public String description() {
			return "Looking for Database Item "+ path;
		}
		
	}
}
