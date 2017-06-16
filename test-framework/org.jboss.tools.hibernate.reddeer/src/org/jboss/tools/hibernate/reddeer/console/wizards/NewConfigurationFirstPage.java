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

import org.jboss.reddeer.jface.wizard.WizardPage;
import org.jboss.reddeer.swt.impl.text.DefaultText;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;

/**
 * Hibernate Console Configuration Location page
 * @author jpeterka
 *
 */public class NewConfigurationFirstPage extends WizardPage {

	/**
	 * Sets location
	 * @param location given location
	 */
	public void setLocation(String... location) {
		new DefaultTreeItem(location).select();
	}
	
	/**
	 * Sets location file name
	 * @param filename given file name
	 */
	public void setFilenName(String filename) {
		new DefaultText("File name:").setText(filename);
	}
		
}
