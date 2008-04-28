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
package org.jboss.tools.hibernate.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class RemoveConfirmRadioDialog extends MessageDialog{
	private boolean deleteMapping=false;
    private Button radio1;
    private Button radio2;
    private String[] labels;
	public RemoveConfirmRadioDialog(Shell parentShell,String title,String message,String[] labels){
		super(parentShell, title, null, // accept the default window icon
                message, MessageDialog.QUESTION, new String[] {
                        IDialogConstants.YES_LABEL,
                        IDialogConstants.NO_LABEL }, 0); 
		

		this.labels=labels;
	}
	protected Control createCustomArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        radio1 = new Button(composite, SWT.RADIO);
        radio1.addSelectionListener(selectionListenerRadio);

        radio1.setText(labels[0]);
        radio1.setFont(parent.getFont());

        radio2 = new Button(composite, SWT.RADIO);
        radio2.addSelectionListener(selectionListenerRadio);
        radio2.setText(labels[1]);
        radio2.setFont(parent.getFont());
        radio2.setSelection(true);//checked by default 
       
        return composite;
    }
	 
     private SelectionListener selectionListenerRadio = new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
             Button button = (Button) e.widget;
             if (button.getSelection()) //Returns true if the receiver is selected, and false otherwise.
                 deleteMapping =(button == radio1);
             
         }
     };

     
     public  boolean getDeleteMapping() {
         return deleteMapping;
     }
 
}

