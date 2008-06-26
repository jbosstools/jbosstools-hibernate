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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.BagMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.IdBagMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ListMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SetMapping;


/**
 * @author kaa
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FieldMappingWizardPage1 extends WizardPage {
	public static final String BUNDLE_NAME = "fieldmapping"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(FieldMappingWizardPage1.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private IPersistentField field;
	private Button ListButton;
	private Button SetButton;
	private Button BagButton;
	private Button IdBagButton;
    private int old_mapping;
	private int selected_mapping;
	private Label label1;
	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight=0;
		layout.horizontalSpacing=0;
		layout.verticalSpacing = 5;
		container.setLayout(layout);
		
		
		label1 = new Label(container, SWT.NULL);

		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		label1.setLayoutData(data);

		
		Label label = new Label(container, SWT.NULL);
		label.setText(BUNDLE.getString("FieldMappingWizardPage1.label"));
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		label.setLayoutData(data);
		
	    Group group = new Group(container, SWT.NONE);
		GridLayout Grlayout = new GridLayout();
		Grlayout.numColumns = 1;
		group.setLayout(Grlayout);
	    
	    group.setText(BUNDLE.getString("FieldMappingWizardPage1.group"));
	    GridData groupData =   new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
	    group.setLayoutData(groupData);
	    group.setBounds(0,0,convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_MARGIN));
		
		ListButton= new Button(group, SWT.RADIO);
		ListButton.setText(BUNDLE.getString("FieldMappingWizardPage1.listbutton"));
		ListButton.addListener(SWT.Selection, listener);

		SetButton= new Button(group, SWT.RADIO);
		SetButton.setText(BUNDLE.getString("FieldMappingWizardPage1.setbutton"));
		SetButton.addListener(SWT.Selection, listener);
		
		BagButton= new Button(group, SWT.RADIO);
		BagButton.setText(BUNDLE.getString("FieldMappingWizardPage1.bagbutton"));
		BagButton.addListener(SWT.Selection, listener);

		
		IdBagButton= new Button(group, SWT.RADIO);
		IdBagButton.setText(BUNDLE.getString("FieldMappingWizardPage1.idbagbutton"));
		IdBagButton.addListener(SWT.Selection, listener);
		
		
		
		Refresh();
		setControl(container);		
	}
	
	Listener listener = new Listener() {
	    public void handleEvent(Event event) {
	        Button button = (Button) event.widget;
	        if (!button.getSelection()) return;
	        else SetMapping(button);
	    }};	
	    
	public FieldMappingWizardPage1(IMapping mod, String persistentClassName, IPersistentField field){
		super("FieldMappingWizard");
		setTitle(BUNDLE.getString("FieldMappingWizardPage1.title"));
		setDescription(BUNDLE.getString("FieldMappingWizardPage1.description"));
		this.field=field;
	}

	public int getMappingDo() {
		if (old_mapping==FieldMappingWizard.S_NoMap)
			return selected_mapping;
		else
		{
			if (old_mapping==selected_mapping)
				return FieldMappingWizard.S_NoMap;
			else return selected_mapping;
			}
	}

	private void SetMapping(Button button) {
		if (button==ListButton)
			selected_mapping=FieldMappingWizard.S_ListMapping;
		else
		if (button==SetButton)
			selected_mapping=FieldMappingWizard.S_SetMapping;
		else
		if (button==BagButton)
			selected_mapping=FieldMappingWizard.S_BagMapping;
		else
		if (button==IdBagButton)
			selected_mapping=FieldMappingWizard.S_IdBagMapping;
		getWizard().getContainer().updateButtons();
		
	}
	
	public void Refresh()
	{

		String FieldType;
		selected_mapping=FieldMappingWizard.S_NoMap;
		if ((field.getMapping()==null) ||
				(field.getMapping().getPersistentValueMapping())==null)		
		{
		FieldType="null";
		old_mapping=FieldMappingWizard.S_NoMap;
		}
		else
		{
		IPersistentValueMapping MapType=(field.getMapping().getPersistentValueMapping());
			

		if (MapType instanceof ListMapping)
		{
			old_mapping=FieldMappingWizard.S_ListMapping;
			ListButton.setSelection(true);
		}
		else
		if (MapType instanceof SetMapping)
		{
			old_mapping=FieldMappingWizard.S_SetMapping;
			SetButton.setSelection(true);
		}
		else
		if (MapType instanceof BagMapping)
		{
			old_mapping=FieldMappingWizard.S_BagMapping;
			BagButton.setSelection(true);			
		}
		else
		if (MapType instanceof IdBagMapping)
		{
			old_mapping=FieldMappingWizard.S_IdBagMapping;
			IdBagButton.setSelection(true);			
		}
		else old_mapping=FieldMappingWizard.S_NoMap;
			
		FieldType=(field.getMapping().getPersistentValueMapping().getClass().getName());
		FieldType=FieldType.substring(FieldType.lastIndexOf(".")+1,FieldType.length());
		}
		
		label1.setText(BUNDLE.getString("FieldMappingWizardPage1.label1")+FieldType);		
		
		
	}
	
	public boolean canFlipToNextPage() {
		if ((old_mapping==FieldMappingWizard.S_NoMap)&&(selected_mapping==FieldMappingWizard.S_NoMap))
			return false;
		else return true;
	}
	
}
