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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IDatabaseTablePrimaryKey;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateValueMapping;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.data.PrimaryKey;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ComponentMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.PropertyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.properties.AddColumnDialog;
import org.jboss.tools.hibernate.internal.core.util.TypeUtils;


/**
 * @author kaa
 *
 * Primary Key page
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MappingWizardPage8 extends WizardPage {
	public static final String BUNDLE_NAME = "mappingwizard"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(MappingWizardPage8.class.getPackage().getName() + "." + BUNDLE_NAME);	
    private Button AddButton;
    private Button RemoveButton;
    private Button GeneratorButton;
    private Button SelectButton;
    private List list;
	private Label tablelabel;
	private IMapping mod;
    private String SelectedPC;
	private boolean dirty=false;
	
	public class AddDlg extends ListDialog{//New dialog to select clolumn
	    private Combo ClassList;
 	    public AddDlg(Shell parent) {
 			super(parent);
 			this.setTitle(BUNDLE.getString("MappingWizardPage8.addtitle"));
 		}
 	    
 	    protected Control createDialogArea(Composite parent) {
 	        Composite root = new Composite(parent, SWT.NULL);
 			GridLayout layout = new GridLayout();
 			root.setLayout(layout);
 			
 			Label label1 = new Label(root, SWT.NULL);
 			label1.setText(BUNDLE.getString("MappingWizardPage8.combo"));
 			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
 			data.horizontalSpan = 1;
 			label1.setLayoutData(data);
 			
 			ClassList = new Combo(root, SWT.READ_ONLY);
 			if ((mod.findClass(SelectedPC)).getFields().length>0)
 			{
 				Iterator iter =((Table)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDatabaseTable()).getColumnIterator();
 				Column newcol;
 				while ( iter.hasNext() ) {
 					newcol=(Column)iter.next();
 				if (isReadyForPK(newcol)) 
				ClassList.add(newcol.getName());
 				}
			}
 			if (ClassList.getItemCount()>0)
			ClassList.setText(ClassList.getItem(0));
 			ClassList.setLayout(layout);
 			
 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
 			data.heightHint = 26;
 			data.widthHint = 150; 			
 			ClassList.setLayoutData(data);
 			
 			return root;
 	    }
 	    
 	    // #added# by Konstantin Mishin on 26.11.2005 fixed for ESORM-405
 	    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
 	    	Button b = super.createButton(parent, id, label, defaultButton);
 	    	if(ClassList.getItemCount()<=0 && id==OK)
 	    		b.setEnabled(false);
 	    	return b;
 	    }
 	    // #added#
 	    
 	    protected void okPressed() {
 	    	if (ClassList.indexOf(ClassList.getText())>=0)
 	    	{
 	    		AddColumntoPK(ClassList.getText());
 	    		setReturnCode(OK); 	    	
 	    	}
 	    	else setReturnCode(CANCEL);
 	    	this.close();
 	    }	 	
	}
	public class GeneratorDlg extends ListDialog{//New dialog to add Generator
		private Text Newtext,ParamNameText,ParamValueText;
	    private Combo ClassList,ParamCombo;
	    private Button isGenerate,AddParamButton,RemoveParamButton;
	    private List ParamList;
 	    public GeneratorDlg(Shell parent) {
 			super(parent);
 			this.setTitle(BUNDLE.getString("MappingWizardPage8.generatortitle"));
 		}
 	    
 	    protected Control createDialogArea(Composite parent) {
 	        Composite root = new Composite(parent, SWT.NULL);
			root.setBounds(0,0,320,300); 			
 			
 			isGenerate= new Button(root, SWT.CHECK);
 			isGenerate.setText(BUNDLE.getString("MappingWizardPage8.isGenerate"));
 			isGenerate.addSelectionListener(new SelectionAdapter() {
 				public void widgetSelected(SelectionEvent e) {
 					DoCheck();
 				}
 			});
			isGenerate.setBounds(10,10,300,20);
 			
 			Label label1 = new Label(root, SWT.NULL);
 			label1.setText(BUNDLE.getString("MappingWizardPage8.generators"));
			label1.setBounds(20,40,80,15);			
			// #added# by Konstantin Mishin on 23.11.2005 fixed for ESORM-336
			label1.pack();
			// #added#
			
 			Label label3 = new Label(root, SWT.NULL);
 			label3.setText(BUNDLE.getString("MappingWizardPage8.generatorclassname"));
			label3.setBounds(200,40,80,15);
			// #added# by Konstantin Mishin on 23.11.2005 fixed for ESORM-336
			label3.pack();
			// #added#
			
 			ClassList = new Combo(root, SWT.NONE);
 			ClassList.setItems(OrmConfiguration.PK_GENERATOR_VALUES); 			
			ClassList.setText(OrmConfiguration.DEFAULT_PK_GENERATOR);
			ClassList.setBounds(20,60,120,20);
 			ClassList.addModifyListener(new ModifyListener()
 					{

						public void modifyText(ModifyEvent e) {
							ParamList.removeAll();
							ParamValueText.setText("");
				 			ParamCombo.setItems(OrmConfiguration.PK_GENERATOR_PARAMS[ClassList.indexOf(ClassList.getText())]);
							if (ParamCombo.getItemCount()>0) ParamCombo.setText(ParamCombo.getItem(0)); 
							}}
 			); 			
 			
 			Newtext = new Text(root, SWT.BORDER | SWT.SINGLE);
			Newtext.addModifyListener(new ModifyListener()
					{
				public void modifyText(ModifyEvent e) {
					Text text = (Text) e.widget;
					String newtxt=text.getText().trim();
					if (newtxt.equals(""))
					{
						getButton(IDialogConstants.OK_ID).setEnabled(false);
					}
					else
						getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
			});
 			
			Newtext.setBounds(200,60,120,20);			

 			isGenerate.setSelection(true);
			ClassList.setEnabled(true);
 			Newtext.setEnabled(false);
 				
 				
 			Label label5 = new Label(root, SWT.NULL);
 			label5.setText(BUNDLE.getString("MappingWizardPage8.paramname"));
			label5.setBounds(20,90,120,15);			
 			
 			Label label7 = new Label(root, SWT.NULL);
 			label7.setText(BUNDLE.getString("MappingWizardPage8.paramvalue"));
			label7.setBounds(200,90,120,15);
			
 			ParamCombo = new Combo(root, SWT.BORDER | SWT.READ_ONLY);
			ParamCombo.setBounds(20,110,120,20);
			ParamCombo.addModifyListener(new ModifyListener()
 					{
						public void modifyText(ModifyEvent e) {
							String newstr=ParamCombo.getText().trim();
							String newstr1=ParamValueText.getText().trim();
							if ((!newstr.equals(""))&&(!newstr1.equals("")))  
								{								
								AddParamButton.setEnabled(true);
								}
							else AddParamButton.setEnabled(false);
						}});
			
	
			
 			ParamNameText = new Text(root, SWT.BORDER | SWT.SINGLE);
			ParamNameText.setBounds(20,110,120,20);			
 			ParamNameText.addModifyListener(new ModifyListener()
 					{
						public void modifyText(ModifyEvent e) {
							String newstr=ParamNameText.getText().trim();
							String newstr1=ParamValueText.getText().trim();
							if ((!newstr.equals(""))&&(!newstr1.equals("")))  
								{								
								AddParamButton.setEnabled(true);
								}
							else AddParamButton.setEnabled(false);
						}}
						); 			
			
 			ParamValueText = new Text(root, SWT.BORDER | SWT.SINGLE);
			ParamValueText.setBounds(200,110,120,20);				
			ParamValueText.addModifyListener(new ModifyListener()
 					{
						public void modifyText(ModifyEvent e) {
							String newstr=ParamValueText.getText().trim();
							String newstr1=ParamNameText.getText().trim();
							if (isGenerate.getSelection())
								newstr1=ParamCombo.getText().trim();
							if ((!newstr.equals(""))&&(!newstr1.equals("")))  
								{								
								AddParamButton.setEnabled(true);
								}
							else AddParamButton.setEnabled(false);
						}}
						); 			

 			Label label9 = new Label(root, SWT.NULL);
 			label9.setText(BUNDLE.getString("MappingWizardPage8.generatorparamlist"));
			label9.setBounds(10,150,345,15);			
 			
 			ParamList = new List(root,SWT.BORDER|SWT.V_SCROLL);
 			ParamList.setBackground(new Color(null,255,255,255));
			ParamList.setBounds(10,170,250,120);			
			ParamList.addSelectionListener(new SelectionListener()
					{
				
				public void widgetSelected(SelectionEvent e) {
					RemoveParamButton.setEnabled(true);
					
				}

				public void widgetDefaultSelected(SelectionEvent e) {

					
				}
		}
	);
 	        AddParamButton= new Button(root, SWT.PUSH);
			AddParamButton.setBounds(270,180,80,23);			
 	        AddParamButton.setText(BUNDLE.getString("MappingWizardPage8.paramadd"));
 	        AddParamButton.addSelectionListener(new SelectionAdapter() {
 				public void widgetSelected(SelectionEvent e) {
 					AddParam();
 				}
 			});
 			AddParamButton.setEnabled(false); 			
 			
 	        RemoveParamButton= new Button(root, SWT.PUSH);
 	        RemoveParamButton.setEnabled(false);
			RemoveParamButton.setBounds(270,215,80,23);			
 	        RemoveParamButton.setText(BUNDLE.getString("MappingWizardPage8.paramremove"));
 	        RemoveParamButton.addSelectionListener(new SelectionAdapter() {
 				public void widgetSelected(SelectionEvent e) {
 					RemoveParam();
 				}
 			});

	      return root;
 	    }
 
 		private void DoCheck() {
			ParamList.removeAll();
			ParamValueText.setText("");
 			if (isGenerate.getSelection())//process with chek button
 			{
 				ClassList.setEnabled(true);
 				Newtext.setEnabled(false);
				ParamCombo.setVisible(true);
				ParamNameText.setVisible(false);
				getButton(IDialogConstants.OK_ID).setEnabled(true);				
 			}
 			else
 			{
 				ClassList.setEnabled(false);
 				Newtext.setEnabled(true);
				ParamCombo.setVisible(false);
				ParamNameText.setVisible(true);
				String newtxt=Newtext.getText().trim();
				if (newtxt.equals(""))
				{
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}
				else
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				
 			}
 		}	

 		private void AddParam() {
			String ParName=null;
			if (isGenerate.getSelection())
			{
				if (ParamCombo.getItemCount()>0) ParName=ParamCombo.getText();
			}
			else ParName=ParamNameText.getText();
			
			if (ParName!=null)
			{
 			ParamList.add(ParName+"="+ParamValueText.getText());//process add param
 			ParamNameText.setText("");
 			ParamValueText.setText("");
			}
 		}	
 		
 		private void RemoveParam() {
 			if (ParamList.getSelectionCount()==1)
 				{
 				ParamList.remove(ParamList.getSelectionIndex());//process remove param
 				ParamList.setFocus();
 				if (ParamList.getItemCount()>0)
 				{
 					ParamList.setSelection(0);
 				}
 				else RemoveParamButton.setEnabled(false);
 				}
 		}	

 		
 	    protected void okPressed() {
 	    	String classname;
 	    	if (isGenerate.getSelection()) classname=ClassList.getText();
 	    		else classname=Newtext.getText();
 	    		
 	    	((SimpleValueMapping)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getIdentifier()).setIdentifierGeneratorStrategy(classname);
			Properties params = new Properties();
			for(int i=0;i<ParamList.getItemCount();i++)
			{
			int z=ParamList.getItem(i).indexOf("=");
			params.setProperty( ParamList.getItem(i).substring(0,z), ParamList.getItem(i).substring(z+1));
			}
			((SimpleValueMapping)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getIdentifier()).setIdentifierGeneratorProperties( params );
			setDirty(true);
         this.close();
 	}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.dialogs.ListDialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
		 */
		protected void createButtonsForButtonBar(Composite parent) {
			super.createButtonsForButtonBar(parent);
			if (ClassList.indexOf(((SimpleValueMapping)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getIdentifier()).getIdentifierGeneratorStrategy())<0)
			{
				isGenerate.setSelection(false);
				DoCheck();
				Newtext.setText(((SimpleValueMapping)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getIdentifier()).getIdentifierGeneratorStrategy());
			}
			else
			{
			ClassList.setText(((SimpleValueMapping)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getIdentifier()).getIdentifierGeneratorStrategy());
			
			if (ClassList.getItemCount()>0)
			{
 			ParamCombo.setItems(OrmConfiguration.PK_GENERATOR_PARAMS[ClassList.indexOf(ClassList.getText())]);
			if (ParamCombo.getItemCount()>0) ParamCombo.setText(ParamCombo.getItem(0)); 
			}
			}

			if (((SimpleValueMapping)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getIdentifier()).getIdentifierGeneratorProperties()!=null)
			{
			String GenValue;
			Enumeration elem=((SimpleValueMapping)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getIdentifier()).getIdentifierGeneratorProperties().keys();
			while (elem.hasMoreElements())
			{
				GenValue=(String) elem.nextElement();
				ParamList.add(GenValue+"="+((SimpleValueMapping)((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getIdentifier()).getIdentifierGeneratorProperties().getProperty(GenValue));//process add param
			}
			}			
			
		}	 	
	}

	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		layout.marginHeight=0;
		layout.horizontalSpacing=0;
		layout.verticalSpacing = 3;
		container.setLayout(layout);

		tablelabel = new Label(container, SWT.NULL);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 4;
		tablelabel.setLayoutData(data);
		
		list = new List(container,SWT.BORDER);
		list.setBackground(new Color(null,255,255,255));
         data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		data.verticalSpan = 4;		
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

		Label label1 = new Label(container, SWT.NULL);
		label1.setText("    ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		label1.setLayoutData(data);
		
        AddButton= new Button(container, SWT.PUSH);
        AddButton.setText(BUNDLE.getString("MappingWizardPage8.add"));
        AddButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AddNewPK();
			}
		});
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
	    data.horizontalSpan = 1;
	    AddButton.setLayoutData(data);

		Label label4 = new Label(container, SWT.NULL);
		label4.setText("    ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		label4.setLayoutData(data);
		
        SelectButton= new Button(container, SWT.PUSH);
        SelectButton.setText(BUNDLE.getString("MappingWizardPage8.select"));
        SelectButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SetNewPK();
			}
		});
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
	    data.horizontalSpan = 1;
	    SelectButton.setLayoutData(data);
	    
		Label label2 = new Label(container, SWT.NULL);
		label2.setText("    ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		label2.setLayoutData(data);
	    
        RemoveButton= new Button(container, SWT.PUSH);
        RemoveButton.setText(BUNDLE.getString("MappingWizardPage8.remove"));
        RemoveButton.setEnabled(false);
        RemoveButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				RemovePK();
			}
		});
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
	    data.horizontalSpan = 1;
	    RemoveButton.setLayoutData(data);

		Label label3 = new Label(container, SWT.NULL);
		label3.setText("    ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		label3.setLayoutData(data);
	    
        GeneratorButton= new Button(container, SWT.PUSH);
        GeneratorButton.setText(BUNDLE.getString("MappingWizardPage8.generate"));
        GeneratorButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AddGenerator();
			}
		});
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
	    data.horizontalSpan = 1;
	    GeneratorButton.setLayoutData(data);
	    
        
		setControl(container);		
		
	}
	public MappingWizardPage8(IMapping mod){
		super("MappingWizardPage");
		setTitle(BUNDLE.getString("MappingWizardPage1.title"));
		setDescription(BUNDLE.getString("MappingWizardPage8.description")+"\n"+BUNDLE.getString("MappingWizardPage8.descriptionpage"));
	  	this.mod=mod;
	}
	public void SetVariables(String NewSelectedPC)
	{
		SelectedPC=NewSelectedPC;
		tablelabel.setText(BUNDLE.getString("MappingWizardPage8.listdescr")+"\""+((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDatabaseTable().getName()+"\"");
		ShowPK();		
	}
	
	private void ShowPK() {
		list.removeAll();
		if (((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDatabaseTable().getPrimaryKey()!=null)		
		{
			Iterator iter =((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDatabaseTable().getPrimaryKey().getColumnIterator();			
			Column curcol=null;
			while ( iter.hasNext() ) {
				curcol=(Column)iter.next();
				String TypeName;
				if (TypeUtils.columnTypeToHibType(curcol, true, null)==null)
					TypeName=curcol.getSqlTypeName();
				else
					TypeName=TypeUtils.columnTypeToHibType(curcol, true, null).getName();
				
				list.add(curcol.getName()+":"+TypeName);
			}
		}
		RemoveButton.setEnabled(false);
	}

	private void SetNewPK() {
		Dialog dlg=new AddDlg(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
	 	dlg.open();
	 	if (dlg.getReturnCode()==Window.OK)
	 		this.getWizard().getContainer().updateButtons();
	}

	private void AddNewPK() {
		Dialog dlg=new AddColumnDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).getDatabaseTable());
	 	dlg.open();
	 	if (dlg.getReturnCode()==Window.OK)
	 	{
	 		AddColumntoPK(((AddColumnDialog)dlg).getColumnName());
	 		this.getWizard().getContainer().updateButtons();
	 	}
	}
	
	private void RemovePK() {
	 	if (list.getSelectionCount()==1) 
	 	{
 		//if (MessageDialog.openConfirm(getShell(), BUNDLE.getString("MappingWizardPage8.deldlgtitle"), BUNDLE.getString("MappingWizardPage8.deldlgdescr")+ "  " + list.getItem(list.getSelectionIndex())+"?"))
	 	if(MessageDialog.openConfirm(getShell(), BUNDLE.getString("MappingWizardPage8.deldlgtitle"),MessageFormat.format(BUNDLE.getString("MappingWizardPage8.deldlgdescr"),
					new Object[]{list.getItem(list.getSelectionIndex())})))
 		{
			IDatabaseTable table=mod.findClass(SelectedPC).getPersistentClassMapping().getDatabaseTable();
			IDatabaseTablePrimaryKey currPK=table.getPrimaryKey();
			int z=list.getItem(list.getSelectionIndex()).indexOf(":");
			currPK.removeColumn(list.getItem(list.getSelectionIndex()).substring(0,z));
 			RebuildMaping(); 			
 			ShowPK(); 		
 		 	RemoveButton.setEnabled(false);
 			this.getWizard().getContainer().updateButtons();
 		}
 		}
	}
	
	private boolean isReadyForPK(Column Columnname) {
		boolean res=true;		
		//TODO akuzmin do nessesary compares field for posability to be PK here
		String TypeName;
		if (TypeUtils.columnTypeToHibType(Columnname, true, null)==null)
			TypeName=Columnname.getSqlTypeName();
		else
			TypeName=TypeUtils.columnTypeToHibType(Columnname, true, null).getName();
		
		if (list.indexOf(Columnname.getName()+":"+TypeName)>=0) res=false;
		return res;
	}

	public boolean canFlipToNextPage() {
		
	if (list.getItemCount()==1)
		GeneratorButton.setEnabled(true);	
	else
		GeneratorButton.setEnabled(false);
	if (list.getItemCount()>0)
	{
		return true;
	}
	else
	{
		return false;
	}
	}

	private void AddColumntoPK(String columnname)
	{
 	    	IDatabaseTable table=mod.findClass(SelectedPC).getPersistentClassMapping().getDatabaseTable();
 	    	IDatabaseColumn newelem=table.getColumn(columnname);
 	    	newelem.setNullable(false);
 	    	newelem.setUnique(true);
			IDatabaseTablePrimaryKey pk=table.getPrimaryKey();
 	    	if (pk==null)
			{
				pk=new PrimaryKey();
				table.setPrimaryKey(pk);
			}
 	    	pk.addColumn(newelem);
  	       	RebuildMaping();  	       
  	        ShowPK();
	}
	
	
	private void RebuildMaping()
	{
	    IHibernateKeyMapping ValueMapping = null;
		IDatabaseTable table=mod.findClass(SelectedPC).getPersistentClassMapping().getDatabaseTable();
		IDatabaseTablePrimaryKey newPK=table.getPrimaryKey();
		if (newPK!=null)
		{
			
			if (newPK.getColumnSpan()>1) //ComponentMapping
			{
				Iterator iter = newPK.getColumnIterator();
	            ComponentMapping mapping = new ComponentMapping(((ClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())));
				while ( iter.hasNext() ) {
			        Column newcol=(Column)iter.next();					
			        SimpleValueMapping fieldmapping = new SimpleValueMapping();
			        fieldmapping.setTable(table);
			        fieldmapping.setName(newcol.getName());
			        fieldmapping.setType(TypeUtils.columnTypeToHibType(newcol, true, null));
			        fieldmapping.addColumn(newcol);
		 	        newcol.setPersistentValueMapping(fieldmapping);
		 	       PropertyMapping pm = new PropertyMapping();
				   //pm.setName();
                   pm.setValue((IHibernateValueMapping)fieldmapping);
                   mapping.addProperty(pm);			        
				}
	 	        ValueMapping=mapping; 	        
			}
			else if (newPK.getColumnSpan()>0)//SimpleValueMapping
			{
		        SimpleValueMapping mapping = new SimpleValueMapping();
		        Column newcol=(Column)newPK.getColumnIterator().next();
	 	        mapping.setTable(table);
	 	        mapping.setName(newcol.getName());
	 	        mapping.setType(TypeUtils.columnTypeToHibType(newcol, true, null));
	 	        mapping.addColumn(newcol);
	 	        newcol.setPersistentValueMapping(mapping);
	 	        ValueMapping=mapping;
			}
		}
		
		((IHibernateClassMapping)(mod.findClass(SelectedPC).getPersistentClassMapping())).setIdentifier(ValueMapping);
 		setDirty(true);
	}
	
	private void AddGenerator()
	{
		Dialog dlg=new GeneratorDlg(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
	 	dlg.open();
	}
	
	public boolean getDirty() {
		return dirty;
	}
	/**
	 * @param dirty The dirty to set.
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	
}
