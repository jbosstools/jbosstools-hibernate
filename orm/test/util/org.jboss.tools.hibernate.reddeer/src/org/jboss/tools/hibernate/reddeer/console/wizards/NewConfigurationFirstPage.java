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

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.impl.text.DefaultText;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;

/**
 * Hibernate Console Configuration Location page
 * @author jpeterka
 *
 */public class NewConfigurationFirstPage extends WizardPage {

	public NewConfigurationFirstPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	/**
	 * Sets location
	 * @param location given location
	 */
	public void setLocation(String... location) {
		new DefaultTreeItem(new DefaultTree(referencedComposite), location).select();
	}
	
	/**
	 * Sets location file name
	 * @param filename given file name
	 */
	public void setFilenName(String filename) {
		new DefaultText(referencedComposite, "File name:").setText(filename);
	}
		
}
