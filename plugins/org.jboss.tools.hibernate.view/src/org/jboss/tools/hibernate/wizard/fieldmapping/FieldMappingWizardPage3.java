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

import java.util.Iterator;
import java.util.ResourceBundle;

import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IJoinMapping;
import org.jboss.tools.hibernate.internal.core.PersistentClass;
import org.jboss.tools.hibernate.internal.core.hibernate.AnyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.CollectionMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ComponentMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ManyToAnyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ManyToManyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ManyToOneMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.OneToManyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.OneToOneMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.PropertyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMappingHelper;
import org.jboss.tools.hibernate.internal.core.util.TypeUtils;


/**
 * @author kaa
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FieldMappingWizardPage3 extends WizardPage {
	public static final String BUNDLE_NAME = "fieldmapping"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(FieldMappingWizardPage3.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private IMapping mod;
	private String persistentClassName;
	private IPersistentField field;
    private Button SimpleValueButton;
    private Button OneToManyButton;
    private Button ManyToManyButton;	
    private Button ComponentButton;
	private Button AnyButton;
	private Button ManyToAnyButton;	
	private Button ManyToOneButton;	
	private Button OneToOneButton;
    private Button CheckButton;
    private boolean Check; 
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
		String MappingType;
		
		
		label1 = new Label(container, SWT.NULL);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		label1.setLayoutData(data);
		
		Label label = new Label(container, SWT.NULL);
		label.setText(BUNDLE.getString("FieldMappingWizardPage3.label"));
		 data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		label.setLayoutData(data);

	    Group group = new Group(container, SWT.NONE);
		GridLayout Grlayout = new GridLayout();
		Grlayout.numColumns = 1;
		group.setLayout(Grlayout);
	    
	    group.setText(BUNDLE.getString("FieldMappingWizardPage3.group"));
	    GridData groupData =   new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
	    group.setLayoutData(groupData);
	    group.setBounds(0,0,convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_MARGIN));
		
		MappingType=field.getType();		
		if (Signature.getArrayCount(MappingType) != 0)		
		{
			if (TypeUtils.javaTypeToHibType(Signature.getElementType(MappingType))==null)
			{
			OneToManyButton = new Button(group, SWT.RADIO);
			OneToManyButton.setText(BUNDLE.getString("FieldMappingWizardPage3.onetomanybutton"));
			OneToManyButton.addListener(SWT.Selection, listener);

			ManyToManyButton = new Button(group, SWT.RADIO);
			ManyToManyButton.setText(BUNDLE.getString("FieldMappingWizardPage3.manytomanybutton"));
			ManyToManyButton.addListener(SWT.Selection, listener);
			
			ComponentButton= new Button(group, SWT.RADIO);
			ComponentButton.setText(BUNDLE.getString("FieldMappingWizardPage3.componentbutton"));
			ComponentButton.addListener(SWT.Selection, listener);

			ManyToAnyButton= new Button(group, SWT.RADIO);
			ManyToAnyButton.setText(BUNDLE.getString("FieldMappingWizardPage3.manytoanybutton"));
			ManyToAnyButton.addListener(SWT.Selection, listener);
			}
		}
		else
		if ((MappingType.equals("java.util.Map"))||(MappingType.equals("java.util.SortedMap"))) //java.util.Map
		{
		
			OneToManyButton = new Button(group, SWT.RADIO);
			OneToManyButton.setText(BUNDLE.getString("FieldMappingWizardPage3.onetomanybutton"));
			OneToManyButton.addListener(SWT.Selection, listener);

			ManyToManyButton = new Button(group, SWT.RADIO);
			ManyToManyButton.setText(BUNDLE.getString("FieldMappingWizardPage3.manytomanybutton"));
			ManyToManyButton.addListener(SWT.Selection, listener);
			
			ComponentButton= new Button(group, SWT.RADIO);
			ComponentButton.setText(BUNDLE.getString("FieldMappingWizardPage3.componentbutton"));
			ComponentButton.addListener(SWT.Selection, listener);

			ManyToAnyButton= new Button(group, SWT.RADIO);
			ManyToAnyButton.setText(BUNDLE.getString("FieldMappingWizardPage3.manytoanybutton"));
			ManyToAnyButton.addListener(SWT.Selection, listener);
			
		}
		else
		if (MappingType.equals("java.util.Collection")) //java.util.Collection (Bag,idBag)
		{
			
			OneToManyButton = new Button(group, SWT.RADIO);
			OneToManyButton.setText(BUNDLE.getString("FieldMappingWizardPage3.onetomanybutton"));
			OneToManyButton.addListener(SWT.Selection, listener);

			ManyToManyButton = new Button(group, SWT.RADIO);
			ManyToManyButton.setText(BUNDLE.getString("FieldMappingWizardPage3.manytomanybutton"));
			ManyToManyButton.addListener(SWT.Selection, listener);
			
			ComponentButton= new Button(group, SWT.RADIO);
			ComponentButton.setText(BUNDLE.getString("FieldMappingWizardPage3.componentbutton"));
			ComponentButton.addListener(SWT.Selection, listener);

			ManyToAnyButton= new Button(group, SWT.RADIO);
			ManyToAnyButton.setText(BUNDLE.getString("FieldMappingWizardPage3.manytoanybutton"));
			ManyToAnyButton.addListener(SWT.Selection, listener);
			
		}
		else
		if ((MappingType.equals("java.util.Set"))|| (MappingType.equals("java.util.SortedSet"))) //java.util.Set
		{
			
			OneToManyButton = new Button(group, SWT.RADIO);
			OneToManyButton.setText(BUNDLE.getString("FieldMappingWizardPage3.onetomanybutton"));
			OneToManyButton.addListener(SWT.Selection, listener);

			ManyToManyButton = new Button(group, SWT.RADIO);
			ManyToManyButton.setText(BUNDLE.getString("FieldMappingWizardPage3.manytomanybutton"));
			ManyToManyButton.addListener(SWT.Selection, listener);
			
			ComponentButton= new Button(group, SWT.RADIO);
			ComponentButton.setText(BUNDLE.getString("FieldMappingWizardPage3.componentbutton"));
			ComponentButton.addListener(SWT.Selection, listener);

			ManyToAnyButton= new Button(group, SWT.RADIO);
			ManyToAnyButton.setText(BUNDLE.getString("FieldMappingWizardPage3.manytoanybutton"));
			ManyToAnyButton.addListener(SWT.Selection, listener);
			
	
		}
		else
		if (MappingType.equals("java.util.List")) //java.util.List
		{
			
			OneToManyButton = new Button(group, SWT.RADIO);
			OneToManyButton.setText(BUNDLE.getString("FieldMappingWizardPage3.onetomanybutton"));
			OneToManyButton.addListener(SWT.Selection, listener);

			ManyToManyButton = new Button(group, SWT.RADIO);
			ManyToManyButton.setText(BUNDLE.getString("FieldMappingWizardPage3.manytomanybutton"));
			ManyToManyButton.addListener(SWT.Selection, listener);
			
			ComponentButton= new Button(group, SWT.RADIO);
			ComponentButton.setText(BUNDLE.getString("FieldMappingWizardPage3.componentbutton"));
			ComponentButton.addListener(SWT.Selection, listener);

			ManyToAnyButton= new Button(group, SWT.RADIO);
			ManyToAnyButton.setText(BUNDLE.getString("FieldMappingWizardPage3.manytoanybutton"));
			ManyToAnyButton.addListener(SWT.Selection, listener);
			
		}
		else
		if (mod.findClass(MappingType)!=null) //PersistentClass
		{
			
			ManyToOneButton= new Button(group, SWT.RADIO);
			ManyToOneButton.setText(BUNDLE.getString("FieldMappingWizardPage3.manytoonebutton"));
			ManyToOneButton.addListener(SWT.Selection, listener);

			
			if (!((FieldMappingWizard)getWizard()).isIsindexcollection())//not use this mappings if 
				//field inside component mapping of index collection 
			{
			OneToOneButton= new Button(group, SWT.RADIO);
			OneToOneButton.setText(BUNDLE.getString("FieldMappingWizardPage3.onetoonebutton"));
			OneToOneButton.addListener(SWT.Selection, listener);
			
			ComponentButton= new Button(group, SWT.RADIO);
			ComponentButton.setText(BUNDLE.getString("FieldMappingWizardPage3.componentbutton"));
			ComponentButton.addListener(SWT.Selection, listener);

			AnyButton= new Button(group, SWT.RADIO);
			AnyButton.setText(BUNDLE.getString("FieldMappingWizardPage3.anybutton"));
			AnyButton.addListener(SWT.Selection, listener);
			}
			
		}
		else if (TypeUtils.javaTypeToHibType(MappingType)==null)//other
		{
			if (!((FieldMappingWizard)getWizard()).isIsindexcollection())//not use this mappings if 
				//field inside component mapping of index collection 
			{
			ComponentButton= new Button(group, SWT.RADIO);
			ComponentButton.setText(BUNDLE.getString("FieldMappingWizardPage3.componentbutton"));
			ComponentButton.addListener(SWT.Selection, listener);
			}
			
			ManyToOneButton= new Button(group, SWT.RADIO);
			ManyToOneButton.setText(BUNDLE.getString("FieldMappingWizardPage3.manytoonebutton"));
			ManyToOneButton.addListener(SWT.Selection, listener);

			if (!((FieldMappingWizard)getWizard()).isIsindexcollection())//not use this mappings if 
				//field inside component mapping of index collection 
			{
			OneToOneButton= new Button(group, SWT.RADIO);
			OneToOneButton.setText(BUNDLE.getString("FieldMappingWizardPage3.onetoonebutton"));
			OneToOneButton.addListener(SWT.Selection, listener);
			
			AnyButton= new Button(group, SWT.RADIO);
			AnyButton.setText(BUNDLE.getString("FieldMappingWizardPage3.anybutton"));
			AnyButton.addListener(SWT.Selection, listener);
			}
		}
		SimpleValueButton = new Button(group, SWT.RADIO);
		SimpleValueButton.setText(BUNDLE.getString("FieldMappingWizardPage3.simplevaluebutton"));
		SimpleValueButton.addListener(SWT.Selection, listener);

		
		CheckButton= new Button(container, SWT.CHECK);
		CheckButton.setText(BUNDLE.getString("FieldMappingWizardPage3.checkbutton"));

		CheckButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DoCheck();
			}
		});
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
	    data.horizontalSpan = 1;
	    CheckButton.setLayoutData(data);
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
			if (button==OneToManyButton)
				selected_mapping=FieldMappingWizard.S_OneToManyMapping;
			else
			if (button==ManyToManyButton)
				selected_mapping=FieldMappingWizard.S_ManyToManyMapping;
			else
			if (button==ComponentButton)
				selected_mapping=FieldMappingWizard.S_ComponentMapping;
			else
			if (button==AnyButton)
				selected_mapping=FieldMappingWizard.S_AnyMapping;
			else
			if (button==ManyToAnyButton)
				selected_mapping=FieldMappingWizard.S_ManyToAnyMapping;
			else
			if (button==ManyToOneButton)
				selected_mapping=FieldMappingWizard.S_ManyToOneMapping;
			else
			if (button==OneToOneButton)
				selected_mapping=FieldMappingWizard.S_OneToOneMapping;
			getWizard().getContainer().updateButtons();
		}
		
		
	public FieldMappingWizardPage3(IMapping mod, String persistentClassName,IPersistentField field){
		super("FieldMappingWizard");
		setTitle(BUNDLE.getString("FieldMappingWizardPage3.title"));
		setDescription(BUNDLE.getString("FieldMappingWizardPage3.description"));
		this.persistentClassName=persistentClassName;
		this.field=field;
		this.mod = mod;
	}
	
	private void DoCheck() {
		Check=CheckButton.getSelection();
		if (!Check) ((FieldMappingWizard)getWizard()).setIsfinish(true);
	}	

	public boolean isJoinedTable() {
		return Check;
	}
	private void JoinedBoxEnable(boolean isCollection, int mappingType) {
		boolean resault=false;
		if (!isCollection)
		{
		if (((IHibernateClassMapping)(mod.findClass(persistentClassName).getPersistentClassMapping())).isClass() ||
				((IHibernateClassMapping)(mod.findClass(persistentClassName).getPersistentClassMapping())).isSubclass())
			resault=true;
		if ((mappingType==FieldMappingWizard.S_SimpleValueMapping) || (mappingType==FieldMappingWizard.S_ComponentMapping) ||
				(mappingType==FieldMappingWizard.S_AnyMapping) || (mappingType==FieldMappingWizard.S_ManyToOneMapping))
			resault=true;
		}	
		CheckButton.setEnabled(resault);
		if (!CheckButton.getEnabled()) 
			{
			CheckButton.setSelection(false);
			DoCheck();
			}
		else
		{//verify if there is join mapping
			boolean delmapping=false;
			Iterator iter =((IHibernateClassMapping)(mod.findClass(persistentClassName).getPersistentClassMapping())).getJoinIterator();			
			while ( iter.hasNext() ) {
				IJoinMapping jm =(IJoinMapping)iter.next();
				 Iterator propiterator=jm.getPropertyIterator();
				  while ( propiterator.hasNext() ) {
					  if (((PropertyMapping)propiterator.next()).getPersistentField().getName().equals(field.getName()))//find nessesarry join
					  {
						  CheckButton.setSelection(true);
						  // #added# by Konstantin Mishin on 19.12.2005 fixed for ESORM-414
						  CheckButton.setEnabled(false);
						  // #added#
						  DoCheck();
						  delmapping=true;
						  break;
					  }
				  }
				  if (delmapping) break;
			}
			
		}
	}
	public int getMappingDo() {
		// #changed# by Konstantin Mishin on 13.12.2005 fixed for ESORM-407
//		if (old_mapping==FieldMappingWizard.S_NoMap)
//			return selected_mapping;
//		else
//		{
//			if (old_mapping==selected_mapping)
//				return FieldMappingWizard.S_NoMap;
//			if (old_mapping==selected_mapping)
//				return FieldMappingWizard.S_NoMap;
//			else return selected_mapping;
//			}
		if (selected_mapping == FieldMappingWizard.S_NoMap)
			return old_mapping;
		else
			return selected_mapping;
		// #changed#
			
	}
	
//	public void setMappingDo(int old_mapping) {
//		this.old_mapping=old_mapping;
//}
	public boolean canFlipToNextPage() {
		if ((old_mapping==FieldMappingWizard.S_NoMap)&&(selected_mapping==FieldMappingWizard.S_NoMap))
			return false;
		else return true;
	}
	
	public void Refresh()
	{
		// #added# by Konstantin Mishin on 05.12.2005 fixed for ESORM-337
		SimpleValueButton.setSelection(false);
		if (OneToManyButton!=null)
			OneToManyButton.setSelection(false);
		if (ManyToManyButton!=null)
			ManyToManyButton.setSelection(false);
		if (ManyToOneButton!=null)
			ManyToOneButton.setSelection(false);
		if (OneToOneButton!=null)
			OneToOneButton.setSelection(false);
		if (ComponentButton!=null)
			ComponentButton.setSelection(false);
		if (AnyButton!=null)
			AnyButton.setSelection(false);
		if (ManyToAnyButton!=null)
			ManyToAnyButton.setSelection(false);
		// #added#
		
		String FieldType,MappingType;
		boolean isCollection=false;
		MappingType=field.getType();
		selected_mapping=FieldMappingWizard.S_NoMap;
		FieldType=((ClassMapping)((PersistentClass)mod.findClass(persistentClassName)).getPersistentClassMapping()).getClassName();
		
		// edit tau 28.01.2006 /ESORM-499 for [][]...
		/*
		if ((field.getMapping()==null)
				||((HibernateAutoMappingHelper.getLinkedType(MappingType) != 0)
						&&(field.getMapping().getPersistentValueMapping()!=null)
						&&((CollectionMapping)(field.getMapping().getPersistentValueMapping())).getElement()==null))
		*/
		
		if ((field.getMapping()==null)
				||((HibernateAutoMappingHelper.getLinkedType(MappingType) != 0)
						&&(field.getMapping().getPersistentValueMapping()!=null)
						&&(field.getMapping().getPersistentValueMapping()instanceof CollectionMapping) &&						
							((CollectionMapping)(field.getMapping().getPersistentValueMapping())).getElement()==null))
			
		{
		old_mapping=FieldMappingWizard.S_NoMap;			
		FieldType="null";
		// #deleted# by Konstantin Mishin on 05.12.2005 fixed for ESORM-337
//		SimpleValueButton.setSelection(false);
//		if (OneToManyButton!=null)
//			OneToManyButton.setSelection(false);
//		if (ManyToManyButton!=null)
//			ManyToManyButton.setSelection(false);
//		if (ManyToOneButton!=null)
//			ManyToOneButton.setSelection(false);
//		if (OneToOneButton!=null)
//			OneToOneButton.setSelection(false);
//		if (ComponentButton!=null)
//			ComponentButton.setSelection(false);
//		if (AnyButton!=null)
//			AnyButton.setSelection(false);
//		if (ManyToAnyButton!=null)
//			ManyToAnyButton.setSelection(false);	
		// #deleted#
		}
		else
		{
			IPersistentValueMapping elementmapping;
			if (HibernateAutoMappingHelper.getLinkedType(MappingType) != 0)
			{
				if (field.getMapping().getPersistentValueMapping() instanceof CollectionMapping)
				elementmapping=((CollectionMapping)(field.getMapping().getPersistentValueMapping())).getElement();
				else elementmapping=null;
				isCollection=true;
			}
			else elementmapping=field.getMapping().getPersistentValueMapping();
		if (elementmapping==null)
		{
			old_mapping=FieldMappingWizard.S_NoMap;
			FieldType="null";
			// #deleted# by Konstantin Mishin on 05.12.2005 fixed for ESORM-337
//			SimpleValueButton.setSelection(false);
			// #deleted#
			if (OneToManyButton!=null)
			{
				OneToManyButton.setSelection(true);
				selected_mapping=FieldMappingWizard.S_OneToManyMapping;
			}
			// #deleted# by Konstantin Mishin on 05.12.2005 fixed for ESORM-337
//			if (ManyToManyButton!=null)
//				ManyToManyButton.setSelection(false);
//			if (ManyToOneButton!=null)
//				ManyToOneButton.setSelection(false);
//			if (OneToOneButton!=null)
//				OneToOneButton.setSelection(false);
//			if (ComponentButton!=null)
//				ComponentButton.setSelection(false);
//			if (AnyButton!=null)
//				AnyButton.setSelection(false);
//			if (ManyToAnyButton!=null)
//				ManyToAnyButton.setSelection(false);		
			// #deleted#

			if (selected_mapping==FieldMappingWizard.S_NoMap)
			{
				SimpleValueButton.setSelection(true);
				selected_mapping=FieldMappingWizard.S_SimpleValueMapping;
			}
		}
		else
		{
			if (elementmapping instanceof OneToManyMapping)
			{
				if (OneToManyButton!=null)
				OneToManyButton.setSelection(true);
				old_mapping=FieldMappingWizard.S_OneToManyMapping;
			}
			else
			if (elementmapping instanceof ManyToManyMapping)
			{
				if (ManyToManyButton!=null)
					ManyToManyButton.setSelection(true);
				old_mapping=FieldMappingWizard.S_ManyToManyMapping;
			}
			else
			if (elementmapping instanceof ManyToOneMapping)
			{
				if (ManyToOneButton!=null)
					ManyToOneButton.setSelection(true);
				old_mapping=FieldMappingWizard.S_ManyToOneMapping;
			}
			else
			if (elementmapping instanceof OneToOneMapping)
			{
				if (OneToOneButton!=null)
					OneToOneButton.setSelection(true);
				old_mapping=FieldMappingWizard.S_OneToOneMapping;
			}
			else
			if (elementmapping instanceof ComponentMapping)
			{
				if (ComponentButton!=null)
					ComponentButton.setSelection(true);
				old_mapping=FieldMappingWizard.S_ComponentMapping;
			}
			else
			if (elementmapping instanceof ManyToAnyMapping)
			{
				if (ManyToAnyButton!=null)
					ManyToAnyButton.setSelection(true);
				old_mapping=FieldMappingWizard.S_ManyToAnyMapping;
			}
			else
			if (elementmapping instanceof AnyMapping)
			{
				if (AnyButton!=null)
					AnyButton.setSelection(true);
				old_mapping=FieldMappingWizard.S_AnyMapping;
			}
			else
				if (elementmapping instanceof SimpleValueMapping)
				{
					SimpleValueButton.setSelection(true);
					old_mapping=FieldMappingWizard.S_SimpleValueMapping;
				}
			else old_mapping=FieldMappingWizard.S_NoMap;
			
		FieldType=elementmapping.getClass().getName();
		FieldType=FieldType.substring(FieldType.lastIndexOf(".")+1,FieldType.length());
		}
		}

		if (old_mapping==FieldMappingWizard.S_SimpleValueMapping) FieldType=BUNDLE.getString("FieldMappingWizard.simple");
		label1.setText(BUNDLE.getString("FieldMappingWizardPage3.label1")+FieldType);
		JoinedBoxEnable(isCollection,old_mapping);
	}
	
}
