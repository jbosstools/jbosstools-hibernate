/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.eclipse.launch.ConnectionProfileCtrl;

/**
 * 
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class SelectConnectionProfileDialog extends Dialog {
	
	private ConnectionProfileCtrl connectionProfileCtrl;
	private String selectedCP = null;

	/**
	 * @param shell
	 */
	protected SelectConnectionProfileDialog(Shell shell) {
		super(shell);
	}
	
	public void setDefaultValue(String str){
		selectedCP = str;
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Messages.SelectConnectionProfile); 
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Control c = super.createContents(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(selectedCP != null);
		return c;
	}
	
	@Override
	protected Control createDialogArea(Composite container) {
		Composite parent = (Composite) super.createDialogArea(container);
		
		
		Label label = new Label(parent, SWT.NULL);
		label.setText(Messages.ConnectionProfile);
        connectionProfileCtrl = new ConnectionProfileCtrl(parent, 1, ""); //$NON-NLS-1$
        if (selectedCP != null){
			connectionProfileCtrl.selectValue(selectedCP);
		}
		connectionProfileCtrl.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getButton(IDialogConstants.OK_ID).setEnabled(connectionProfileCtrl.hasConnectionProfileSelected());
				selectedCP = connectionProfileCtrl.getSelectedConnectionName();
			}
		});
		
		
		return parent;
	}
	
	
	
	public String getConnectionProfileName(){
		return selectedCP;
	}

}
