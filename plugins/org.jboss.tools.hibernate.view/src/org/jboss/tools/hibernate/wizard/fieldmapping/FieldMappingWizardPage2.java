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
import org.jboss.tools.hibernate.core.hibernate.IIndexedCollectionMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ComponentMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.IdBagMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ManyToManyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;


/**
 * @author kaa
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FieldMappingWizardPage2 extends WizardPage {
	public static final String BUNDLE_NAME = "fieldmapping"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(FieldMappingWizardPage2.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private IPersistentField field;	
    private Button SimpleValueButton;
    private Button ManyToManyButton;	
    private Button ComponentButton;
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
		String FieldType;

		label1 = new Label(container, SWT.NULL);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		label1.setLayoutData(data);
		
		Label label = new Label(container, SWT.NULL);
		label.setText(BUNDLE.getString("FieldMappingWizardPage2.label"));
		 data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		label.setLayoutData(data);

	    Group group = new Group(container, SWT.NONE);
		GridLayout Grlayout = new GridLayout();
		Grlayout.numColumns = 1;
		group.setLayout(Grlayout);
	    
	    group.setText(BUNDLE.getString("FieldMappingWizardPage2.group"));
	    GridData groupData =   new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
	    group.setLayoutData(groupData);
	    group.setBounds(0,0,convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_MARGIN));
		

		SimpleValueButton = new Button(group, SWT.RADIO);
		SimpleValueButton.setText(BUNDLE.getString("FieldMappingWizardPage2.simplevaluebutton"));
		SimpleValueButton.addListener(SWT.Selection, listener);
	    
		FieldType=field.getType();		
		if ((FieldType.equals("java.util.Map"))||(FieldType.equals("java.util.SortedMap"))||(FieldType.equals("java.util.Collection"))) //java.util.Map
		{

			ManyToManyButton = new Button(group, SWT.RADIO);
			ManyToManyButton.setText(BUNDLE.getString("FieldMappingWizardPage2.manytomanybutton"));
			ManyToManyButton.addListener(SWT.Selection, listener);
			
			ComponentButton= new Button(group, SWT.RADIO);
			ComponentButton.setText(BUNDLE.getString("FieldMappingWizardPage2.componentbutton"));
			ComponentButton.addListener(SWT.Selection, listener);

		}
		
		selected_mapping=FieldMappingWizard.S_NoMap;
		
		Refresh();
		setControl(container);		
	}
	
	Listener listener = new Listener() {
	    public void handleEvent(Event event) {
	        Button button = (Button) event.widget;
	        if (!button.getSelection()) return;
	        else SetMapping(button);			
	    }};	

		private void SetMapping(Button button) {
			if (button==SimpleValueButton)
				selected_mapping=FieldMappingWizard.S_SimpleValueMapping;
			else
			if (button==ManyToManyButton)
				selected_mapping=FieldMappingWizard.S_ManyToManyMapping;
			else
			if (button==ComponentButton)
				selected_mapping=FieldMappingWizard.S_ComponentMapping;
			getWizard().getContainer().updateButtons();
		}
	    
	public FieldMappingWizardPage2(IMapping mod, String persistentClassName,IPersistentField field){
		super("FieldMappingWizard");
		setTitle(BUNDLE.getString("FieldMappingWizardPage2.title"));
		setDescription(BUNDLE.getString("FieldMappingWizardPage2.description"));
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
	
	public void Refresh()
	{
		String FieldType;
		if ((field.getMapping()==null)||(field.getMapping().getPersistentValueMapping()==null)||
			((!(field.getMapping().getPersistentValueMapping() instanceof IIndexedCollectionMapping))&&
			(!(field.getMapping().getPersistentValueMapping() instanceof IdBagMapping))))
		{
		old_mapping=FieldMappingWizard.S_NoMap;
		FieldType="null";
		}
		else
		{
			IPersistentValueMapping elementmapping = null;
			if (field.getMapping().getPersistentValueMapping() instanceof IIndexedCollectionMapping)
			{
				elementmapping=((IIndexedCollectionMapping)(field.getMapping().getPersistentValueMapping())).getIndex();
				if ((!field.getType().equals("java.util.Map"))&&(!field.getType().equals("java.util.SortedMap"))) //java.util.Map
				{
					if (ManyToManyButton!=null)
					ManyToManyButton.setVisible(false);
					if (ComponentButton!=null)
					ComponentButton.setVisible(false);
				}
				else 
				{
					if (ManyToManyButton!=null)
					ManyToManyButton.setVisible(true);
					if (ComponentButton!=null)
					ComponentButton.setVisible(true);
				}
			}
			else if (field.getMapping().getPersistentValueMapping() instanceof IdBagMapping)
			{
				elementmapping=((IdBagMapping)(field.getMapping().getPersistentValueMapping())).getIdentifier();
				if (ManyToManyButton!=null)
				ManyToManyButton.setVisible(false);
				if (ComponentButton!=null)
				ComponentButton.setVisible(false);				
			}
			
			
			if (elementmapping==null)
			{
				old_mapping=FieldMappingWizard.S_NoMap;
				FieldType="null";
				SimpleValueButton.setSelection(true);
				selected_mapping=FieldMappingWizard.S_SimpleValueMapping;					
			}
			else
			{
				if (elementmapping instanceof ManyToManyMapping)
				{
					old_mapping=FieldMappingWizard.S_ManyToManyMapping;
					ManyToManyButton.setSelection(true);					
				}
				else
				if (elementmapping instanceof ComponentMapping)
				{
					old_mapping=FieldMappingWizard.S_ComponentMapping;
					ComponentButton.setSelection(true);					
				}
				else
					if (elementmapping instanceof SimpleValueMapping)
					{
						old_mapping=FieldMappingWizard.S_SimpleValueMapping;
						SimpleValueButton.setSelection(true);					
					}
					
			FieldType=elementmapping.getClass().getName();
		    FieldType=FieldType.substring(FieldType.lastIndexOf(".")+1,FieldType.length());
			}
		}
		if (old_mapping==FieldMappingWizard.S_SimpleValueMapping) FieldType=BUNDLE.getString("FieldMappingWizard.simple");
		label1.setText(BUNDLE.getString("FieldMappingWizardPage2.label1")+FieldType);
	}
	
	public boolean canFlipToNextPage() {
		if ((old_mapping==FieldMappingWizard.S_NoMap)&&(selected_mapping==FieldMappingWizard.S_NoMap))
			return false;
		else return true;
	}	
}
