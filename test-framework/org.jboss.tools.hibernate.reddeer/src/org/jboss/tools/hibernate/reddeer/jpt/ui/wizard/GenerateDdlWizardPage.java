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
package org.jboss.tools.hibernate.reddeer.jpt.ui.wizard;

import java.util.List;

import org.jboss.reddeer.jface.wizard.WizardPage;
import org.jboss.reddeer.swt.impl.button.CheckBox;
import org.jboss.reddeer.swt.impl.combo.LabeledCombo;
import org.jboss.reddeer.swt.impl.text.LabeledText;

/**
 * Generate Tables from Entities Page
 * @author Jiri Peterka
 *
 */
public class GenerateDdlWizardPage extends WizardPage {

	/**
	 * Sets output directory for ddl
	 */
	public void setOutputDirectory(String dir) {
		new LabeledText("Output directory:").setText(dir);
	}

	/**
	 * Sets ddl file name
	 */
	public void setFileName(String fileName) {
		new LabeledText("File name").setText(fileName);
	}

	/**
	 * Sets whether to use console configuration or not for ddl generation 
	 * @param useConsole if set to true hibernate console configuration will be used
	 */
	public void setUseConsoleConfiguration(boolean useConsole) {
		CheckBox cbUseConsole = new CheckBox("Use Console Configuration");
		if (cbUseConsole.isEnabled() != useConsole) {
			cbUseConsole.click();
		}
	}
	
	/**
	 * Set Hibernate Version for table/ddl generation
	 * @param hbVersion hibernate version 
	 */
	public void setHibernateVersion(String hbVersion) {
		LabeledCombo lc = new LabeledCombo("Hibernate Version:");
		lc.setSelection(hbVersion);
	}
	
	public void setConsoleConfiguration(String configuration){
		new LabeledCombo("Console configuration:").setSelection(configuration);
	}
	
	public String getConsoleConfiguration(){
		return new LabeledCombo("Console configuration:").getSelection();
	}
	
	public List<String> getConsoleConfigurations(){
		return new LabeledCombo("Console configuration:").getItems();
	}
	
	public boolean isHibernateVersionEnabled(){
		return new LabeledCombo("Hibernate Version:").isEnabled();
	}
	
	public boolean isConsoleConfigurationEnabled(){
		return new LabeledCombo("Console configuration:").isEnabled();
	}
}
