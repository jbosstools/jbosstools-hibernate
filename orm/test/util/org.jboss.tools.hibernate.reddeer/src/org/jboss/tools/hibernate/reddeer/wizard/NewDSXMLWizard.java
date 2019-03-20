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

import org.eclipse.reddeer.eclipse.selectionwizard.NewMenuWizard;


/**
 * Datasource RedDeer Wizard
 * @author Jiri Peterka
 * TODO move to jbosstools.JST
 */
public class NewDSXMLWizard extends NewMenuWizard {

	/**
	 * Initialize JBoss Datasource wizard
	 */
	public NewDSXMLWizard() {
		super("New JBoss Datasource","JBoss Tools", "JBoss Datasource (-ds.xml)");
	}

}
