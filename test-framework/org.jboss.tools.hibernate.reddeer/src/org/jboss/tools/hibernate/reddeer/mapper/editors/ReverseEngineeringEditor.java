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
package org.jboss.tools.hibernate.reddeer.mapper.editors;

import org.jboss.reddeer.swt.api.Shell;
import org.jboss.reddeer.swt.condition.ShellIsAvailable;
import org.jboss.reddeer.swt.condition.TreeContainsItem;
import org.jboss.reddeer.swt.condition.TreeHasChildren;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.tree.DefaultTree;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.workbench.impl.editor.DefaultEditor;

/**
 * Reverse engineer editor RedDeer implementation
 * @author jpeterka
 *
 */
public class ReverseEngineeringEditor extends DefaultEditor {
	
	public ReverseEngineeringEditor() {
		super("Hibernate Reverse Engineering Editor");
	}

	/**
	 * Activates editor's Overview tab
	 */
	public void activateOverviewTab() {
		new DefaultCTabItem("Overview").activate();
	}

	/**
	 * Activates editor's Type Mappings tab
	 */
	public void activateTypeMappingsTab() {
		new DefaultCTabItem("Type Mappings").activate();
	}

	/**
	 * Activates editor's Type Filters tab
	 */
	public void activateTableFiltersTab() {
		new DefaultCTabItem("Table Filters").activate();
	}

	/**
	 * Activates editor's Table and Columns tab
	 */	
	public void  activateTableAndColumnsTab() {
		new DefaultCTabItem("Table  Columns").activate();
	}
	
	/**
	 * Activates editor's Design tab
	 */
	public void activateDesignTab() {
		new DefaultCTabItem("Design").activate();
	}
	
	/**
	 * Activates editor's Source tab
	 */
	public void activateSourceTab() {
		new DefaultCTabItem("Source").activate();
	}

	/**
	 * Select all tables within Add Tables & Columns tab
	 */
	public void selectAllTables(String databaseName) {
		activateTableAndColumnsTab();
		new PushButton("Add...").click();
		Shell s= new DefaultShell("Add Tables & Columns");
		DefaultTree dbTree = new DefaultTree();
		new WaitUntil(new TreeHasChildren(dbTree));
		new WaitUntil(new TreeContainsItem(dbTree, databaseName));
		new DefaultTreeItem(databaseName).select();
		new PushButton("Select all children").click();
		new PushButton("OK").click();
		new WaitWhile(new ShellIsAvailable(s));
	}

	
}
