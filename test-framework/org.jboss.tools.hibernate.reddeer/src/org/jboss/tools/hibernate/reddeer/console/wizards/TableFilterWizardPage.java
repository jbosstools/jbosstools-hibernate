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

import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.condition.TreeHasChildren;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import org.eclipse.reddeer.swt.impl.group.DefaultGroup;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.core.reference.ReferencedComposite;

/**
 * New Reverse Engineering Table wizard page
 * @author Jiri Peterka
 *
 */
public class TableFilterWizardPage extends WizardPage {

	public TableFilterWizardPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	/**
	 * Sets console configuration for reveng 
	 * @param cfgName console name
	 */
	public void setConsoleConfiguration(String cfgName) {
		new LabeledCombo(referencedComposite, "Console configuration:").setSelection(cfgName);
	}
	
	/**
	 * Refreshes database schema
	 */
	public void refreshDatabaseSchema() {
		new PushButton(referencedComposite, "Refresh").click();
		DefaultGroup group = new DefaultGroup(referencedComposite, "Database schema:");
		DefaultTree tree = new DefaultTree(group);
		new WaitUntil(new TreeHasChildren(tree));
	}
	
	/**
	 * Sets parent folder for reveng
	 * @param folder given folder name
	 */
	public void setParentFolder(String folder) {
		new LabeledText(referencedComposite, "Parent folder:").setText(folder);
	}

	/**
	 * Press include button
	 */
	public void pressInclude() {
		new PushButton(referencedComposite, "Include...").click();	
	}
}
