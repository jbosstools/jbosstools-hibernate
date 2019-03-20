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
package org.jboss.tools.hibernate.reddeer.console;

import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.tab.DefaultTabItem;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.common.wait.WaitWhile;

/**
 * Hibernate Console Configuration shell
 * @author jpeterka
 *
 */
public class EditConfigurationShell extends DefaultShell {

	/**
	 * Hibernate Console Configuration shell
	 */
	public EditConfigurationShell() {
		super("Edit Configuration");
	}
	
	/**
	 * Returns Hibernate Console Configuration Main page
	 * @return main page
	 */
	public EditConfigurationMainPage getMainPage() {
		new DefaultCTabItem(this, "Main").activate();
		return new EditConfigurationMainPage();
	}

	/**
	 * Returns Hibernate Console Configuration Options page
	 * @return options page
	 */
	public EditConfigurationOptionsPage getOptionsPage() {
		new DefaultTabItem(this, "Options").activate();
		return new EditConfigurationOptionsPage();
	}
	
	/**
	 * Returns Hibernate Console Configuration Classpath page
	 * @return classpath page
	 */
	public EditorConfigurationClassPathPage getClassPathPage() {
		new DefaultTabItem(this, "ClassPath").activate();
		return new EditorConfigurationClassPathPage();
	}

	/**
	 * Returns Hibernate Console Configuration Common page
	 * @return classpath page
	 */	
	public EditConfigurationMappingsPage getCommonPage() {
		new DefaultTabItem(this, "Mappings").activate();
		return new EditConfigurationMappingsPage();
	}

	/**
	 * Click ok on Hibernate Configuration Console shell
	 */
	public void ok() {
		new PushButton(this, "OK").click();
		new WaitWhile(new ShellIsAvailable(this));
		new WaitWhile(new JobIsRunning());		
	}

	/**
	 * Sets name of Hibernate Console Configuration
	 * @param name given name
	 */
	public void setName(String name) {
		new LabeledText(this, "Name:").setText(name);		
	}	
}
