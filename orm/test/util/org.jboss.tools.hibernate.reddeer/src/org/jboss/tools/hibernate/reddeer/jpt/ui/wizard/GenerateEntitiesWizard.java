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

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.core.matcher.WithTextMatcher;
import org.eclipse.reddeer.jface.window.Openable;
import org.eclipse.reddeer.jface.wizard.WizardDialog;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;

/**
 * Wizard for JPA Entities generation
 * @author Jiri Peterka
 *
 */
public class GenerateEntitiesWizard extends WizardDialog{

	/**
	 * Initializes Generate Entities Wizard 
	 */
	public GenerateEntitiesWizard() {
	}
	
	@Override
	protected Openable getOpenAction() {
		return new OpenEngitiesWizardAction();
	}
	
	class OpenEngitiesWizardAction extends Openable {
		
		public OpenEngitiesWizardAction() {
			super(new WithTextMatcher("Generate Entities"));
		}

		@Override
		public void run() {
			new ContextMenuItem("JPA Tools","Generate Entities from Tables...").select();
			
		}
		
	}
	
	/**
	 * Clicks finish button
	 */
	public void finish() {
		super.finish(TimePeriod.LONG);
	}
}
