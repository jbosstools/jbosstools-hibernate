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

import org.eclipse.reddeer.swt.api.Shell;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.condition.TreeContainsItem;
import org.eclipse.reddeer.swt.condition.TreeHasChildren;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.workbench.impl.editor.DefaultEditor;

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
		new DefaultCTabItem(this, "Overview").activate();
	}

	/**
	 * Activates editor's Type Mappings tab
	 */
	public void activateTypeMappingsTab() {
		new DefaultCTabItem(this, "Type Mappings").activate();
	}

	/**
	 * Activates editor's Type Filters tab
	 */
	public void activateTableFiltersTab() {
		new DefaultCTabItem(this, "Table Filters").activate();
	}

	/**
	 * Activates editor's Table and Columns tab
	 */	
	public void  activateTableAndColumnsTab() {
		new DefaultCTabItem(this, "Table  Columns").activate();
	}
	
	/**
	 * Activates editor's Design tab
	 */
	public void activateDesignTab() {
		new DefaultCTabItem(this, "Design").activate();
	}
	
	/**
	 * Activates editor's Source tab
	 */
	public void activateSourceTab() {
		new DefaultCTabItem(this, "Source").activate();
	}

	/**
	 * Select all tables within Add Tables & Columns tab
	 */
	public void selectAllTables(String databaseName) {
		activateTableAndColumnsTab();
		new PushButton(this, "Add...").click();
		Shell s= new DefaultShell("Add Tables & Columns");
		DefaultTree dbTree = new DefaultTree(s);
		new WaitUntil(new TreeHasChildren(dbTree));
		new WaitUntil(new TreeContainsItem(dbTree, databaseName));
		new DefaultTreeItem(dbTree, databaseName).select();
		new PushButton(s, "Select all children").click();
		new PushButton(s, "OK").click();
		new WaitWhile(new ShellIsAvailable(s));
	}

	
}
