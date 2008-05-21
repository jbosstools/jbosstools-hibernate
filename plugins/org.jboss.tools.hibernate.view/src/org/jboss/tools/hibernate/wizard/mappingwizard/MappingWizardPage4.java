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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;



/**
 * @author kaa
 *
 * Inheritance mapping page
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MappingWizardPage4 extends WizardPage {
	public static final String BUNDLE_NAME = "mappingwizard"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(MappingWizardPage4.class.getPackage().getName() + "." + BUNDLE_NAME);	
    private Button HierarchyButton;
    private Button SubclassButton;
    private Button ConcreteButton;
	private Button UnionButton;
	private String old_active,new_active;
   
	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight=0;
		layout.horizontalSpacing=0;
		layout.verticalSpacing = 5;
		container.setLayout(layout);
		old_active=OrmConfiguration.TABLE_PER_HIERARCHY;
		new_active=OrmConfiguration.TABLE_PER_HIERARCHY;		
	    Group group = new Group(container, SWT.NONE);
	    group.setText(BUNDLE.getString("MappingWizardPage4.group"));
		GridLayout Grlayout = new GridLayout();
		Grlayout.numColumns = 1;
		//Grlayout.verticalSpacing=28;
		group.setLayout(Grlayout);
	    GridData groupData =   new GridData(SWT.FILL, SWT.FILL, true, true, 2, 7);
	    group.setLayoutData(groupData);
	    group.setBounds(0,0,convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_MARGIN));

		HierarchyButton = new Button(group, SWT.RADIO);
		HierarchyButton.setText(BUNDLE.getString("MappingWizardPage4.hierarchybutton"));
		HierarchyButton.addListener(SWT.Selection, listener);
		
		SubclassButton = new Button(group, SWT.RADIO);
		SubclassButton.setText(BUNDLE.getString("MappingWizardPage4.subclassbutton"));
		SubclassButton.addListener(SWT.Selection, listener);
		//SubclassButton.setSelection(true);
		
		ConcreteButton= new Button(group, SWT.RADIO);;
		GridData data= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING );
		ConcreteButton.setText(BUNDLE.getString("MappingWizardPage4.concretebutton"));
		ConcreteButton.setLayoutData(data);
		ConcreteButton.addListener(SWT.Selection, listener);

		UnionButton= new Button(group, SWT.RADIO);
		UnionButton.setText(BUNDLE.getString("MappingWizardPage4.unionbutton"));
		UnionButton.addListener(SWT.Selection, listener);
		


	
		setControl(container);		
		
	}
	Listener listener = new Listener() {
	    public void handleEvent(Event event) {
	        Button button = (Button) event.widget;
	        if (!button.getSelection()) return;
	        else SelectButton(button);
	    }};
		
	public MappingWizardPage4(IMapping mod){
		super("MappingWizardPage");
		setTitle(BUNDLE.getString("MappingWizardPage1.title"));
		setDescription(BUNDLE.getString("MappingWizardPage4.description")+"\n"+BUNDLE.getString("MappingWizardPage4.descriptionpage"));
	}

	public void SetActiveButton(String Active)
	{
		ConcreteButton.setSelection(false);
		HierarchyButton.setSelection(false);
		SubclassButton.setSelection(false);
		UnionButton.setSelection(false);
		if (Active.equals(OrmConfiguration.TABLE_PER_HIERARCHY))
			HierarchyButton.setSelection(true);
		if (Active.equals(OrmConfiguration.TABLE_PER_SUBCLASS))
			SubclassButton.setSelection(true);
		if (Active.equals(OrmConfiguration.TABLE_PER_CLASS))
			ConcreteButton.setSelection(true);
		if (Active.equals(OrmConfiguration.TABLE_PER_CLASS_UNION))
			UnionButton.setSelection(true);
		old_active=Active;
		new_active=Active;
	}

	private void SelectButton(Button button) {
		if (button==HierarchyButton)
			new_active=OrmConfiguration.TABLE_PER_HIERARCHY;
		else
		if (button==SubclassButton)
			new_active=OrmConfiguration.TABLE_PER_SUBCLASS;
		else
		if (button==ConcreteButton)
			new_active=OrmConfiguration.TABLE_PER_CLASS;
		else
			if (button==UnionButton)
				new_active=OrmConfiguration.TABLE_PER_CLASS_UNION;
	}
	
	
	public String getActiveButton()
	{
		if (old_active.equals(new_active))
			return "none";
		else return new_active;
	}	
}
