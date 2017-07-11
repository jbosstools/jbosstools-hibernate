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

import org.eclipse.swt.widgets.Shell;
import org.jboss.reddeer.swt.condition.ShellIsAvailable;
import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.swt.impl.button.CheckBox;
import org.jboss.reddeer.swt.impl.button.OkButton;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.combo.LabeledCombo;
import org.jboss.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.jboss.reddeer.swt.impl.menu.ShellMenu;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.table.DefaultTableItem;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.core.lookup.ShellLookup;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.common.wait.WaitWhile;
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
    	
    	new ShellMenu("Run", "Hibernate Code Generation...","Hibernate Code Generation Configurations...").select();
    	swtShell = new DefaultShell(DIALOG_TITLE).getSWTWidget();
	}

	/**
	 * Creates new hibernate launch configuration
	 */
	public void createNewConfiguration() {
		new DefaultTreeItem("Hibernate Code Generation").select();
		new DefaultToolItem("New launch configuration").click();
	}
	
	public void selectHibernateCodeGeneration(String genName){
		new DefaultTreeItem("Hibernate Code Generation", genName).select();
	}
		
	/**
	 * Select configuration name
	 * @param confName configuration name
	 */
	public void selectConfiguration(String confName) {
		new LabeledCombo("Console configuration:").setSelection(confName);
	}
	
	/**
	 * Sets output dir for generated files
	 * @param dir given dir
	 */
	public void setOutputDir(String dir) {
		new LabeledText("Output directory:").setText(dir);
	}

	/**
	 * Set if JDB connection should be used
	 * @param enable if true JDBC will be used
	 */
	public void setReverseFromJDBC(boolean enable) {
		boolean state = new CheckBox("Reverse engineer from JDBC Connection").isChecked();
		if (state != enable) new CheckBox("Reverse engineer from JDBC Connection").click();
	}
		
	/**
	 * Sets package for generated entities
	 * @param pkg package name
	 */
	public void setPackage(String pkg) {
		new LabeledText("Package:").setText(pkg);
	}

	/**
	 * Select exporter which should be selected
	 * @param index index of the exporter
	 */
	public void selectExporter(int index) {
		new DefaultCTabItem("Exporters").activate();
		new DefaultTableItem(index).setChecked(true);
	}
	
	/**
	 * Executes configuration
	 */
	public void run() {
		new PushButton("Run").click();
    	new WaitWhile(new ShellWithTextIsAvailable("Hibernate Code Generation Configurations"));
    	new WaitUntil(new JobIsRunning());
    	new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
	}

	/**
	 * Select reveng file
	 * @param path path to existing reveng file
	 */
	public void setRevengFile(String... path) {
		new PushButton("Setup...").click();
		new WaitUntil(new ShellWithTextIsAvailable("Setup reverse engineering"));
		new DefaultShell("Setup reverse engineering");
		new PushButton("Use existing...").click();
		new WaitUntil(new ShellWithTextIsAvailable("Select reverse engineering settings file"));
		new DefaultTreeItem(path).select();
		new OkButton().click();
	}

	
	/**
	 * Click apply
	 */
	public void apply() {
		new PushButton("Apply").click();
		new WaitWhile(new JobIsRunning());
	}

	/**
	 * Presses Close button on the Dialog. 
	 */
	public void close() {
		new PushButton("Close").click();
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
