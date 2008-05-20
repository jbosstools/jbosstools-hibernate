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
package org.jboss.tools.hibernate.internal.core.properties;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.PropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetPage;

/**
 * @author kaa
 * akuzmin@exadel.com
 * Aug 5, 2005
 */
public class PropertySheetPageWithDescription extends PropertySheetPage {
	private Text description;
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.PropertySheetPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		description= new Text(parent,SWT.BORDER | SWT.SINGLE );
		description.setEditable(false);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		
		data.grabExcessHorizontalSpace=true;
		description.setLayoutData(data);
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        Menu menu = menuMgr.createContextMenu(this.getControl());
        getControl().setMenu(menu);
		}

	public void handleEntrySelection(ISelection selection) {
		if ((((StructuredSelection)selection).getFirstElement()!=null)&&(((PropertySheetEntry)((StructuredSelection)selection).getFirstElement()).getDescription()!=null))
		description.setText(((PropertySheetEntry)((StructuredSelection)selection).getFirstElement()).getDescription());
		super.handleEntrySelection(selection);
	}

}
