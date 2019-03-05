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
package org.jboss.tools.hibernate.reddeer.dialog;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.core.lookup.ShellLookup;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.CheckBox;
import org.eclipse.reddeer.swt.impl.button.OkButton;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import org.eclipse.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.eclipse.reddeer.swt.impl.menu.ShellMenuItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.table.DefaultTable;
import org.eclipse.reddeer.swt.impl.table.DefaultTableItem;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.hibernate.reddeer.perspective.HibernatePerspective;

/**
 * Launch configurations dialog for Hibernate generation configurations
 *
 * @author Jiri Peterka
 */
public class LaunchConfigurationsDialog extends DefaultShell{

	public static final String DIALOG_TITLE = "Hibernate Code Generation Configurations";

	/**
	 * Opens Hibernate code generation configuration dialog
	 */
	public void open() {		
		HibernatePerspective p = new HibernatePerspective();
    	p.open();
    	
    	new ShellMenuItem("Run", "Hibernate Code Generation...","Hibernate Code Generation Configurations...").select();
    	swtWidget = new DefaultShell(DIALOG_TITLE).getSWTWidget();
	}

	/**
	 * Creates new hibernate launch configuration
	 */
	public void createNewConfiguration() {
		new DefaultTreeItem(new DefaultTree(this),"Hibernate Code Generation").select();
		new DefaultToolItem(this, "New launch configuration").click();
	}
	
	public void selectHibernateCodeGeneration(String genName){
		new DefaultTreeItem(new DefaultTree(this), "Hibernate Code Generation", genName).select();
	}
		
	/**
	 * Select configuration name
	 * @param confName configuration name
	 */
	public void selectConfiguration(String confName) {
		new LabeledCombo(this, "Console configuration:").setSelection(confName);
	}
	
	/**
	 * Sets output dir for generated files
	 * @param dir given dir
	 */
	public void setOutputDir(String dir) {
		new LabeledText(this, "Output directory:").setText(dir);
	}

	/**
	 * Set if JDB connection should be used
	 * @param enable if true JDBC will be used
	 */
	public void setReverseFromJDBC(boolean enable) {
		boolean state = new CheckBox(this, "Reverse engineer from JDBC Connection").isChecked();
		if (state != enable) new CheckBox(this, "Reverse engineer from JDBC Connection").click();
	}
		
	/**
	 * Sets package for generated entities
	 * @param pkg package name
	 */
	public void setPackage(String pkg) {
		new LabeledText(this, "Package:").setText(pkg);
	}

	/**
	 * Select exporter which should be selected
	 * @param index index of the exporter
	 */
	public void selectExporter(int index) {
		new DefaultCTabItem(this, "Exporters").activate();
		new DefaultTableItem(new DefaultTable(this), index).setChecked(true);
	}
	
	/**
	 * Executes configuration
	 */
	public void run() {
		new PushButton(this, "Run").click();
    	new WaitWhile(new ShellIsAvailable(this));
    	new WaitUntil(new JobIsRunning());
    	new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
	}

	/**
	 * Select reveng file
	 * @param path path to existing reveng file
	 */
	public void setRevengFile(String... path) {
		new PushButton(this, "Setup...").click();
		org.eclipse.reddeer.swt.api.Shell setupShell = new DefaultShell("Setup reverse engineering");
		new PushButton(setupShell, "Use existing...").click();
		org.eclipse.reddeer.swt.api.Shell selectShell = new DefaultShell("Select reverse engineering settings file");
		new DefaultTreeItem(new DefaultTree(selectShell), path).select();
		new OkButton(selectShell).click();
		new WaitWhile(new ShellIsAvailable(selectShell));
		new WaitWhile(new ShellIsAvailable(setupShell));
	}

	
	/**
	 * Click apply
	 */
	public void apply() {
		new PushButton(this, "Apply").click();
		new WaitWhile(new JobIsRunning());
	}

	/**
	 * Presses Close button on the Dialog. 
	 */
	public void close() {
		new PushButton(this, "Close").click();
		new WaitWhile(new ShellIsAvailable(this)); 
		new WaitWhile(new JobIsRunning());
	}
	
	/**
	 * Checks if Workbench Preference dialog is opened.
	 * @return true if the dialog is opened, false otherwise
	 */
	public boolean isOpen() {
		Shell shell = ShellLookup.getInstance().getShell(DIALOG_TITLE,TimePeriod.SHORT);
		return (shell != null);		
	}
}
