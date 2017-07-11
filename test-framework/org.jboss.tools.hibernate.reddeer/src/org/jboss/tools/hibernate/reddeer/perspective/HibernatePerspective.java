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
package org.jboss.tools.hibernate.reddeer.perspective;

import org.jboss.reddeer.eclipse.ui.perspectives.AbstractPerspective;

/**
 * Hibernate Perspective implementation
 * 
 * @author Jiri Peterka
 *
 */
public class HibernatePerspective extends AbstractPerspective {

	/**
	 * Constructs the perspective with "Hibernate".
	 */
	public HibernatePerspective() {
		super("Hibernate");
	}
}
