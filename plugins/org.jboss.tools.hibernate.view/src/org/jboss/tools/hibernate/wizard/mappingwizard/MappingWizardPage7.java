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

import java.util.Iterator;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IAutoMappingService.Settings;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateConfiguration;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.PropertyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.ConfigurationReader;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMappingHelper;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.PersistentFieldProvider;
import org.jboss.tools.hibernate.internal.core.properties.AddColumnDialog;
import org.jboss.tools.hibernate.internal.core.properties.CombinedBeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.util.TypeUtils;


/**
 * @author kaa
 *
 * Version maping page
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MappingWizardPage7 extends WizardPage {
	public static final String BUNDLE_NAME = "mappingwizard"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(MappingWizardPage5.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private IMapping mod;
	private String SelectedPC;
	private PropertySheetPage page;
	private Button RemoveMappingButton;
	private Button AddVersionButton;
	private boolean dirty=false;
	private CombinedBeanPropertySourceBase bp;
	public class AddDlg extends ListDialog{
	    private Combo ClassList,FieldList,Newtype;
		private Button isCreate,isCreateField;
		private Text NewField;
		private CLabel message;
 	    public AddDlg(Shell parent) {
 			super(parent);
 			this.setTitle(BUNDLE.getString("MappingWizardPage7.addtitle"));
 		}
 	    
 	    protected Control createDialogArea(Composite parent) {
 	        Composite root = new Composite(parent, SWT.NULL);
			root.setBounds(0,0,230,300);
			message = new CLabel(root, SWT.NULL);
			message.setBounds(0,5,190,15);
			message.setText("");

 			Label label = new Label(root, SWT.NULL);
 			label.setText("");
			label.setBounds(0,20,190,5);
			
			isCreateField= new Button(root, SWT.CHECK);
			isCreateField.setText(BUNDLE.getString("MappingWizardPage7.isnewfield"));
			isCreateField.addSelectionListener(new SelectionAdapter() {
 				public void widgetSelected(SelectionEvent e) {
					DoFieldCheck();
 				}
 			});
			isCreateField.setBounds(20,30,210,20);
			
 			Label label1 = new Label(root, SWT.NULL);
 			label1.setText(BUNDLE.getString("MappingWizardPage7.fieldcombo"));
			label1.setBounds(30,60,210,15);			
			
			FieldList = new Combo(root, SWT.READ_ONLY);
			Iterator iter = ((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getPropertyIterator();
			PropertyMapping newcol;
				while ( iter.hasNext() ) {
			newcol=(PropertyMapping)iter.next();
			if (isFieldReadyforVersion(newcol))
					FieldList.add(newcol.getName());
				}
 			
 			if (FieldList.getItemCount()>0)
				FieldList.setText(FieldList.getItem(0));
			FieldList.setBounds(40,80,150,20);

 			Label label2 = new Label(root, SWT.NULL);
 			label2.setText(BUNDLE.getString("MappingWizardPage7.newfield"));
			label2.setBounds(30,110,170,15);
			
 			NewField = new Text(root, SWT.BORDER | SWT.SINGLE);
 			NewField.addModifyListener(new ModifyListener()
					{
				public void modifyText(ModifyEvent e) {
					Text text = (Text) e.widget;
					String newtxt=text.getText().trim();
					if (newtxt.equals("")||
						((!isCreate.getSelection())&&(ClassList.getItemCount()==0)))
					{
						getButton(IDialogConstants.OK_ID).setEnabled(false);
						setErrorMessage(null);
					}
					else 
					if (isFieldExists(newtxt))
					{
						getButton(IDialogConstants.OK_ID).setEnabled(false);
						setErrorMessage(BUNDLE.getString("MappingWizardPage7.existfield"));
					}
					else
					{
						getButton(IDialogConstants.OK_ID).setEnabled(true);
						setErrorMessage(null);
					}
				}
			});
 			
			NewField.setBounds(40,130,150,20);
			
 			Label label3 = new Label(root, SWT.NULL);
 			label3.setText(BUNDLE.getString("MappingWizardPage7.newfieldjavatype"));
			label3.setBounds(30,160,150,15);
			
 			Newtype = new Combo(root, SWT.READ_ONLY);
		    for (int i = 0 ; i < OrmConfiguration.VERSION_TYPES.length ; i++ )
			{
				Newtype.add(Type.getType(OrmConfiguration.VERSION_TYPES[i]).getJavaType().getName());
			}
			Newtype.setText(Newtype.getItem(0));
			Newtype.setBounds(40,180,150,20);
			
			isCreate= new Button(root, SWT.CHECK);
			isCreate.setText(BUNDLE.getString("MappingWizardPage7.createcolumn"));
			isCreate.addSelectionListener(new SelectionAdapter() {
 				public void widgetSelected(SelectionEvent e) {
 					DoCheck();
 				}
 			});
			isCreate.setBounds(20,220,210,20);
			
 			Label label4 = new Label(root, SWT.NULL);
 			label4.setText(BUNDLE.getString("MappingWizardPage7.columncombo"));
			label4.setBounds(30,250,210,15);
			
			ClassList = new Combo(root, SWT.READ_ONLY);
 			iter = (mod.findClass(SelectedPC).getPersistentClassMapping().getDatabaseTable().getColumnIterator());
 			Column curcol=null;
 			while ( iter.hasNext() ) {
 				curcol=(Column)iter.next();
 				if (IsReadyVersion(curcol.getName()))
 				ClassList.add(curcol.getName());
 			}
 			
 			if (ClassList.getItemCount()>0)
			ClassList.setText(ClassList.getItem(0));
			ClassList.setBounds(40,270,150,20);			

			Label label5 = new Label(root, SWT.NULL);
 			label5.setText("");
			label5.setBounds(30,300,210,20);
			
			NewField.setEnabled(false);
			Newtype.setEnabled(false); 			
 			
 	      return root;
 	    }

	    public void setErrorMessage(String message) {
	    	if (message!=null)
	    	{	    		
				this.message.setText(message);
				this.message.setImage(JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR));
	    	}
	    	else
	    	{
				this.message.setText("");
				this.message.setImage(null);
	    	}
		}
 	    
 		private void DoCheck() {
 			if (isCreate.getSelection())//process with chek button
 			{
 				ClassList.setEnabled(false);
 				if (isCreateField.getSelection())
 				{
 					String newname=NewField.getText().trim();
 					if (newname.equals("")) this.getButton(IDialogConstants.OK_ID).setEnabled(false);
 					else this.getButton(IDialogConstants.OK_ID).setEnabled(true);
 				}
 				else 
 				{
 					if (FieldList.getItemCount()==0) this.getButton(IDialogConstants.OK_ID).setEnabled(false);
 					else this.getButton(IDialogConstants.OK_ID).setEnabled(true);	
 				}
 			}
 			else
 			{
 				ClassList.setEnabled(true);
 				if (isCreateField.getSelection())
 				{
 					String newname=NewField.getText().trim();
 					if ((newname.equals(""))||(ClassList.getItemCount()==0)) this.getButton(IDialogConstants.OK_ID).setEnabled(false);
 					else this.getButton(IDialogConstants.OK_ID).setEnabled(true);
 				}
 				else 
 				{
 					if ((FieldList.getItemCount()==0)||(ClassList.getItemCount()==0)) this.getButton(IDialogConstants.OK_ID).setEnabled(false);
 					else this.getButton(IDialogConstants.OK_ID).setEnabled(true);	
 				}
 				
 			}
 		}	
		
 		private void DoFieldCheck() {
 			if (isCreateField.getSelection())//process with chek button
 			{
				FieldList.setEnabled(false);
				NewField.setEnabled(true);
				Newtype.setEnabled(true);
				setErrorMessage(null);
				String newname=NewField.getText().trim();
				if ((newname.equals(""))||
					((!isCreate.getSelection())&&(ClassList.getItemCount()==0)))
					this.getButton(IDialogConstants.OK_ID).setEnabled(false);
				else
					if (isFieldExists(newname))
					{
						getButton(IDialogConstants.OK_ID).setEnabled(false);
						setErrorMessage(BUNDLE.getString("MappingWizardPage7.existfield"));
					}
					else
					this.getButton(IDialogConstants.OK_ID).setEnabled(true);
 			}
 			else
 			{
				FieldList.setEnabled(true);
				NewField.setEnabled(false);
				Newtype.setEnabled(false);
				if ((FieldList.getItemCount()==0)||
						((!isCreate.getSelection())&&(ClassList.getItemCount()==0)))
					this.getButton(IDialogConstants.OK_ID).setEnabled(false);
				else this.getButton(IDialogConstants.OK_ID).setEnabled(true);
				setErrorMessage(null);
 			}
 		}	
		
		protected void createButtonsForButtonBar(Composite parent) {
			super.createButtonsForButtonBar(parent);
			if ((FieldList.getItemCount()==0)||(ClassList.getItemCount()==0))
				this.getButton(IDialogConstants.OK_ID).setEnabled(false);
 			//18.05.2005
 			if (FieldList.getItemCount()==0)
 			{
 				isCreateField.setSelection(true);
 				DoFieldCheck();
 				isCreateField.setEnabled(false);
 			}
 			if (ClassList.getItemCount()==0)
 			{
 				isCreate.setSelection(true);
 				DoCheck();
 				isCreate.setEnabled(false);
 			}
			
		}
		
		private boolean isFieldExists(String fieldname)
		{
			IHibernateClassMapping mapping=(IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping());
			Iterator fmi=mapping.getFieldMappingIterator();
	    	if (fieldname!=null)
	    	{
		    	while (fmi.hasNext())
		    	{
		    		IPropertyMapping value=(IPropertyMapping)fmi.next();
		    		if (value.getPersistentField().getName().toUpperCase().equals(fieldname.toUpperCase()))
		    			return true;
		    	}
	    	}
	    	return false;
		}
		
 	    protected void okPressed() {
			String SelectedColumn=null,SelectedField=null;
			IHibernateClassMapping mapping=(IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping());
			ConfigurationReader hamConfig = ConfigurationReader.getAutomappingConfigurationInstance((HibernateMapping)mod);
            HibernateAutoMappingHelper helper = new HibernateAutoMappingHelper(hamConfig);

			if (isCreate.getSelection())//new column
			{
				Dialog dlg=new AddColumnDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDatabaseTable());
			 	dlg.open();
			 	if (dlg.getReturnCode()==Window.OK)
			 	{
				SelectedColumn=((AddColumnDialog)dlg).getColumnName();
			 	}
			 	else
				{
		 			setReturnCode(CANCEL);					
					this.close();
					return;
				}
			}
			else
 	    	if (ClassList.indexOf(ClassList.getText())>=0)
 	    	{
				SelectedColumn=ClassList.getText();
 	    	}
		 	else//no column
			{
	 			setReturnCode(CANCEL);					
				this.close();
				return;
			}
			
			if (isCreateField.getSelection())//new field
			{
				SelectedField=NewField.getText().trim();
				if (SelectedField.equals(""))
				{
		 			setReturnCode(CANCEL);					
					this.close();
					return;
				}
				IPersistentField field=null;
				try {
					// changed by Nick 13.06.2005 - use PC instead
                    PersistentFieldProvider pf = new PersistentFieldProvider(null,hamConfig);
                    field = pf.getOrCreatePersistentField(mapping.getPersistentClass(),NewField.getText(),Newtype.getText(),
                            Settings.DEFAULT_SETTINGS.canUseHeuristicAlgorithms);
                    pf.runFieldCreation(mod.getProject().getProject());
                    // by Nick
                } catch (CoreException e) {
                	//TODO (tau-tau) for Exception                	
					ExceptionHandler.handle(e, getShell(), null, null);
				}
				if (field==null)
				{
		 			setReturnCode(CANCEL);					
					this.close();
					return;
				}
				SelectedField=field.getName();
				helper.createAndBindPropertyMapping(field);				
				
				
			}
			else 
				if (FieldList.indexOf(FieldList.getText())>=0)
				{
				SelectedField=FieldList.getText();
				}
			 	else
				{
		 			setReturnCode(CANCEL);					
					this.close();
					return;
				}				
			
			
			RemoveMaping();//set version to null
			try {
				((HibernateAutoMapping)mod.getAutoMappingService()).createVersionMapping((ClassMapping)mapping,(HibernateConfiguration)mod.getConfiguration(),SelectedField);
			} catch (CoreException e) {
				ExceptionHandler.handle(e, getShell(), null, null);
				e.printStackTrace();
			}
			if (!isCreateField.getSelection())
			{
			((SimpleValueMapping)mapping.getVersion().getValue()).removeColumn((Column)((SimpleValueMapping)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getVersion().getValue()).getColumnIterator().next());
			}
			else
			{
		        SimpleValueMapping fieldmapping = new SimpleValueMapping();
		        fieldmapping.setTable((Table) ((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDatabaseTable());
				Column newcol=(Column)((Table)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDatabaseTable()).getColumn(SelectedColumn);
		        fieldmapping.setName(newcol.getName());
				if (newcol.getSqlTypeCode()==0)
					fieldmapping.setType(TypeUtils.javaTypeToHibType(Newtype.getText()));	
				else 
					fieldmapping.setType(TypeUtils.columnTypeToHibType(newcol, true, null));
				mapping.getVersion().setValue(fieldmapping);
			}
            Column versionColumn = HibernateAutoMappingHelper.createAndBindColumn(
					(SimpleValueMapping)mapping.getVersion().getValue(),
					SelectedColumn,
                    (Table) ((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDatabaseTable());
            versionColumn.setNullable(false);
			
			
 		setReturnCode(OK);			
        this.close();
 	}	 	
	}
	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight=0;
		layout.horizontalSpacing=0;
		layout.verticalSpacing = 5;
		container.setLayout(layout);
		
		Group gr = new Group(container, SWT.NONE);
		GridLayout Grlayout = new GridLayout();
		Grlayout.numColumns = 2;
		gr.setLayout(Grlayout);
	    GridData groupData =   new GridData(SWT.FILL, SWT.FILL, true, true, 2, 5);
	    groupData.heightHint=450;
	    groupData.widthHint=200;
	    gr.setLayoutData(groupData);

		page=new PropertySheetPage();
		page.createControl(gr);
		//akuzmin 29.07.2005
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        Menu menu = menuMgr.createContextMenu(page.getControl());
		page.getControl().setMenu(menu);
		//
		
		GridData data =new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL); 
		page.getControl().setSize(510,400);
		data.horizontalSpan=2;
		data.verticalSpan = 2;
		page.getControl().setLayoutData(data);
	    
		AddVersionButton= new Button(container, SWT.PUSH);
		data=new GridData(SWT.FILL, SWT.BEGINNING, false,false, 1, 1);
		data.horizontalIndent=9;
		data.verticalIndent=8;
		AddVersionButton.setLayoutData(data);
		AddVersionButton.setText(BUNDLE.getString("MappingWizardPage7.addversion"));//Add version...
		AddVersionButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				SetNewVersion();
			}
		});
	    
		
	    RemoveMappingButton= new Button(container, SWT.PUSH);
		data=new GridData(SWT.FILL, SWT.BEGINNING, false,false, 1, 1);
		data.horizontalIndent=9;
		RemoveMappingButton.setLayoutData(data);
	    RemoveMappingButton.setText(BUNDLE.getString("MappingWizardPage7.removebutton"));
	    RemoveMappingButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				RemoveMaping();
			}
		});

		setControl(container);		
		
	}
	public MappingWizardPage7(IMapping mod){
		super("MappingWizardPage");
		setTitle(BUNDLE.getString("MappingWizardPage1.title"));
		setDescription(BUNDLE.getString("MappingWizardPage7.description")+"\n"+BUNDLE.getString("MappingWizardPage7.descriptionpage"));
	  	this.mod=mod;
	}

	public void SetSelectedPC(String NewSelectedPC)
	{
		SelectedPC=NewSelectedPC;
	}

	public void FormList()
	{
	    IPersistentClass PertsistentClassesobj=mod.findClass(SelectedPC);
	    if (((IHibernateClassMapping)(PertsistentClassesobj.getPersistentClassMapping())).getVersion()!=null)
		{
			page.createControl(this.getShell().getParent());
			page.getControl().setEnabled(true);

		bp = (CombinedBeanPropertySourceBase) ((IHibernateClassMapping)(PertsistentClassesobj.getPersistentClassMapping())).getVersion().getPropertySource(((IHibernateClassMapping)(PertsistentClassesobj.getPersistentClassMapping())).getVersion().getPersistentValueMapping());
		
		page.selectionChanged(null, new StructuredSelection(bp));
		
		AddVersionButton.setEnabled(false);
		RemoveMappingButton.setEnabled(true);
		}
		else
		{
			page.createControl(this.getShell().getParent());
			page.getControl().setEnabled(false);
			RemoveMappingButton.setEnabled(false);
			AddVersionButton.setEnabled(true);
		}
		
	}
	
	private void RemoveMaping()
	{
		setDirty(true);
		((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).setVersion(null);		
		FormList();	
	}
	
	private void SetNewVersion() {
		Dialog dlg=new AddDlg(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
	 	dlg.open();
	 	if (dlg.getReturnCode()==Window.OK)
		this.getWizard().getContainer().updateButtons();
	}
			

	private boolean IsReadyVersion(String Colimnname) {
		return (((MappingWizard)this.getWizard()).IsFieldColumn(Colimnname)); 
	}

	private boolean isFieldReadyforVersion(PropertyMapping newcol) {
		if (newcol.getPersistentField().getType()==null)
		{
			ExceptionHandler.logInfo("Find Persistent Class field\""+newcol.getName()+"\" with null type ");
			return false;	
		}
			
		if (TypeUtils.javaTypeToHibType(newcol.getPersistentField().getType())==null) return false;
		for(int i=0;i<OrmConfiguration.VERSION_TYPES.length;i++)
		{
			if (OrmConfiguration.VERSION_TYPES[i].equals(TypeUtils.javaTypeToHibType(newcol.getPersistentField().getType()).getName()))
			{
				if ((mod.findClass(SelectedPC).getPersistentClassMapping().getIdentifier()==null)||
					((newcol.getPersistentField().getMapping()!=null)&&!(mod.findClass(SelectedPC).getPersistentClassMapping().getIdentifier().equals(newcol.getPersistentField().getMapping().getPersistentValueMapping()))))
				return true;
			}
		}
		return false;
	}
	/**
	 * @return Returns the dirty.
	 */
	public boolean isDirty() {
		if (bp!=null)
			return dirty||bp.isDirty();
		else return dirty;
	}
	/**
	 * @param dirty The dirty to set.
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
}
