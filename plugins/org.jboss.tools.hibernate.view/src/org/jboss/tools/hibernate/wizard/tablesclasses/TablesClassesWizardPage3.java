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
package org.jboss.tools.hibernate.wizard.tablesclasses;

import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.hibernate.core.IMapping;


/**
 * @author sushko
 *
 */

public class TablesClassesWizardPage3 extends WizardPage {
	
	public static final String BUNDLE_NAME = "tablesclasses"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(TablesClassesWizardPage1.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private IMapping ormmodel;
    private Composite container;
    private List list;
    private Button renameButton;
    private Button replaceButton;
	private Text ReplText;
	private Text WithText;	

	/**
	 * createControl() of the TablesClassesWizardPage3
	 * @param parent
	 */
	public void createControl(Composite parent) {
		
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 5;
		container.setBounds(0,0,convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_MARGIN));
		container.setLayout(layout);

		
	    GridData labelData =   new GridData();
	    labelData.horizontalAlignment = GridData.FILL;
	    labelData.horizontalSpan = 5;
		
		Label label = new Label(container, SWT.NULL);
		label.setText(BUNDLE.getString("TablesClassesWizardPage3.label"));
		label.setLayoutData(labelData);
		
	    GridData listData =   new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		list = new List(container,SWT.BORDER);
		list.setBackground(new Color(null,255,255,255));
		list.setLayoutData(listData);
		
	    GridData renamebuttonData =   new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1);
//	    GridData renamebuttonData =   new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_END);
        renameButton= new Button(container, SWT.PUSH);
        renameButton.setText(BUNDLE.getString("TablesClassesWizardPage3.TablesClassesButton"));
        renameButton.setLayoutData(renamebuttonData);
        renameButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//XXX (sushko)? Antonov What should be done by pressing Rename Button on this page?
				//Page should be removed from wizard. but don't delete the file.
			}
		});
       
	    GridData label3Data =   new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
	    
		Label label3 = new Label(container, SWT.NULL);		
		label3.setText(BUNDLE.getString("TablesClassesWizardPage3.label3"));
		label3.setLayoutData(label3Data);
		
	    GridData ReplTextData =   new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		
		ReplText = new Text(container, SWT.BORDER | SWT.SINGLE);
		ReplText.setText(BUNDLE.getString("TablesClassesWizardPage3.ReplText"));
		ReplText.setLayoutData(ReplTextData);
	
		
	    GridData label4Data =   new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
	    
		Label label4 = new Label(container, SWT.NULL);		
		label4.setText(BUNDLE.getString("TablesClassesWizardPage3.label4"));
		label4.setLayoutData(label4Data);
	
	    GridData WithTextData =   new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
	    WithTextData.grabExcessHorizontalSpace =true;
		WithText = new Text(container, SWT.BORDER | SWT.SINGLE);
		WithText.setLayoutData(WithTextData);
		
	    GridData replaceButtonData =   new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		replaceButton= new Button(container, SWT.PUSH);
		replaceButton.setText(BUNDLE.getString("TablesClassesWizardPage3.TablesClassesButtonReplace"));
		replaceButton.setLayoutData(replaceButtonData);
		replaceButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//XXX (sushko)? Antonov  What should be done by pressing Replace Button on this page?  
				//Page should be removed from wizard. but don't delete the file.
}
		});
		this.setPageComplete(true);
		setControl(container);		
	}
	
	/**
	 * constructor of the TablesClassesWizardPage3
 	 * @param ormmodel

	 */
	public TablesClassesWizardPage3(IMapping ormmodel) {
		super("wizardPage");
		setTitle(BUNDLE.getString("TablesClassesWizardPage3.Title"));
		setDescription(BUNDLE.getString("TablesClassesWizardPage3.Description"));
		this.ormmodel = ormmodel;
	}

	/**
	 * canFlipToNextPage of the TablesClassesWizardPage3
	 */
	public boolean canFlipToNextPage() {
		return false;
	}
	
	/**
	 * isPageComplete of the TablesClassesWizardPage3
	 */
	public boolean isPageComplete() {
		return super.isPageComplete();
	}
}
