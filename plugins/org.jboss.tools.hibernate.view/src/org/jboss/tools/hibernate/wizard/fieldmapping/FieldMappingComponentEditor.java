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

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.hibernate.IComponentMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.PersistentField;
import org.jboss.tools.hibernate.internal.core.hibernate.CollectionMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ComponentMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.ConfigurationReader;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMappingHelper;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.CombinedBeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertySheetPageWithDescription;


/**
 * @author akuzmin - akuzmin@exadel.com
 *
 * 
 */
public class FieldMappingComponentEditor extends Composite {
	public static final String BUNDLE_NAME = "fieldmapping"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(FieldMappingComponentEditor.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private PropertySheetPage page;
//	private List fieldlist;
	private IMapping mod;
	private IComponentMapping cm;
//	private IPropertyMapping prop;
	private String persistentClassName;
	private BeanPropertySourceBase bp;
	private CombinedBeanPropertySourceBase bps;
	private boolean dirty=false;	
//	private Button AddFieldButton,RemoveFieldButton;
	public class AddDlg extends ListDialog{
	    private Combo Newtype;
		private Button isUserType;
		private Text NewField,UserType;
 	    public AddDlg(Shell parent) {
 			super(parent);
 			this.setTitle(BUNDLE.getString("FieldMappingComponentEditor.addtitle"));
 		}
 	    
 	    protected Control createDialogArea(Composite parent) {
 	        Composite root = new Composite(parent, SWT.NULL);
			root.setBounds(0,0,210,200);
			
 			Label label2 = new Label(root, SWT.NULL);
 			label2.setText(BUNDLE.getString("FieldMappingComponentEditor.newfield"));
			label2.setBounds(25,20,170,15);
			
 			NewField = new Text(root, SWT.BORDER | SWT.SINGLE);
			NewField.setBounds(35,35,150,20);
			
 			Label label3 = new Label(root, SWT.NULL);
 			label3.setText(BUNDLE.getString("FieldMappingComponentEditor.newfieldjavatype"));
			label3.setBounds(25,70,150,15);
			
 			Newtype = new Combo(root, SWT.READ_ONLY);
			Object[] types =Type.getHibernateTypes(); 
		    for (int i = 0 ; i < types.length; i++ )
			{
				if (Newtype.indexOf(((Type)types[i]).getJavaType().getName())<0)
				Newtype.add(((Type)types[i]).getJavaType().getName());
			}
			Newtype.add("java.util.List");
			Newtype.add("java.util.Map");
			Newtype.add("java.util.Set");
			Newtype.add("java.util.Collection");
			Newtype.add("java.util.SortedSet");
			Newtype.add("java.util.SortedMap");
			Newtype.setText(Newtype.getItem(0));
			Newtype.setBounds(35,85,150,20);
			
			isUserType= new Button(root, SWT.CHECK);
			isUserType.setText(BUNDLE.getString("FieldMappingComponentEditor.isusertype"));
			isUserType.addSelectionListener(new SelectionAdapter() {
 				public void widgetSelected(SelectionEvent e) {
 					DoCheck();
 				}
 			});
			isUserType.setBounds(20,115,190,20);
			
 			Label label4 = new Label(root, SWT.NULL);
 			label4.setText(BUNDLE.getString("FieldMappingComponentEditor.usertypename"));
			label4.setBounds(25,145,180,15);
			
			
			UserType = new Text(root, SWT.BORDER | SWT.SINGLE);
			UserType.setBounds(35,160,150,20);
			
			Label label5 = new Label(root, SWT.NULL);
 			label5.setText("");
			label5.setBounds(30,180,180,20);
			
			UserType.setEnabled(false);
			
 	      return root;
 	    }
		
 		private void DoCheck() {
 			if (isUserType.getSelection())//process with chek button
 			{
				Newtype.setEnabled(false);
				UserType.setEnabled(true);
 			}
 			else
 			{
				Newtype.setEnabled(true);
				UserType.setEnabled(false);
 			}
  		}	
		
		
 	    protected void okPressed() {
			String SelectedType=null;
//			IHibernateClassMapping mapping=(IHibernateClassMapping)(mod.findClass(persistentClassName).getPersistentClassMapping());
			
			if (isUserType.getSelection())//new field
			{
				SelectedType=UserType.getText().trim();
				if (SelectedType.equals(""))
				{
		 			setReturnCode(CANCEL);					
					this.close();
					return;
				}
			}
			else SelectedType=Newtype.getText().trim();
			
	        String fieldName = NewField.getText();
//			ICompilationUnit unit = ((PersistentClass)mapping.getPersistentClass()).getSourceCode();
//			if (unit != null)
//			{
//				IType type = unit.findPrimaryType();
//				if (type != null)
//	            {
//	                TypeAnalyzer ta = new TypeAnalyzer(type);
//	                
//	                try {
//						fieldName = ((HibernateAutoMapping)mod.getAutoMappingService()).findNonConflictingPropertyName(ta,"",fieldName);
//					} catch (CoreException e) {
//						e.printStackTrace();
//					}
//	                try {
//						ClassUtils.generateProperty(fieldName,Newtype.getText(),type,ClassUtils.G_GETTER|ClassUtils.G_SETTER);
//					} catch (CoreException e) {
//						e.printStackTrace();
//					}
//	            }
//			}		
				// changed by Nick 23.06.2005
                PersistentField field=HibernateAutoMapping.createPersistentFieldByType(null,fieldName,SelectedType,null);
                // by Nick
                // added by Nick 23.06.2005
                field.addAccessorMask(PersistentField.ACCESSOR_FIELD|PersistentField.ACCESSOR_PROPERTY);
                //by Nick
//				IPersistentField field=null;
//				try {
//					field = ((HibernateAutoMapping)mod.getAutoMappingService()).createReversedField(null,NewField.getText(),Newtype.getText());
//				} catch (CoreException e) {
//					ExceptionHandler.handle(e, getShell(), null, null);
//				}
				if (field==null)
				{
		 			setReturnCode(CANCEL);					
					this.close();
					return;
				}
		        dirty=true;
//				SelectedField=field.getName();
				HibernateAutoMappingHelper helper = new HibernateAutoMappingHelper(ConfigurationReader.getAutomappingConfigurationInstance((HibernateMapping)mod));
				((PersistentField)field).setName(fieldName);
				((PersistentField)field).setOwnerClass(mod.findClass(persistentClassName));
				cm.addProperty(helper.createAndBindPropertyMapping(field));
				
 		setReturnCode(OK);			
        this.close();
 	}	 	
	}	
	
	
	public FieldMappingComponentEditor(Composite parent,IMapping mod,String persistentClassName) {
			super(parent, SWT.NONE);
			GridLayout layout = new GridLayout(1,false);
			setLayout(layout);
			this.mod=mod;
			this.persistentClassName=persistentClassName;
//			coment acording ORMIISTUD-151 09.06.05
//			fieldlist = new List(this,SWT.BORDER|SWT.V_SCROLL);
//			fieldlist.setBackground(new Color(null,255,255,255));
//
//			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
//			data.horizontalSpan = 2;
//			data.verticalSpan= 3;
//			data.grabExcessHorizontalSpace=true;
//			fieldlist.setLayoutData(data);
//			Label label1 = new Label(this, SWT.NULL);
//			label1.setText("");
//			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
//			label1.setLayoutData(data);
//			
//			AddFieldButton = new Button(this, SWT.NONE);
//			AddFieldButton.setText(BUNDLE.getString("FieldMappingComponentEditor.addbutton"));
//			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
//			AddFieldButton.setLayoutData(data);
//			AddFieldButton.addSelectionListener(new SelectionAdapter() {
//				public void widgetSelected(SelectionEvent e) {
//					AddField();
//				}
//			});
//
//			Label label2 = new Label(this, SWT.NULL);
//			label2.setText("");
//			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
//			label2.setLayoutData(data);
//			
//			RemoveFieldButton = new Button(this, SWT.NONE);
//			RemoveFieldButton.setText(BUNDLE.getString("FieldMappingComponentEditor.removebutton"));
//			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
//			RemoveFieldButton.setLayoutData(data);
//			RemoveFieldButton.addSelectionListener(new SelectionAdapter() {
//				public void widgetSelected(SelectionEvent e) {
//					RemoveField();
//				}
//			});
//			
//
//			Label label3 = new Label(this, SWT.NULL);
//			label3.setText("");
//			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
//			data.horizontalSpan = 2;			
//			label3.setLayoutData(data);
//			end coment acording ORMIISTUD-151 09.06.05
			// #changed# by Konstantin Mishin on 16.09.2005 fixed for ESORM-40
			//page=new PropertySheetPage();
			page=new PropertySheetPageWithDescription();
			
			// #changed#
			page.createControl(this);
			page.getControl().setSize(550,600);// added 9/28/2005
			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_FILL);
//			coment acording ORMIISTUD-151 09.06.05
			data.widthHint = 550;
		    data.heightHint=300;			
//			data.grabExcessHorizontalSpace=true;
			data.grabExcessVerticalSpace=true;
//			data.verticalSpan = 2;
//			data.horizontalSpan = 2;
//			end coment acording ORMIISTUD-151 09.06.05			
//			data.heightHint=300;//this line will be delete after uncomment
//			data.widthHint=570;//this line will be delete after uncomment
			page.getControl().setLayoutData(data);
		
	}
	
//	private void RemoveField() {
//	 	if (fieldlist.getSelectionCount()==1) 
//	 	{
//		String fieldname=fieldlist.getItem(fieldlist.getSelectionIndex()).substring(0,fieldlist.getItem(fieldlist.getSelectionIndex()).indexOf(":"));
// 		if (MessageDialog.openConfirm(getShell(), BUNDLE.getString("FieldMappingComponentEditor.deldlgtitle"), BUNDLE.getString("FieldMappingComponentEditor.deldlgdescr")+fieldname+" ?"))
// 		{
//			Iterator iter=cm.getPropertyIterator();
//			while(iter.hasNext())
//			{
//				IPropertyMapping pm=(IPropertyMapping)iter.next();
//				if (fieldname.equals(pm.getName()))
//				{
//					cm.removeProperty(pm);
//					break;
//				}
//			}
//			FormList(cm,prop);	
// 		}
// 		}
//	}

//	private void AddField() {
//		Dialog dlg=new AddDlg(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
//	 	dlg.open();
//	 	if (dlg.getReturnCode()==Window.OK)
//			FormList(cm,prop);
//	}

