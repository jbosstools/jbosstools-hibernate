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

import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;


public class RefreshSchemaMappingDialog extends Dialog {
	private String title;
	private boolean isUseFuzzyName=false;
	
	private Button schemaToObjectRadio,objectToSchemaRadio,meetAtTheMiddleButRadio,fuzyNameCheck;
	public static int REFRESH_MAPPING_SCHEMA_TO_OBJECT = 1001;
	public static int REFRESH_MAPPING_OBJECT_TO_SCHEMA = 1002;	
	public static int REFRESH_MAPPING_MIDDLE = 1003;
	private int returncode;
	public static final String BUNDLE_NAME = "messages"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(RefreshSchemaMappingDialog.class.getPackage().getName() + "." + BUNDLE_NAME); 
	public RefreshSchemaMappingDialog(Shell parent) {
		super(parent);
		this.setTitle(BUNDLE.getString("RefreshSchemaMappingDialog.Title"));		
	}
	 protected Control createDialogArea(Composite parent) {
	    	
	        Composite root = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			root.setLayout(layout);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.grabExcessHorizontalSpace = true;
			data.heightHint=150;
			data.widthHint=400;
			root.setLayoutData(data);

			
			Group grMapApproach = new Group(root, SWT.NONE);
			GridLayout Grlayout = new GridLayout();
			//Grlayout.numColumns = 6;
			grMapApproach.setLayout(Grlayout);
			grMapApproach.setText(BUNDLE.getString("RefreshSchemaMappingDialog.MappingApproach"));
		    GridData groupData =new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		    grMapApproach.setLayoutData(groupData);
		    schemaToObjectRadio=new Button(grMapApproach, SWT.RADIO);
		    schemaToObjectRadio.setText(BUNDLE.getString("RefreshSchemaMappingDialog.SchemaToObject"));
		    data=new GridData();
		    data.verticalIndent=9;
		    schemaToObjectRadio.addSelectionListener(selectionListener);
		    schemaToObjectRadio.setLayoutData(data);
		    
		    objectToSchemaRadio=new Button(grMapApproach, SWT.RADIO);
		    objectToSchemaRadio.setText(BUNDLE.getString("RefreshSchemaMappingDialog.ObjectToSchema"));
		    objectToSchemaRadio.addSelectionListener(selectionListener);

		    meetAtTheMiddleButRadio=new Button(grMapApproach, SWT.RADIO);
		    meetAtTheMiddleButRadio.setText(BUNDLE.getString("RefreshSchemaMappingDialog.MeetAtTheMddle"));
		    meetAtTheMiddleButRadio.addSelectionListener(selectionListener);
		    
		    
		    fuzyNameCheck=new Button(root, SWT.CHECK);
		    fuzyNameCheck.setText(BUNDLE.getString("RefreshSchemaMappingDialog.Check"));
		    fuzyNameCheck.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					DoCheck();
				}
			});

				
	      return root;
	    }
	 
	 private SelectionListener selectionListener = new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
             Button button = (Button) e.widget;
             if (button.getSelection()) //Returns true if the receiver is selected, and false otherwise.
                  if((button == schemaToObjectRadio))
                	  returncode=REFRESH_MAPPING_SCHEMA_TO_OBJECT;
                  else if((button == objectToSchemaRadio))
                	  returncode=REFRESH_MAPPING_OBJECT_TO_SCHEMA;
                  else if((button == meetAtTheMiddleButRadio))
                	  returncode=REFRESH_MAPPING_MIDDLE;
            	 
             
         }
     };
	    protected void configureShell(Shell shell) {
			super.configureShell(shell);
			if (title != null)
				shell.setText(title);
		}
	    public void setTitle(String title) {
			this.title = title;
		}
	    protected void cancelPressed() {
			super.cancelPressed();
		}
	    protected void okPressed() {
	    	setReturnCode(returncode);
	    	close();
		//super.okPressed();
	    }

	    public boolean getIsFuzzyName(){
	    	return isUseFuzzyName;
	    }
	    
	    public void DoCheck(){
	    	isUseFuzzyName=!isUseFuzzyName;
	    }
}
