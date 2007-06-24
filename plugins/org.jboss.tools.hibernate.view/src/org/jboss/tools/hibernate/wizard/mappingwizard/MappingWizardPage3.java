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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.ISimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.hibernate.ComponentMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.PropertyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;


/**
 * @author kaa
 *
 * Persistent class identification page
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MappingWizardPage3 extends WizardPage {
	public static final String BUNDLE_NAME = "mappingwizard"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(MappingWizardPage3.class.getPackage().getName() + "." + BUNDLE_NAME);	
    private Button AddButton;
    private Button RemoveButton;    
    private List list;
	private IMapping mod;
    private String SelectedPC;
    private boolean dirty=false;
	public class AddDlg extends ListDialog{//New dialog to add Composite ID property
		private List FieldList,NewColumnList;
	    private Combo ClassList,Newtext,Lenghttext,PrimaryColumn;
	    private Button RemoveColumnButton,AddColumnButton;
 	    public AddDlg(Shell parent) {
 			super(parent);
 			this.setTitle(BUNDLE.getString("MappingWizardPage3.addtitle"));
 		}
 	    
 	    protected Control createDialogArea(Composite parent) {
 	        Composite root = new Composite(parent, SWT.NULL);
			

 			GridLayout layout = new GridLayout();
			layout.numColumns=5;
 			root.setLayout(layout);
 			Label label1 = new Label(root, SWT.NULL);
 			label1.setText(BUNDLE.getString("MappingWizardPage3.combo"));
 			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
 			data.horizontalSpan = 1;
 			label1.setLayoutData(data);

 			Label label2 = new Label(root, SWT.NULL);
 			label2.setText("");
 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
 			data.horizontalSpan = 1;
 			label2.setLayoutData(data);

 			Label label3 = new Label(root, SWT.NULL);
 			label3.setText(BUNDLE.getString("MappingWizardPage3.unsaved-value"));
 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
 			data.horizontalSpan = 1;
 			label3.setLayoutData(data);

 			Label label4 = new Label(root, SWT.NULL);
 			label4.setText("");
 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
 			data.horizontalSpan = 1;
 			label4.setLayoutData(data);

			Label label5 = new Label(root, SWT.NULL);
 			label5.setText(BUNDLE.getString("MappingWizardPage3.access"));
 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
 			data.horizontalSpan = 1;
 			label5.setLayoutData(data);

 			ClassList = new Combo(root, SWT.READ_ONLY);
				Iterator iter = ((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getPropertyIterator();
 				//	(mod.findClass(SelectedPC)).getFields()[0].getMapping().getPersistentValueMapping().getTable().getColumnIterator();
				PropertyMapping newcol;
 				while ( iter.hasNext() ) {
				newcol=(PropertyMapping)iter.next();
 				if (isReadyForID(newcol)) 
				ClassList.add(newcol.getName());
 				}
 			
 			if (ClassList.getItemCount()>0)
			ClassList.setText(ClassList.getItem(0));
 			ClassList.addModifyListener(new ModifyListener()
 					{

						public void modifyText(ModifyEvent e) {
							SetData();
						}
						}
 			); 			
 			ClassList.setLayout(layout);
 			
 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
 			data.horizontalSpan = 1; 			
 			ClassList.setLayoutData(data);
 			
 			Label label6 = new Label(root, SWT.NULL);
 			label6.setText("");
 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
 			data.horizontalSpan = 1;
 			label6.setLayoutData(data);
 			
 			Newtext = new Combo(root, SWT.READ_ONLY);
 			Newtext.setItems(OrmConfiguration.ID_UNSAVED_VALUES);
 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING); 			
 			data.horizontalSpan = 1; 			

 			Newtext.setLayoutData(data);

			Label label7 = new Label(root, SWT.NULL);
 			label7.setText("");
 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
 			data.horizontalSpan = 1;
 			label7.setLayoutData(data);
 			
 			Lenghttext= new Combo(root, SWT.NULL);
 			Lenghttext.setItems(OrmConfiguration.ACCESS_VALUES);
 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING); 			
 			data.horizontalSpan = 1; 			
 			Lenghttext.setLayoutData(data);
			
	 			Label label8 = new Label(root, SWT.NULL);
	 			label8.setText(BUNDLE.getString("MappingWizardPage3.fieldlist"));
	 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
	 			data.horizontalSpan = 3;
	 			label8.setLayoutData(data);

	 			Label label11 = new Label(root, SWT.NULL);
	 			label11.setText("");
	 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
	 			data.horizontalSpan = 1;
	 			label11.setLayoutData(data);

				Label label12 = new Label(root, SWT.NULL);
	 			label12.setText(BUNDLE.getString("MappingWizardPage3.pkcolumn"));
	 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
	 			data.horizontalSpan = 1;
	 			label12.setLayoutData(data);

				FieldList = new List(root,SWT.BORDER);
				FieldList.setBackground(new Color(null,255,255,255));
		        data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
				data.horizontalSpan = 3;
				data.verticalSpan = 2;		
				data.grabExcessVerticalSpace = true;
				data.grabExcessHorizontalSpace = true;
				FieldList.setLayoutData(data);
	 			Label label14 = new Label(root, SWT.NULL);
	 			label14.setText("");
	 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
	 			data.horizontalSpan = 1;
	 			label14.setLayoutData(data);
				
				NewColumnList = new List(root,SWT.BORDER);
				NewColumnList.setBackground(new Color(null,255,255,255));
		        data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
				data.horizontalSpan = 1;
				data.verticalSpan = 2;		
				data.grabExcessVerticalSpace = true;
				data.grabExcessHorizontalSpace = true;
				NewColumnList.setEnabled(false);
				NewColumnList.setLayoutData(data);
				
				Label label15 = new Label(root, SWT.NULL);
	 			label15.setText(BUNDLE.getString("MappingWizardPage3.pkcombo"));
	 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
	 			data.horizontalSpan = 5;
	 			label15.setLayoutData(data);
				
	 			PrimaryColumn = new Combo(root, SWT.READ_ONLY);
	 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);	 			
				data.horizontalSpan = 1;
				PrimaryColumn.setLayoutData(data);
				PrimaryColumn.addModifyListener(new ModifyListener()
				{

			public void modifyText(ModifyEvent e) {
				if (getButton(IDialogConstants.OK_ID)!=null)
				if (PrimaryColumn.getItemCount()==0)
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				else getButton(IDialogConstants.OK_ID).setEnabled(false);				
			}
	}
);
	 			Label label16 = new Label(root, SWT.NULL);
	 			label16.setText("");
	 			data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
	 			data.horizontalSpan = 1;
	 			label16.setLayoutData(data);
				
		        AddColumnButton= new Button(root, SWT.PUSH);

				AddColumnButton.setText(BUNDLE.getString("MappingWizardPage3.addcolumn"));
				AddColumnButton.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						AddMapping();
					}
				});
	 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);				
			    data.horizontalSpan = 1;
				AddColumnButton.setLayoutData(data);
				Label label17 = new Label(root, SWT.NULL);
				label17.setText("    ");
				data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
				data.horizontalSpan = 1;
				label17.setLayoutData(data);
			    
		        RemoveColumnButton= new Button(root, SWT.PUSH);
				RemoveColumnButton.setText(BUNDLE.getString("MappingWizardPage3.removecolumn"));
				RemoveColumnButton.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						RemoveMapping();
					}
				});
	 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);				
				data.horizontalSpan = 1;
				RemoveColumnButton.setLayoutData(data);

				Label label13 = new Label(root, SWT.NULL);
	 			label13.setText(BUNDLE.getString("MappingWizardPage3.adddescriptionmapping"));
	 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
	 			data.horizontalSpan = 4;
	 			label13.setLayoutData(data);

				Label label10 = new Label(root, SWT.NULL);
	 			label10.setText(BUNDLE.getString("MappingWizardPage3.removedescriptionmapping"));
	 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
	 			data.horizontalSpan = 1;
	 			label10.setLayoutData(data);
				
				SetData();
				
 	      return root;
 	    }

		protected void createButtonsForButtonBar(Composite parent) {
			super.createButtonsForButtonBar(parent);
			this.getButton(IDialogConstants.OK_ID).setEnabled(false);			
		}
 	    
 	    protected void okPressed() {
 	    	if (PrimaryColumn.getItemCount()==0/*(NewColumnList.indexOf(" "+BUNDLE.getString("MappingWizardPage3.adddlgnomaping"))<0)&&(NewColumnList.getItemCount()>0)*/)
 	    	{
	    	Table table=((Table)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDatabaseTable());
            ComponentMapping RootMapping = (ComponentMapping)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getProperty(ClassList.getText()).getValue();
			ArrayList<String> OldFields = new ArrayList<String>(), PKList=new ArrayList<String>();
			Iterator iter = ((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDatabaseTable().getPrimaryKey().getColumnIterator();
 			while (iter.hasNext() ) {
 				PKList.add(((Column)iter.next()).getName());
 			}
			
			iter = RootMapping.getPropertyIterator();
 			while (iter.hasNext()) {
				PropertyMapping elem=(PropertyMapping)iter.next();
		        Column newcol=(Column)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDatabaseTable().getColumn(NewColumnList.getItem(FieldList.indexOf(elem.getName())));
				if (!PKList.contains(((Column)elem.getColumnIterator().next()).getName()))
					OldFields.add(((Column)elem.getColumnIterator().next()).getName());
				if (newcol!=null)
				{
		        SimpleValueMapping fieldmapping = new SimpleValueMapping();
		        fieldmapping.setTable(table);
		        fieldmapping.setName(elem.getName());
		        fieldmapping.setType(elem.getValue().getType());
		        fieldmapping.addColumn(newcol);
	 	        newcol.setPersistentValueMapping(fieldmapping);
				elem.setValue(fieldmapping);	
				}
				else elem.setValue(null);
				}
			
			((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).setIdentifierProperty(((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getProperty(ClassList.getText()));
			((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).setIdentifier(RootMapping);
			if (OldFields.size()>0)
			{
		 		if (MessageDialog.openConfirm(getShell(), BUNDLE.getString("MappingWizardPage3.delcolumntitle"), BUNDLE.getString("MappingWizardPage3.delcolumndescr")))
				{
					iter=OldFields.iterator();
		 			while ( iter.hasNext() ) {
					   table.removeColumn(((Column)table.getColumn(iter.next().toString())).getName());	
		 			}
	
				}
			}
			((ComponentMapping)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getProperty(ClassList.getText()).getValue()).setNullValue(Newtext.getText());
			((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getProperty(ClassList.getText()).setPropertyAccessorName(Lenghttext.getText());
			setDirty(true);
 			setReturnCode(OK);
			this.close();
	    	}
       
 	}

		private void ReloadPKColumn()
		{
			PrimaryColumn.removeAll();
			Iterator iter=((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDatabaseTable().getPrimaryKey().getColumnIterator();
 			while ( iter.hasNext() ) {
				Column newcol=(Column)iter.next();
				if (NewColumnList.indexOf(newcol.getName())<0)
				PrimaryColumn.add(newcol.getName());
 			}
			if (PrimaryColumn.getItemCount()>0)
				{
				PrimaryColumn.setEnabled(true);
				AddColumnButton.setEnabled(true);
				PrimaryColumn.setText(PrimaryColumn.getItem(0));
				}
			else 
			{
				AddColumnButton.setEnabled(false);
				PrimaryColumn.setEnabled(false);	
			}
			
		}
		
		private void SetData()
		{
			if (ClassList.getItemCount()>0)
			{
			ComponentMapping RootMapping = (ComponentMapping)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getProperty(ClassList.getText()).getValue();
			FieldList.removeAll();
			Iterator iter=RootMapping.getPropertyIterator();
 			while ( iter.hasNext() ) {
				PropertyMapping elem=(PropertyMapping)iter.next();
				FieldList.add(elem.getName());
						//+"->"+((Column)elem.getColumnIterator().next()).getName());
				NewColumnList.add(" "+BUNDLE.getString("MappingWizardPage3.adddlgnomaping"));
			}
			
 			if (((ComponentMapping)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getProperty(ClassList.getText()).getValue()).getNullValue()==null)
 				Newtext.setText(OrmConfiguration.DEFAULT_ID_UNSAVED);	
 			else	
 				Newtext.setText(((ComponentMapping)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getProperty(ClassList.getText()).getValue()).getNullValue());
 			if (((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getProperty(ClassList.getText()).getPropertyAccessorName()==null)
 				Lenghttext.setText(OrmConfiguration.DEFAULT_ACCESS);
 			else
 				Lenghttext.setText(((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getProperty(ClassList.getText()).getPropertyAccessorName());	 			
			
			// #added# by Konstantin Mishin on 23.11.2005 fixed for ESORM-333
			ReloadPKColumn();
			// #added#
			}
			else 
				{
				MessageDialog.openInformation(getShell(), BUNDLE.getString("MappingWizardPage3.informtitle"),BUNDLE.getString("MappingWizardPage3.informmessage")+((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDatabaseTable().getPrimaryKey().getColumnSpan()+" elements");
				setReturnCode(CANCEL);
				this.close();
		    	}
				// #deleted# by Konstantin Mishin on 23.11.2005 fixed for ESORM-333
				// ReloadPKColumn();
				// #deleted#
		}
		
		private void AddMapping()
		{
		 	if (FieldList.getSelectionCount()==1) 
		 	{
	 		if (MessageDialog.openConfirm(getShell(), BUNDLE.getString("MappingWizardPage3.adddlgaddtitle"), BUNDLE.getString("MappingWizardPage3.adddlgadddescr")+FieldList.getItem(FieldList.getSelectionIndex())))
	 		{
					NewColumnList.setItem(FieldList.getSelectionIndex(),PrimaryColumn.getText());
	 		}
	 		}
			ReloadPKColumn();
		}

		private void RemoveMapping()
		{
		 	if (FieldList.getSelectionCount()==1) 
		 	{
	 		if (MessageDialog.openConfirm(getShell(), BUNDLE.getString("MappingWizardPage3.adddlgdeltitle"), BUNDLE.getString("MappingWizardPage3.adddlgdeldescr")+NewColumnList.getItem(FieldList.getSelectionIndex())))
	 		{
				NewColumnList.setItem(FieldList.getSelectionIndex()," "+BUNDLE.getString("MappingWizardPage3.adddlgnomaping"));
	 		}
	 		}
			ReloadPKColumn();
		}		
		
	}
	public class AddSimpleDlg extends ListDialog{//New dialog to Simple ID property
	    private Combo ClassList;
 	    public AddSimpleDlg(Shell parent) {
 			super(parent);
 			this.setTitle(BUNDLE.getString("MappingWizardPage3.addtitle"));
 		}
 	    
 	    protected Control createDialogArea(Composite parent) {
 	        Composite root = new Composite(parent, SWT.NULL);
 			GridLayout layout = new GridLayout();
 			root.setLayout(layout);
 			Label label1 = new Label(root, SWT.NULL);
 			label1.setText(BUNDLE.getString("MappingWizardPage3.combo"));
 			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
 			data.horizontalSpan = 1;
 			label1.setLayoutData(data);
 			
 			ClassList = new Combo(root, SWT.NONE);
				Iterator iter = ((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getPropertyIterator();
				PropertyMapping newcol;
 				while ( iter.hasNext() ) {
				newcol=(PropertyMapping)iter.next();
 				if (isReadyForID(newcol)) 
				ClassList.add(newcol.getName());
 				}
 			
 			if (ClassList.getItemCount()>0)
			ClassList.setText(ClassList.getItem(0));
 			ClassList.setLayout(layout);
 			
 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
 			data.heightHint = 26;
			data.widthHint = 170;
 			ClassList.setLayoutData(data);
 			
 			
 	      return root;
 	    }
 
 	    protected void okPressed() {
 	    	if (ClassList.indexOf(ClassList.getText())>=0)
 	    	{
 	    		((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).setIdentifierProperty(((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getProperty(ClassList.getText()));
 	    		if (((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getProperty(ClassList.getText())!=null)
 				((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).setIdentifier((ISimpleValueMapping) ((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getProperty(ClassList.getText()).getValue());
 	    		setDirty(true);
 	    		ShowID(); 
	 			setReturnCode(OK); 	    	
 	    	}
 	    	else setReturnCode(CANCEL);
        this.close();
 	}	 	
	}
    
    
    
    
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		initializeDialogUnits(container);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight=0;
		layout.horizontalSpacing=0;
		layout.verticalSpacing = 3;
		container.setLayout(layout);
		
		list = new List(container,SWT.BORDER);
		list.setBackground(new Color(null,255,255,255));
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		data.verticalSpan = 3;		
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		list.setLayoutData(data);
		list.addSelectionListener(new SelectionListener()
				{
			
			public void widgetSelected(SelectionEvent e) {
		        RemoveButton.setEnabled(true);
				
			}

			public void widgetDefaultSelected(SelectionEvent e) {

				
			}
	}
);

        AddButton= new Button(container, SWT.PUSH);
		 data=  new GridData(SWT.FILL, SWT.BEGINNING, false,false, 1, 1);
		int widthHint=convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);////add gavr 24 05.05
		data.widthHint=widthHint;
		data.horizontalIndent=7;
		AddButton.setLayoutData(data);
        AddButton.setText(BUNDLE.getString("MappingWizardPage3.add"));
        AddButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SetNewID();
			}
		});
	    
        RemoveButton= new Button(container, SWT.PUSH);
        RemoveButton.setText(BUNDLE.getString("MappingWizardPage3.remove"));
        RemoveButton.setEnabled(false);        
        RemoveButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				RemoveID();
			}
		});
		data = new GridData(SWT.FILL, SWT.BEGINNING, false,false, 1, 1);
		data.widthHint=widthHint;
		data.horizontalIndent=7;
	    RemoveButton.setLayoutData(data);
        
        
		setControl(container);		
		
	}
	public MappingWizardPage3(IMapping mod){
		super("MappingWizardPage");
		setTitle(BUNDLE.getString("MappingWizardPage1.title"));
		setDescription(BUNDLE.getString("MappingWizardPage3.description")+"\n"+BUNDLE.getString("MappingWizardPage3.descriptionpage"));
	  	this.mod=mod;
	}
	public void SetVariables(String NewSelectedPC)
	{
		SelectedPC=NewSelectedPC;
		ShowID();
	}

	private void ShowID() {
		list.removeAll();
		if (((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getIdentifierProperty()!=null)
		{
			PropertyMapping mapping = (PropertyMapping)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getIdentifierProperty();
			String keyname="";
			char simbol=' '; 
			if ( mapping!=null )
			{
				keyname=" <->";
 	    			Iterator iter=mapping.getColumnIterator();
 	 				while ( iter.hasNext() ) {
 	 					keyname=keyname+simbol+((Column)iter.next()).getName();
 	 					simbol=',';
 	 	 				}
 
 	 			if (mapping.getPersistentField()==null)
				{
					ExceptionHandler.logInfo("IdentifierProperty of mapping for class \""+SelectedPC+"\" have the null PersistentField reference.");
 	 				list.add(mapping.getName()+": ??????"+keyname);
				}
 	 			else
 	 				list.add(mapping.getName()+":"+mapping.getPersistentField().getType()+keyname); 	 				
			}

		}
		this.getWizard().getContainer().updateButtons();
		RemoveButton.setEnabled(false);
		}
	
	
	private void SetNewID() {
		Dialog dlg=null;
		if (((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getIdentifier() instanceof ComponentMapping)
		{
			dlg=new AddDlg(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
			// #changed# by Konstantin Mishin on 23.11.2005 fixed for ESORM-333
			try {
				dlg.open();				
			} catch (IllegalArgumentException e) {
            	//TODO (tau-tau) for Exception				
			}
			// #changed#
		}
		else 
		{
			dlg=new AddSimpleDlg(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
			dlg.open();
		}
		
	 	if (dlg.getReturnCode()==Window.OK)
	 	{
			ShowID();
	 		this.getWizard().getContainer().updateButtons();
	 	}
		
	}

	
	private void RemoveID() {
	 	if ((list.getSelectionCount()==1) && (list.getItem(list.getSelectionIndex()).charAt(0)!=' ')) 
	 	{
 		//if (MessageDialog.openConfirm(getShell(), BUNDLE.getString("MappingWizardPage3.deldlgtitle"), BUNDLE.getString("MappingWizardPage3.deldlgdescr")+ "  " + list.getItem(list.getSelectionIndex())+"?"))
 			if(MessageDialog.openConfirm(getShell(), BUNDLE.getString("MappingWizardPage3.deldlgtitle"),MessageFormat.format(BUNDLE.getString("MappingWizardPage3.deldlgdescr"),
					new Object[]{list.getItem(list.getSelectionIndex())})))
 		{
    		((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).setIdentifierProperty(null);
			((IHibernateClassMapping)mod.findClass(SelectedPC).getPersistentClassMapping()).setIdentifier(null);
			setDirty(true);
 			ShowID(); 			
 		 	RemoveButton.setEnabled(false);
 			this.getWizard().getContainer().updateButtons();
 		}
 		}
	}
	
	private boolean isReadyForID(PropertyMapping Columnname) {
		boolean res=true;
		//TODO akuzmin do nessesary compares field for posability to be PK here 
		if (((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getIdentifier() instanceof ComponentMapping)
		{

		if (!(Columnname.getValue() instanceof ComponentMapping)) res=false;
		else if (Columnname.getColumnSpan()<((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getIdentifier().getColumnSpan()) res=false; 
		}
		else		
		if (((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getIdentifier() instanceof SimpleValueMapping)
		{
		if (Columnname.getValue() instanceof ComponentMapping) res=false;
		else if (!(Columnname.getValue() instanceof SimpleValueMapping)) res=false;
		}
		
//		if (!ClassUtils.isSimpleType(TypeName)) res=false;
		return res;
	}

	public boolean canFlipToNextPage() {
		if (list.getItemCount() > 0) {
			ArrayList<String> IDList = new ArrayList<String>(), PKList = new ArrayList<String>();
			if (((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getIdentifierProperty() != null) {
				Iterator iter=((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getIdentifierProperty().getColumnIterator();
	 			while (iter.hasNext()) {
	 				IDList.add(((Column)iter.next()).getName());
	 			}
			}
 			if (((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDatabaseTable().getPrimaryKey()!=null)
 			{
 				Iterator iter=((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDatabaseTable().getPrimaryKey().getColumnIterator();
	 			while ( iter.hasNext() ) {
	 				PKList.add(((Column)iter.next()).getName());
	 			}
	 		}
			if (PKList.containsAll(IDList) && IDList.containsAll(PKList))
			{
				this.setErrorMessage(null);
				return true;
			}
			else
			{
				this.setErrorMessage(BUNDLE.getString("MappingWizardPage3.errormessage"));
				return false;
			}
		}
		else
		{
		this.setErrorMessage(null);			
		return true;
		}
		}
	/**
	 * @return Returns the dirty.
	 */
	public boolean isDirty() {
		return dirty;
	}
	/**
	 * @param dirty The dirty to set.
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	
}
