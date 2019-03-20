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

import org.eclipse.reddeer.eclipse.selectionwizard.NewMenuWizard;

/**
 * New Hibernate xml mapping file wizard
 * @author Jiri Peterka
 *
 */
public class NewHibernateMappingFileWizard extends NewMenuWizard {

	/**
	 * Initializes wizard
	 */
	public NewHibernateMappingFileWizard() {
		super("New Hibernate XML Mapping files (hbm.xml)", "Hibernate", "Hibernate XML Mapping file (hbm.xml)");
	}
}
