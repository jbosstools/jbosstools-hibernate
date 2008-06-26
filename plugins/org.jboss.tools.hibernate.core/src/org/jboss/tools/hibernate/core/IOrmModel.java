/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;

/**
 * @author Tau
 *
 */
public interface IOrmModel {
	public IOrmProject[] getOrmProjects();
	public void addOrmProject(IOrmProject model);
	public IOrmProject getOrmProject(IProject project);
	public void removeOrmProject(IOrmProject project);
	public void removeOrmProject(IProject project);
	public void removeOrmProjects();
	public int size();
	// add 01.03.2005 tau
	public void addListener(IOrmModelListener listener);
	public void removeListener(IOrmModelListener listener);
	// add 18.04.2005 tau
	public void fireOrmModelChanged();
	//added 05/20/05 by alex
	public void resourcesChanged(IResourceDelta delta);
	public boolean isEmptyListeners();
}
