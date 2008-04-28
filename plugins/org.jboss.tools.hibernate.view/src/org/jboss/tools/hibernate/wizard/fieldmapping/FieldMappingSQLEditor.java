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
package org.jboss.tools.hibernate.wizard.fieldmapping;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;

/**
 * @author akuzmin - akuzmin@exadel.com
 *
 * May 31, 2005 - SQLEdit page for Custom sql tab in FieldMappingWizardPage5
 */
public class FieldMappingSQLEditor extends Composite {
	public static final String BUNDLE_NAME = "fieldmapping"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(FieldMappingSQLEditor.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private ICollectionMapping cm;
    private Text InsertText;
    private Text UpdateText;
    private Text DeleteText;
    private Text DeleteAllText;    
	private Button isInsert;
	private Button isUpdate;
	private Button isDelete;
	private Button isDeleteAll;
	
	public FieldMappingSQLEditor(Composite parent) {
			super(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 5;
			layout.marginHeight=0;
			layout.horizontalSpacing=0;
			this.setLayout(layout);

			Label label1 = new Label(this, SWT.NULL);
			label1.setText("    ");
			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			data.horizontalSpan=2;
			label1.setLayoutData(data);
			
			Label label2 = new Label(this, SWT.NULL);
			label2.setText(BUNDLE.getString("FieldMappingSQLEditor.labelInsert"));
			data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
			data.horizontalSpan=3;		
			label2.setLayoutData(data);
			
			
			isInsert= new Button(this, SWT.CHECK);
			isInsert.setText(BUNDLE.getString("FieldMappingSQLEditor.isInsert"));
			isInsert.setLayoutData(layout);
			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			data.heightHint = 26;
			isInsert.setLayoutData(data);

			Label label3 = new Label(this, SWT.NULL);
			label3.setText("    ");
			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			label3.setLayoutData(data);
			
			
			InsertText = new Text(this,SWT.BORDER | SWT.MULTI);
			InsertText.setBackground(new Color(null,255,255,255));
		    data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
			data.horizontalSpan=3;
			data.grabExcessHorizontalSpace=true;
			data.grabExcessVerticalSpace=true;
			InsertText.setLayoutData(data);		

			Label label4 = new Label(this, SWT.NULL);
			label4.setText("    ");
			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			data.horizontalSpan=2;
			label4.setLayoutData(data);
			
			Label label5 = new Label(this, SWT.NULL);
			label5.setText(BUNDLE.getString("FieldMappingSQLEditor.labelUpdate"));
			data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
			data.horizontalSpan=3;		
			label5.setLayoutData(data);
			
			
			isUpdate= new Button(this, SWT.CHECK);
			isUpdate.setText(BUNDLE.getString("FieldMappingSQLEditor.isInsert"));
			isUpdate.setLayoutData(layout);
			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			data.heightHint = 26;
			isUpdate.setLayoutData(data);

			Label label6 = new Label(this, SWT.NULL);
			label6.setText("    ");
			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			label6.setLayoutData(data);
			
			
			UpdateText = new Text(this,SWT.BORDER | SWT.MULTI);
			UpdateText.setBackground(new Color(null,255,255,255));
		    data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
			data.horizontalSpan=3;
			data.grabExcessHorizontalSpace=true;
			data.grabExcessVerticalSpace=true;
			UpdateText.setLayoutData(data);		
			
			Label label7 = new Label(this, SWT.NULL);
			label7.setText("    ");
			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			data.horizontalSpan=2;
			label7.setLayoutData(data);

			
			Label label8 = new Label(this, SWT.NULL);
			label8.setText(BUNDLE.getString("FieldMappingSQLEditor.labelDelete"));
			data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
			data.horizontalSpan=3;		
			label8.setLayoutData(data);
			
			
			isDelete= new Button(this, SWT.CHECK);
			isDelete.setText(BUNDLE.getString("FieldMappingSQLEditor.isInsert"));
			isDelete.setLayoutData(layout);
			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			data.heightHint = 26;
			isDelete.setLayoutData(data);

			Label label9 = new Label(this, SWT.NULL);
			label9.setText("    ");
			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			label9.setLayoutData(data);
			
			
			DeleteText = new Text(this,SWT.BORDER | SWT.MULTI);
			DeleteText.setBackground(new Color(null,255,255,255));
		    data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
			data.horizontalSpan=3;
			data.grabExcessHorizontalSpace=true;
			data.grabExcessVerticalSpace=true;
			DeleteText.setLayoutData(data);
			
			Label label10 = new Label(this, SWT.NULL);
			label10.setText("    ");
			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			data.horizontalSpan=2;
			label10.setLayoutData(data);

			
			Label label11 = new Label(this, SWT.NULL);
			label11.setText(BUNDLE.getString("FieldMappingSQLEditor.labelDeleteAll"));
			data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
			data.horizontalSpan=3;		
			label11.setLayoutData(data);
			
			
			isDeleteAll= new Button(this, SWT.CHECK);
			isDeleteAll.setText(BUNDLE.getString("FieldMappingSQLEditor.isInsert"));
			isDeleteAll.setLayoutData(layout);
			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			data.heightHint = 26;
			isDeleteAll.setLayoutData(data);

			Label label12 = new Label(this, SWT.NULL);
			label12.setText("    ");
			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			label12.setLayoutData(data);
			
			
			DeleteAllText = new Text(this,SWT.BORDER | SWT.MULTI);
			DeleteAllText.setBackground(new Color(null,255,255,255));
		    data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
			data.horizontalSpan=3;
			data.grabExcessHorizontalSpace=true;
			data.grabExcessVerticalSpace=true;
			DeleteAllText.setLayoutData(data);		
			
			
	}
	

	public void FormList(ICollectionMapping cm)
	{
		this.cm=cm;
		isInsert.setSelection(cm.isCustomInsertCallable());		
		isUpdate.setSelection(cm.isCustomUpdateCallable());
		isDelete.setSelection(cm.isCustomDeleteCallable());		
		isDeleteAll.setSelection(cm.isCustomDeleteAllCallable());
		
		if (cm.getCustomSQLInsert()==null)
			InsertText.setText("");
		else
			InsertText.setText(cm.getCustomSQLInsert());
		if (cm.getCustomSQLUpdate()==null)
			UpdateText.setText("");
		else
			UpdateText.setText(cm.getCustomSQLUpdate());
		if (cm.getCustomSQLDelete()==null)
			DeleteText.setText("");
		else
			DeleteText.setText(cm.getCustomSQLDelete());
		if (cm.getCustomSQLDeleteAll()==null)
			DeleteAllText.setText("");
		else
			DeleteAllText.setText(cm.getCustomSQLDeleteAll());
	}

	public void SetResaults()
	{	
   		cm.setCustomSQLUpdate(UpdateText.getText(),isUpdate.getSelection());
   		cm.setCustomSQLInsert(InsertText.getText(),isInsert.getSelection());
   		cm.setCustomSQLDelete(DeleteText.getText(),isDelete.getSelection());
   		cm.setCustomSQLDeleteAll(DeleteAllText.getText(),isDeleteAll.getSelection());
	}
	
}
