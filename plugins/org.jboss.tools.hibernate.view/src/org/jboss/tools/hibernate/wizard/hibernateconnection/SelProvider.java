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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * @author sushko
 *
 */


/*
 * Helper class to simulate a selection provider
 */
public class SelProvider implements ISelectionProvider {

	protected IStructuredSelection projectSelection = StructuredSelection.EMPTY;
	
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		// do nothing
	}

	public ISelection getSelection() {
		return projectSelection;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		// do nothing
	}

	public void setSelection(ISelection selection) {
		// do nothing
	}
}





