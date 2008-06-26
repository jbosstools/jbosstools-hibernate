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
package org.jboss.tools.hibernate.wizard.hibernateconnection;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * @author eskimo
 * 
 */
public class ListContentProvider implements IStructuredContentProvider {
	List contents;

	public ListContentProvider() {
	}

	public Object[] getElements(Object input) {
		if (contents != null && contents == input)
			return contents.toArray();
		return new Object[0];
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof List)
			contents = (List) newInput;
		else
			contents = null;
	}

	public void dispose() {
	}

	public boolean isDeleted(Object o) {
		return contents != null && !contents.contains(o);
	}
}
