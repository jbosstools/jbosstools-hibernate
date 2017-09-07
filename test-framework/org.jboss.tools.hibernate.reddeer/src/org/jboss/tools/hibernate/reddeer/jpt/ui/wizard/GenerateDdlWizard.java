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

import org.eclipse.reddeer.core.matcher.WithTextMatcher;
import org.eclipse.reddeer.jface.window.Openable;
import org.eclipse.reddeer.jface.wizard.WizardDialog;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;

/**
 * Wizard for DDL generation
 * @author Jiri Peterka
 *
 */
public class GenerateDdlWizard extends WizardDialog{
	
	@Override
	protected Openable getOpenAction() {
		return new OpenDDLWizardAction();
	}
	
	class OpenDDLWizardAction extends Openable {
		
		public OpenDDLWizardAction() {
			super(new WithTextMatcher("Generate Tables from Entities"));
		}

		@Override
		public void run() {
			new ContextMenuItem("JPA Tools","Generate Tables from Entities...").select();
			
		}
		
	}
}
