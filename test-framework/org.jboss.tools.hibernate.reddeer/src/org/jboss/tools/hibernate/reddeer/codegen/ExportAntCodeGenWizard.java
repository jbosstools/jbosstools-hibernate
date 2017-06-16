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
package org.jboss.tools.hibernate.reddeer.codegen;

import org.jboss.reddeer.jface.wizard.ExportWizardDialog;


/**
 * RedDeer dialog for Ant Hibernate Code Generation configuration export
 * @author Jiri Peterka
 *
 */
public class ExportAntCodeGenWizard extends ExportWizardDialog {

	public static final String CATEGORY = "Hibernate";
	
	public static final String NAME = "Ant Code Generation";
	
	/**
	 * Initialize Export Ang Code Generation wizard
	 */
	public ExportAntCodeGenWizard() {
		super(CATEGORY, NAME);
	}

}