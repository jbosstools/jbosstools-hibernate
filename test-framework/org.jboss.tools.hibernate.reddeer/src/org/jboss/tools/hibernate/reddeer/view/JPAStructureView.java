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
package org.jboss.tools.hibernate.reddeer.view;

import org.jboss.reddeer.workbench.impl.view.WorkbenchView;

/**
 * JPA Structure View RedDeer implementation
 * @author Jiri Peterka
 * TODO move to reddeer
 *
 */
public class JPAStructureView extends WorkbenchView {
	
	/**
	 * Initialization and lookup for JPA Details view
	 */
	public JPAStructureView() {
		super("JPA Structure");
	}

}
