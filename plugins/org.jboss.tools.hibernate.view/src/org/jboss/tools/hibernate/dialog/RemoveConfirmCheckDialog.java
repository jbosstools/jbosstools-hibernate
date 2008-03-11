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


public class RemoveConfirmCheckDialog  extends MessageDialog{
	private boolean deleteSourceCode = false;
	private boolean deleteMapping=true;
	// $changed$ by Konstantin Mishin on 2005/08/17 fixed for ORMIISTUD-644
	//private Button check1;
	//private Button check2;
	// $changed$
    private String[] labels;
	public RemoveConfirmCheckDialog(Shell parentShell,String title,String message,String[] labels){
		super(parentShell, title, null, // accept the default window icon
                message, MessageDialog.QUESTION, new String[] {
                        IDialogConstants.YES_LABEL,
                        IDialogConstants.NO_LABEL }, 0); 
		

		this.labels=labels;
	}
	protected Control createCustomArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        Button check1 = new Button(composite, SWT.CHECK);
        check1.addSelectionListener(selectionListenerCheck1);

        check1.setText(labels[0]);
        check1.setFont(parent.getFont());

        Button check2 = new Button(composite, SWT.CHECK);
        check2.addSelectionListener(selectionListenerCheck2);
        check2.setText(labels[1]);
        check2.setFont(parent.getFont());
    	// $changed$ by Konstantin Mishin on 2005/08/17 fixed for ORMIISTUD-644
        //check1.setSelection(true);//checked by default 
        check1.setSelection(deleteMapping);
        check2.setSelection(deleteSourceCode);
    	// $changed$
      
        return composite;
    }
	 private SelectionListener selectionListenerCheck2 = new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            // Button button = (Button) e.widget;
             //if (button.getSelection()) //Returns true if the receiver is selected, and false otherwise.
                 deleteSourceCode =!deleteSourceCode;
             
         }
     };
     private SelectionListener selectionListenerCheck1 = new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            // Button button = (Button) e.widget;
            // if (button.getSelection()) //Returns true if the receiver is selected, and false otherwise.
                 deleteMapping =!deleteMapping;
             
         }
     };

     public  boolean getDeleteSourceCode() {
         return deleteSourceCode;
     }
     public  boolean getDeleteMapping() {
         return deleteMapping;
     }
     
 	// $added$ by Konstantin Mishin on 2005/08/17 fixed for ORMIISTUD-644
     public  void setSelection (boolean isFirstButton, boolean isChecked) {
    	 if (isFirstButton)
    		 deleteMapping = isChecked;
    	 else
    		 deleteSourceCode = isChecked;    	 
     }
 	// $added$
 
}
