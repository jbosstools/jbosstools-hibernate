/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.view;

import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;

/**
 *
 * author: Vitali Yemialyanchyk
 */
public class DiagramEditorStorage implements IStorage {

	protected OrmDiagram ormDiagram = null;
	
	public void init(OrmDiagram ormDiagram) {
		this.ormDiagram = ormDiagram;
	}

	public InputStream getContents() throws CoreException {
		if (ormDiagram == null) {
			return null;
		}
		return null;
	}

	public IPath getFullPath() {
		if (ormDiagram == null) {
			return null;
		}
		return ormDiagram.getStoreFilePath();
	}

	public String getName() {
		if (ormDiagram == null) {
			return null;
		}
		return ormDiagram.getDiagramName();
	}

	public boolean isReadOnly() {
		return false;
	}

    @SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

}
