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
package org.jboss.tools.hibernate.reddeer.hqleditor;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.exception.WaitTimeoutExpiredException;
import org.eclipse.reddeer.swt.api.Shell;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.YesButton;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;

/**
 * HQL Editor RedDeer implementation
 * @author Jiri Peterka
 *
 */
public class HQLEditor extends TextEditor {

	private Logger log = Logger.getLogger(HQLEditor.class);
	
	/**
	 * Sets focus to HQL Editor with given title 
	 * @param title
	 */
	public HQLEditor(String title) {
		super(title);
	}

	/**
	 * Executes HQL query
	 */
	public void runHQLQuery() {
		new DefaultToolItem("Run HQL").click();
		
		try {
			Shell s= new DefaultShell("Open Session factory");
			new YesButton(s).click();
			new WaitWhile(new ShellIsAvailable(s));
		}
		catch (WaitTimeoutExpiredException e) {
			log.warn("Open Session factory question dialog was expected");
		}		
	}
	
}
