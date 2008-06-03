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

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
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
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.hibernate.IdBagMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertySheetPageWithDescription;


/**
 * @author akuzmin - akuzmin@exadel.com
 *
 * Jun 1, 2005
 */
public class FieldMappingIdentifierEditor extends Composite {
	public static final String BUNDLE_NAME = "fieldmapping"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(FieldMappingIdentifierEditor.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private PropertySheetPage page;
	private IdBagMapping bm;
    private Button GeneratorButton;
	private BeanPropertySourceBase bp;
	private boolean dirty=false;
	public class GeneratorDlg extends ListDialog{//New dialog to add Generator
		private Text Newtext,ParamNameText,ParamValueText;
	    private Combo ClassList,ParamCombo;
	    private Button isGenerate,AddParamButton,RemoveParamButton;
	    private List ParamList;
 	    public GeneratorDlg(Shell parent) {
 			super(parent);
 			this.setTitle(BUNDLE.getString("FieldMappingIdentifierEditor.generatortitle"));
 		}
 	    
 	    protected Control createDialogArea(Composite parent) {
 	        Composite root = new Composite(parent, SWT.NULL);
			root.setBounds(0,0,320,300); 			
 			
 			isGenerate= new Button(root, SWT.CHECK);
 			isGenerate.setText(BUNDLE.getString("FieldMappingIdentifierEditor.isGenerate"));
 			isGenerate.addSelectionListener(new SelectionAdapter() {
 				public void widgetSelected(SelectionEvent e) {
 					DoCheck();
 				}
 			});
			isGenerate.setBounds(10,10,300,20);
 			
 			Label label1 = new Label(root, SWT.NULL);
 			label1.setText(BUNDLE.getString("FieldMappingIdentifierEditor.generators"));
			label1.setBounds(20,40,80,15);
 			
 			Label label3 = new Label(root, SWT.NULL);
 			label3.setText(BUNDLE.getString("FieldMappingIdentifierEditor.generatorclassname"));
			label3.setBounds(170,40,80,15);
 			
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
 			
			Newtext.setBounds(170,60,120,20);			

 			isGenerate.setSelection(true);
			ClassList.setEnabled(true);
 			Newtext.setEnabled(false);
 				
 				
 			Label label5 = new Label(root, SWT.NULL);
 			label5.setText(BUNDLE.getString("FieldMappingIdentifierEditor.paramname"));
			label5.setBounds(20,90,120,15);			
 			
 			Label label7 = new Label(root, SWT.NULL);
 			label7.setText(BUNDLE.getString("FieldMappingIdentifierEditor.paramvalue"));
			label7.setBounds(170,90,120,15);
			
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
 			
			ParamValueText.setBounds(170,110,120,20);				

 			Label label9 = new Label(root, SWT.NULL);
 			label9.setText(BUNDLE.getString("FieldMappingIdentifierEditor.generatorparamlist"));
			label9.setBounds(10,150,345,15);			
 			
 			ParamList = new List(root,SWT.BORDER|SWT.V_SCROLL);
			ParamList.addSelectionListener(new SelectionListener()
					{
				
				public void widgetSelected(SelectionEvent e) {
					RemoveParamButton.setEnabled(true);
					
				}

				public void widgetDefaultSelected(SelectionEvent e) {

					
				}
		}
	);
 			
 			ParamList.setBackground(new Color(null,255,255,255));
			ParamList.setBounds(10,170,250,120);			

 	        AddParamButton= new Button(root, SWT.PUSH);
			AddParamButton.setBounds(270,180,80,23);			
 	        AddParamButton.setText(BUNDLE.getString("FieldMappingIdentifierEditor.paramadd"));
 	        AddParamButton.addSelectionListener(new SelectionAdapter() {
 				public void widgetSelected(SelectionEvent e) {
 					AddParam();
 				}
 			});
 			AddParamButton.setEnabled(false); 			
 			
 	        RemoveParamButton= new Button(root, SWT.PUSH);
			RemoveParamButton.setBounds(270,215,80,23);
 	        RemoveParamButton.setEnabled(false);			
 	        RemoveParamButton.setText(BUNDLE.getString("FieldMappingIdentifierEditor.paramremove"));
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
 	    		
 	    	((SimpleValueMapping)bm.getIdentifier()).setIdentifierGeneratorStrategy(classname);
			Properties params = new Properties();
			for(int i=0;i<ParamList.getItemCount();i++)
			{
			int z=ParamList.getItem(i).indexOf("=");
			params.setProperty( ParamList.getItem(i).substring(0,z), ParamList.getItem(i).substring(z+1));
			}
			((SimpleValueMapping)bm.getIdentifier()).setIdentifierGeneratorProperties( params );
			setDirty(true);
         this.close();
 	}
 	    
		protected void createButtonsForButtonBar(Composite parent) {
			super.createButtonsForButtonBar(parent);
			if (ClassList.indexOf(((SimpleValueMapping)bm.getIdentifier()).getIdentifierGeneratorStrategy())<0)
			{
				isGenerate.setSelection(false);
				DoCheck();
				Newtext.setText(((SimpleValueMapping)bm.getIdentifier()).getIdentifierGeneratorStrategy());
			}
			else
			{
			ClassList.setText(((SimpleValueMapping)bm.getIdentifier()).getIdentifierGeneratorStrategy());
			
			if (ClassList.getItemCount()>0)
			{
 			ParamCombo.setItems(OrmConfiguration.PK_GENERATOR_PARAMS[ClassList.indexOf(ClassList.getText())]);
			if (ParamCombo.getItemCount()>0) ParamCombo.setText(ParamCombo.getItem(0)); 
			}
			}
			
			if (((SimpleValueMapping)bm.getIdentifier()).getIdentifierGeneratorProperties()!=null)
			{
			String GenValue;
			Enumeration elem=((SimpleValueMapping)bm.getIdentifier()).getIdentifierGeneratorProperties().keys();
			while (elem.hasMoreElements())
			{
				GenValue=(String) elem.nextElement();
				ParamList.add(GenValue+"="+((SimpleValueMapping)bm.getIdentifier()).getIdentifierGeneratorProperties().getProperty(GenValue));//process add param
			}
			}			
		} 	    
	}
	
	public FieldMappingIdentifierEditor(Composite parent) {
			super(parent, SWT.NONE);
			GridLayout layout = new GridLayout(2,false);
			setLayout(layout);

			// #changed# by Konstantin Mishin on 19.09.2005 fixed for ESORM-40
			Composite composite = new Composite(this, SWT.NONE);
			layout = new GridLayout(1,false);
			composite.setLayout(layout);
			
			page=new PropertySheetPageWithDescription();
			page.createControl(composite);
			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_FILL);
			data.grabExcessHorizontalSpace=true;
			data.grabExcessVerticalSpace=true;
			composite.setLayoutData(data);
			page.getControl().setLayoutData(data);
			// #changed#
			
			GeneratorButton = new Button(this, SWT.NONE);
			GeneratorButton.setText(BUNDLE.getString("FieldMappingIdentifierEditor.generate"));
			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			GeneratorButton.setLayoutData(data);
			GeneratorButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					AddGenerator();
				}
			});
			
	}

	private void AddGenerator()
	{
		Dialog dlg=new GeneratorDlg(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
	 	dlg.open();
	 	if (dlg.getReturnCode()==Window.OK)
	 	{
			FormList(bm);
	 	}
	 	
	}
	
	public void FormList(IdBagMapping bm)
	{
		this.bm=bm;
		bp=(BeanPropertySourceBase) bm.getIdentifierPropertySource();
		page.selectionChanged(null, new StructuredSelection(bp));
	}

	public boolean isDirty() {
		if (bp!=null)
			return dirty||bp.isDirty();
		else
			return dirty;
	}

	/**
	 * @param dirty The dirty to set.
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

}
