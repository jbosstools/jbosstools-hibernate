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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


public class SelectAdditionPersClasses extends TitleAreaDialog {
	
		IAction persistentClassesWizardAction;
		private String title;
		private Button addPersClButton,reverseEgeneeringButton;
		public static final String BUNDLE_NAME = "messages"; 
		public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(SelectAdditionPersClasses.class.getPackage().getName() + "." + BUNDLE_NAME); 
		public static int PERSISTENT_CLASSES_WIZARD = 101;
		public static int TABLES_CLASSES_WIZARD = 102;		
		
		public SelectAdditionPersClasses(Shell shell) {
			super(shell);
			this.setTitle(BUNDLE.getString("SelectAdditionPersClasses.Title"));
			
		}
		
			 protected Control createDialogArea(Composite parent) {
			       
			        Composite root = new Composite(parent, SWT.NULL);
			        GridLayout layout = new GridLayout();
			        layout.numColumns = 3;
					root.setLayout(layout);
					GridData data = new GridData(GridData.FILL_HORIZONTAL);
					data.grabExcessHorizontalSpace = true;
					data.grabExcessVerticalSpace=true;
					root.setLayoutData(data);

					Label label1 = new Label(root, SWT.NULL);
					label1.setText("                                          ");
					
					reverseEgeneeringButton=new Button(root, SWT.PUSH);
					data = new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1);
				    data.verticalIndent=7;
				    reverseEgeneeringButton.setLayoutData(data);
					reverseEgeneeringButton.setText(BUNDLE.getString("SelectAdditionPersClasses.reverseEgeneeringButton"));
					
					Label label2 = new Label(root, SWT.NULL);
					label2.setText("                                           ");
					Label label3 = new Label(root, SWT.NULL);
					label3.setText("                                          ");
					
					addPersClButton= new Button(root, SWT.PUSH);
				    data=new  GridData(GridData.HORIZONTAL_ALIGN_FILL);
				    data.verticalIndent=7;
				    addPersClButton.setLayoutData(data);
					addPersClButton.setText(BUNDLE.getString("SelectAdditionPersClasses.addPersClButton"));
					
					Label label4 = new Label(root, SWT.NULL);
					label4.setText("                                          ");
					setMessage(BUNDLE.getString("SelectAdditionPersClasses.message"));
					addPersClButton.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							setReturnCode(PERSISTENT_CLASSES_WIZARD);
							close();
						}
					});
					reverseEgeneeringButton.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							setReturnCode(TABLES_CLASSES_WIZARD);
							close();
					}
					});
						
			      return root;
			    }
			
			 protected void createButtonsForButtonBar(Composite parent) {
					super.createButtonsForButtonBar(parent);
					this.getButton(IDialogConstants.OK_ID).setVisible(false);			
				}

			 protected void configureShell(Shell shell) {
					super.configureShell(shell);
					if (title != null)
						shell.setText(title);
				}
			    public void setTitle(String title) {
					this.title = title;
				}
			   
		}





