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

import org.jboss.reddeer.swt.impl.button.CheckBox;
import org.jboss.reddeer.swt.impl.combo.LabeledCombo;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.text.LabeledText;

/**
 * Generate JPA Entities Wizard page implementation
 * @author jpeterka
 *
 */
public class GenerateEntitiesWizardPage  {

	/**
	 * Initalizae Generate Entities Wizard
	 */
	public GenerateEntitiesWizardPage() {
		new DefaultShell("Generate Entities");
	}
	
	/**
	 * Sets if to use console configuration or not
	 * @param useConsole if true use hibernate console
	 */
	public void setUseConsole(boolean useConsole) {
		CheckBox cbUseConsole = new CheckBox("Use Console Configuration");
		if (cbUseConsole.isEnabled() != useConsole) {
			cbUseConsole.click();
		}
	}
	
	/**
	 * Sets output package
	 * @param pkg given package location
	 */
	public void setPackage(String pkg) {
		new LabeledText("Package:").setText(pkg);
	}
	
	/**
	 * Sets hibernate version
	 * @param version given hibernte version
	 */
	public void setHibernateVersion(String version) {
		new LabeledCombo("Hibernate Version:").setSelection(version);
	}
	
	/**
	 * Sets database connection profile
	 * @param profileName given profile name
	 */
	public void setDatabaseConnection(String profileName) {
		new LabeledCombo("Database Connection").setSelection(profileName);
	}
		
}
