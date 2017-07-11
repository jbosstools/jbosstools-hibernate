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
package org.jboss.tools.hibernate.reddeer.console.wizards;

import org.jboss.reddeer.jface.wizard.NewWizardDialog;
/**
 * New Hibernate Configuration File Wizard RedDeer implemenation
 * @author jpeterka
 *
 */
public class NewConfigurationWizard extends NewWizardDialog{

	/**
	 * Initializes wizard
	 */
	public NewConfigurationWizard() {
		super("Hibernate","Hibernate Configuration File (cfg.xml)");
	}

}