	public void FormList(IComponentMapping cm,IPropertyMapping prop)
	{
		this.cm=cm;
//		this.prop=prop;
//		coment acording ORMIISTUD-151 09.06.05		
//		fieldlist.removeAll();
//		Iterator iter=cm.getPropertyIterator();
//		while(iter.hasNext())
//		{
//			PropertyMapping fpm=(PropertyMapping)iter.next();
//	 			if (fpm.getPersistentField()==null)
//				{
////					if (fpm.getPersistentClass()!=null)
////						ExceptionHandler.log("IdentifierProperty "+fpm.getName()+" of mapping for class \""+fpm.getPersistentClass().getName()+"\" have the null PersistentField reference.");
////					else ExceptionHandler.log("IdentifierProperty "+fpm.getName()+" haven't class reference and have the null PersistentField reference.");
//					fieldlist.add(fpm.getName()+": ??????");
//				}
// 	 			else
//					fieldlist.add(fpm.getName()+":"+fpm.getPersistentField().getType()); 	 				
//		}
//		end coment acording ORMIISTUD-151 09.06.05
		if (prop.getValue() instanceof CollectionMapping)
		{
			bp=(BeanPropertySourceBase) ((ComponentMapping)cm).getPropertySource();
			page.selectionChanged(null, new StructuredSelection(bp));
		}
		else
		{
			bps=(CombinedBeanPropertySourceBase) prop.getPropertySource(cm);
			page.selectionChanged(null, new StructuredSelection(bps));
		}
	}

	public boolean isDirty() {
		if (bp!=null)
			return dirty||bp.isDirty();
		else
		if (bps!=null)
			return dirty||bps.isDirty();
		else return dirty;

	}
	
}
