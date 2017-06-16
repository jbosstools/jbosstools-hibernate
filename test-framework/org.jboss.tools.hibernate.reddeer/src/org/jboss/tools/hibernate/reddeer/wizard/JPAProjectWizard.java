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
package org.jboss.tools.hibernate.reddeer.wizard;

import org.jboss.reddeer.jface.wizard.NewWizardDialog;

/**
 * New JPA Project Wizard
 * @author Jiri Peterka
 * TODO move to reddeer
 *
 */
public class JPAProjectWizard extends NewWizardDialog {

	/**
	 * Initialize New JPA project wizard
	 */
	public JPAProjectWizard() {
		super("JPA", "JPA Project");
	}
}
