/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.platform;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.ui.internal.platform.base.EntitiesGenerator;
import org.eclipse.jpt.ui.internal.platform.generic.GenericPlatformUi;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernatePlatformUI extends GenericPlatformUi {
	
	/**
	 * Change method realization to provide other entities generation.
	 */
	@Override
	public void generateEntities(JpaProject project, IStructuredSelection selection) {
		EntitiesGenerator.generate(project, selection);
	}

}
