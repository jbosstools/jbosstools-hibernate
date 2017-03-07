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

import org.jboss.reddeer.eclipse.jdt.ui.ide.NewJavaProjectWizardPage;
import org.jboss.reddeer.swt.impl.combo.DefaultCombo;

/**
 * JPA Project wizard page
 * @author jpeterka
 * TODO move to reddeer
 *
 */
public class JPAProjectWizardFirstPage extends NewJavaProjectWizardPage {
	
	/**
	 * Select JPA version
	 * @param version given JPA version
	 */
	public void setJPAVersion(String version) {
		new DefaultCombo(1).setSelection(version.toString());
	}
}
