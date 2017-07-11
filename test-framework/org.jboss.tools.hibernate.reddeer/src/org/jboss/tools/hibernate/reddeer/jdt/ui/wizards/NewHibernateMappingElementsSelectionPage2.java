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

import org.jboss.reddeer.jface.wizard.WizardPage;
import org.jboss.reddeer.swt.impl.table.DefaultTable;


/**
 * Hibernate Mapping File Element Selection Page
 * Here user select package or classes for further hbm.xml generation
 * @author jpeterka
 *
 */
public class NewHibernateMappingElementsSelectionPage2 extends WizardPage {

	/**
	 * Select items in the list for hbm.xml generation
	 */
	public void selectItem(String... items) {
		new DefaultTable().select(items);
	}
	
}
