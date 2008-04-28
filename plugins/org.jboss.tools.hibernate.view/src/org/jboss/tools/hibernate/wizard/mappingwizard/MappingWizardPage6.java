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
package org.jboss.tools.hibernate.wizard.mappingwizard;

import java.util.ResourceBundle;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping;


/**
 * @author kaa
 *
 * Custom create, update, delete operations
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MappingWizardPage6 extends WizardPage {
	public static final String BUNDLE_NAME = "mappingwizard"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(MappingWizardPage6.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private IMapping mod;
	public boolean isFinish=false;
    private Text InsertText;
    private Text UpdateText;
    private Text DeleteText;	
    private String SelectedPC;
	private Button isInsert;
	private Button isUpdate;
	private Button isDelete;
    
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 5;
		layout.marginHeight=0;
		layout.horizontalSpacing=0;
		container.setLayout(layout);

		Label label1 = new Label(container, SWT.NULL);
		label1.setText("    ");
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan=2;
		label1.setLayoutData(data);
		
		Label label2 = new Label(container, SWT.NULL);
		label2.setText(BUNDLE.getString("MappingWizardPage6.labelInsert"));
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan=3;		
		label2.setLayoutData(data);
		
		
		isInsert= new Button(container, SWT.CHECK);
		isInsert.setText(BUNDLE.getString("MappingWizardPage6.isInsert"));
		isInsert.setLayoutData(layout);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.heightHint = 26;
		isInsert.setLayoutData(data);

		Label label3 = new Label(container, SWT.NULL);
		label3.setText("    ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		label3.setLayoutData(data);
		
		
		InsertText = new Text(container,SWT.BORDER | SWT.MULTI);
		InsertText.setBackground(new Color(null,255,255,255));
	    data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		data.horizontalSpan=3;
		data.grabExcessHorizontalSpace=true;
		data.grabExcessVerticalSpace=true;
		InsertText.setLayoutData(data);		

		Label label4 = new Label(container, SWT.NULL);
		label4.setText("    ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan=2;
		label4.setLayoutData(data);
		
		Label label5 = new Label(container, SWT.NULL);
		label5.setText(BUNDLE.getString("MappingWizardPage6.labelUpdate"));
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan=3;		
		label5.setLayoutData(data);
		
		
		isUpdate= new Button(container, SWT.CHECK);
		isUpdate.setText(BUNDLE.getString("MappingWizardPage6.isInsert"));
		isUpdate.setLayoutData(layout);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.heightHint = 26;
		isUpdate.setLayoutData(data);

		Label label6 = new Label(container, SWT.NULL);
		label6.setText("    ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		label6.setLayoutData(data);
		
		
		UpdateText = new Text(container,SWT.BORDER | SWT.MULTI);
		UpdateText.setBackground(new Color(null,255,255,255));
	    data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		data.horizontalSpan=3;
		data.grabExcessHorizontalSpace=true;
		data.grabExcessVerticalSpace=true;
		UpdateText.setLayoutData(data);		
		
		Label label7 = new Label(container, SWT.NULL);
		label7.setText("    ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan=2;
		label7.setLayoutData(data);

		
		Label label8 = new Label(container, SWT.NULL);
		label8.setText(BUNDLE.getString("MappingWizardPage6.labelDelete"));
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan=3;		
		label8.setLayoutData(data);
		
		
		isDelete= new Button(container, SWT.CHECK);
		isDelete.setText(BUNDLE.getString("MappingWizardPage6.isInsert"));
		isDelete.setLayoutData(layout);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.heightHint = 26;
		isDelete.setLayoutData(data);

		Label label9 = new Label(container, SWT.NULL);
		label9.setText("    ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		label9.setLayoutData(data);
		
		
		DeleteText = new Text(container,SWT.BORDER | SWT.MULTI);
		DeleteText.setBackground(new Color(null,255,255,255));
	    data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		data.horizontalSpan=3;
		data.grabExcessHorizontalSpace=true;
		data.grabExcessVerticalSpace=true;
		DeleteText.setLayoutData(data);		
		setControl(container);		
	}
	public MappingWizardPage6(IMapping mod){
		super("MappingWizardPage");
		setTitle(BUNDLE.getString("MappingWizardPage1.title"));
		setDescription(BUNDLE.getString("MappingWizardPage6.description")+"\n"+BUNDLE.getString("MappingWizardPage6.descriptionpage"));
	  	this.mod=mod;
	}

	public void SetVariables(String NewSelectedPC)
	{
		SelectedPC=NewSelectedPC;
		isInsert.setSelection(((ClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).isCustomInsertCallable());		
		isUpdate.setSelection(((ClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).isCustomUpdateCallable());
		isDelete.setSelection(((ClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).isCustomDeleteCallable());		
		
		if (((ClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getCustomSQLInsert()==null)
			InsertText.setText("");
		else
			InsertText.setText(((ClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getCustomSQLInsert());
		if (((ClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getCustomSQLUpdate()==null)
			UpdateText.setText("");
		else
			UpdateText.setText(((ClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getCustomSQLUpdate());
		if (((ClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getCustomSQLDelete()==null)
			DeleteText.setText("");
		else
			DeleteText.setText(((ClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getCustomSQLDelete());
		
	}

	public void SetResaults()
	{	
   		((ClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).setCustomSQLUpdate(UpdateText.getText(),isUpdate.getSelection());
   		((ClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).setCustomSQLInsert(InsertText.getText(),isInsert.getSelection());
   		((ClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).setCustomSQLDelete(DeleteText.getText(),isDelete.getSelection());
	}
}
