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
package org.jboss.tools.hibernate.reddeer.criteriaeditor;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.common.exception.RedDeerException;
import org.eclipse.reddeer.swt.api.Shell;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.YesButton;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;

/**
 * Hibernate Criteria Editor RedDeer implementation
 * @author Jiri Peterka
 *
 */
public class CriteriaEditor extends TextEditor {

	private Logger log = Logger.getLogger(CriteriaEditor.class);
	
	/**
	 * Sets focus to Criteria Editor with given title 
	 * @param hibernateConsoleName hibernateConsoleName
	 */
	public CriteriaEditor(String hibernateConsoleName) {
		super("Criteria:" + hibernateConsoleName);
	}

	/**
	 * Executes criteria 
	 */
	public void runCriteria() {
		new DefaultToolItem("Run criteria").click();
		
		try {
			Shell s = new DefaultShell("Open Session factory");
			new YesButton(s).click();
			new WaitWhile(new ShellIsAvailable(s));
		}
		catch (RedDeerException e) {
			log.warn("Open Session factory question dialog was expected");
		} finally {
			new WaitWhile(new JobIsRunning());
		}
	}
	
}
