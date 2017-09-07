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

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.impl.button.CheckBox;
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

/**
 * Generate JPA Entities Wizard page implementation
 * @author jpeterka
 *
 */
public class GenerateEntitiesWizardPage  extends WizardPage{

	
	
	public GenerateEntitiesWizardPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	/**
	 * Sets if to use console configuration or not
	 * @param useConsole if true use hibernate console
	 */
	public void setUseConsole(boolean useConsole) {
		CheckBox cbUseConsole = new CheckBox(referencedComposite, "Use Console Configuration");
		if (cbUseConsole.isEnabled() != useConsole) {
			cbUseConsole.click();
		}
	}
	
	/**
	 * Sets output package
	 * @param pkg given package location
	 */
	public void setPackage(String pkg) {
		new LabeledText(referencedComposite, "Package:").setText(pkg);
	}
	
	/**
	 * Sets hibernate version
	 * @param version given hibernte version
	 */
	public void setHibernateVersion(String version) {
		new LabeledCombo(referencedComposite, "Hibernate Version:").setSelection(version);
	}
	
	/**
	 * Sets database connection profile
	 * @param profileName given profile name
	 */
	public void setDatabaseConnection(String profileName) {
		new LabeledCombo(referencedComposite, "Database Connection").setSelection(profileName);
	}
		
}
