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

import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.swt.impl.button.FinishButton;
import org.jboss.reddeer.swt.impl.menu.ContextMenu;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.common.wait.WaitWhile;

/**
 * Wizard for JPA Entities generation
 * @author Jiri Peterka
 *
 */
public class GenerateEntitiesWizard {

	/**
	 * Initializes Generate Entities Wizard 
	 */
	public GenerateEntitiesWizard() {
	}
	
	/**
	 * Opens Wizard for JPA entities generation
	 */
	public void open() {
		new ContextMenu("JPA Tools","Generate Entities from Tables...").select();
		new WaitUntil(new ShellWithTextIsAvailable("Generate Entities"));
		new DefaultShell("Generate Entities");
	}
	
	/**
	 * Clicks finish button
	 */
	public void finish() {	
		new FinishButton().click();
		new WaitWhile(new ShellWithTextIsAvailable("Generate Entities"),TimePeriod.LONG);
		new WaitWhile(new JobIsRunning(),TimePeriod.LONG);
	}
}
