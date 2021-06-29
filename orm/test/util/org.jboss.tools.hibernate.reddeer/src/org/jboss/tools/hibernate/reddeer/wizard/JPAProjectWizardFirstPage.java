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
package org.jboss.tools.hibernate.reddeer.wizard;

import java.util.List;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.reddeer.swt.impl.combo.DefaultCombo;

/**
 * JPA Project wizard page
 * @author jpeterka
 * TODO move to reddeer
 *
 */
public class JPAProjectWizardFirstPage extends NewJavaProjectWizardPageOne {
	
	
	
	public JPAProjectWizardFirstPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	/**
	 * Select JPA version
	 * @param version given JPA version
	 */
	public void setJPAVersion(String version) {
		new DefaultCombo(referencedComposite, 1).setSelection(version.toString());
	}
	
	/**
	 * Set target runtime
	 * @param runtime runtime version to set
	 */
	public void setTargetRuntime(String runtime) {
		new DefaultCombo(referencedComposite, 0).setSelection(runtime.toString());
		
	}

	/**
	 * Get list of available runtimes
	 * @return list of runtimes
	 */
	public List<String> getTargetRuntimes() {
		return new DefaultCombo(referencedComposite, 0).getItems();
	}
}
