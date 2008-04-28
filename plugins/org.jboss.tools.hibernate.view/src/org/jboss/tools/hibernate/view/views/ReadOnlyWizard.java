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
package org.jboss.tools.hibernate.view.views;

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.TreeItem;
import org.jboss.tools.hibernate.core.IOrmElement;


// create tau 13.02.2006
// for ESORM-513 Overwrites and changes our Hibernate mapping files, even if they are marked read-only?

public abstract class ReadOnlyWizard extends Wizard {
	
	protected ContentViewer viewer;
	protected boolean readOnly = false;
	
	
	public ReadOnlyWizard (IOrmElement ormElement, TreeViewer viewer) {
		super();
		this.viewer = viewer;
		
		if (ormElement == null) {
			ISelection selection = viewer.getSelection();
			if (!selection.isEmpty()) {
				TreeItem[] selectionTreeItems =  viewer.getTree().getSelection();
				if((selectionTreeItems.length != 0) && (selection instanceof StructuredSelection)) {
						ormElement = (IOrmElement) ((StructuredSelection) selection).getFirstElement();
				}
			}
		}
		
		if (ormElement != null) {
			Object readOnlyObject = ormElement.accept(ViewsUtils.readOnlyVisitor, viewer);
			if (readOnlyObject != null && readOnlyObject instanceof Boolean && ((Boolean) readOnlyObject).booleanValue()) {
				readOnly = true;
			}
		}
		
	}

}
