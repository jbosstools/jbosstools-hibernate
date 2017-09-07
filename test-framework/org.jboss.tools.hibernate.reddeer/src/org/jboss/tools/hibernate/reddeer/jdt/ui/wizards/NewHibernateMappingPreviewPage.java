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
package org.jboss.tools.hibernate.reddeer.jdt.ui.wizards;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.impl.text.DefaultText;


/**
 * Hibernate XML mapping file preview page
 * User can see preview of generated code
 * @author jpeterka
 *
 */
public class NewHibernateMappingPreviewPage extends WizardPage {
	
	public NewHibernateMappingPreviewPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	/**
	 * Gets preview page text for hbm xml file wizard
	 * @return preview text
	 */
	public String getPreviewText() {
		String ret = new DefaultText(referencedComposite).getText();
		return ret;
	}
}
