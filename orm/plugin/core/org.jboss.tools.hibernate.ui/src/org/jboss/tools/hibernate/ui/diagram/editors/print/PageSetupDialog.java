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
package org.jboss.tools.hibernate.ui.diagram.editors.print;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * initial implementation
 * 
 * @author Vitali Yemialyanchyk
 */
public class PageSetupDialog extends Dialog {

	public PageSetupDialog(Shell parentShell) {
		super(parentShell);
	}
	
	protected void cancelPressed() {
		super.cancelPressed();
	}

	protected Control createDialogArea(Composite parent) {
		//super.createDialogArea(parent);
		getShell().setText("Page Setup");
		return parent;
	}
	
	protected void okPressed() {
	}

	public Button getOkButton() {
		return super.getButton(OK);
	}
}
