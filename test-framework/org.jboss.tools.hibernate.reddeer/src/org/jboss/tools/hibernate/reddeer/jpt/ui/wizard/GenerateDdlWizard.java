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

import org.jboss.reddeer.jface.wizard.WizardDialog;
import org.jboss.reddeer.swt.impl.menu.ContextMenu;

/**
 * Wizard for DDL generation
 * @author Jiri Peterka
 *
 */
public class GenerateDdlWizard extends WizardDialog{

	/**
	 * Initialzie DDL generation wizard
	 */
	public GenerateDdlWizard() {
	}
	
	/**
	 * Opens ddl generation wizard
	 */
	public void open() {
		new ContextMenu("JPA Tools","Generate Tables from Entities...").select();
	}
}
