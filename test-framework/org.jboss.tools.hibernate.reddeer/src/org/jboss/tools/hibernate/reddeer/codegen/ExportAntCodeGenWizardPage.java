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

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

/**
 * Export Hibernate Code Generation Configuration to Ant Script wizard page
 * @author Jiri Peterka
 *
 */
public class ExportAntCodeGenWizardPage extends WizardPage {
	
	public ExportAntCodeGenWizardPage(ReferencedComposite composite) {
		super(composite);
	}

	/**
	 * Sets given generation configuration
	 */
	public void setHibernateGenConfiguration(String genConfiguration) {
		new LabeledCombo(referencedComposite, "Hibernate Code Generation Configurations:").setSelection(genConfiguration);
	}

	/**
	 * Sets given generation configuration
	 */
	public void setAntFileName(String fileName) {
		new LabeledText(referencedComposite, "File name:").setText(fileName);
	}

}
