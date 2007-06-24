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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.hibernate.IAnyMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.AnyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.CollectionMapping;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.CombinedBeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertySheetPageWithDescription;

/**
 * @author akuzmin - akuzmin@exadel.com
 *
 * 
 */
public class FieldMappingAnyEditor extends Composite {
	public static final String BUNDLE_NAME = "fieldmapping"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(FieldMappingComponentEditor.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private PropertySheetPage page;
	private List fieldlist;
	private IMapping mod;
	private IAnyMapping am;
	private IPropertyMapping prop;
	private boolean dirty=false;
	private Button AddFieldButton,RemoveFieldButton,SelectFieldButton;
	private BeanPropertySourceBase bp;
	private CombinedBeanPropertySourceBase bps;
	public class AddDlg extends ListDialog{//New dialog to select clolumn
		private Text Newtext;
	    private Combo ClassList;
	    private String value;
 	    public AddDlg(Shell parent,String value,String title) {
 			super(parent);
 			this.setTitle(title);//BUNDLE.getString("FieldMappingAnyEditor.addtitle"));
 			this.value=value;
 	 		}
 	    
 	    protected Control createDialogArea(Composite parent) {
 	        Composite root = new Composite(parent, SWT.NULL);
 			GridLayout layout = new GridLayout();
 			layout.numColumns=2;
 			root.setLayout(layout);
 			
 			Label label1 = new Label(root, SWT.NULL);
 			label1.setText(BUNDLE.getString("FieldMappingAnyEditor.value"));
 			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
 			label1.setLayoutData(data);

 			
 			Label label3 = new Label(root, SWT.NULL);
 			label3.setText(BUNDLE.getString("FieldMappingAnyEditor.combo"));
 			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
 			label3.setLayoutData(data);

 			Newtext = new Text(root, SWT.BORDER | SWT.SINGLE );
 			data = new GridData(GridData.FILL_HORIZONTAL);
 			
 			data.grabExcessHorizontalSpace=true;
 			data.heightHint = 14;
 			data.widthHint = 80; 
 			Newtext.setLayoutData(data);
 			
			Newtext.addModifyListener(new ModifyListener()
					{
				public void modifyText(ModifyEvent e) {
					if (getButton(IDialogConstants.OK_ID)!=null)
					{
					Text text = (Text) e.widget;
					String newtxt=text.getText().trim();
					if (newtxt.equals(""))
					{
						getButton(IDialogConstants.OK_ID).setEnabled(false);
					}
					else
						getButton(IDialogConstants.OK_ID).setEnabled(true);
					}
				}
			});
 			
 			if (value!=null) Newtext.setText(value);  
 			
 			
 			ClassList = new Combo(root, SWT.READ_ONLY);
 			for(int i=0;i<mod.getPertsistentClasses().length;i++)
 			{
 				ClassList.add(mod.getPertsistentClasses()[i].getName());	
 			}
 			if (value!=null) ClassList.setText(ClassList.getItem(ClassList.indexOf(am.getMetaValues().get(value).toString())));
 			if ((ClassList.getText().equals(""))&&(ClassList.getItemCount()>0))
			ClassList.setText(ClassList.getItem(0));
 			ClassList.setLayout(layout);
 			
 			data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);//new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
  			data.widthHint = 150;
 			ClassList.setLayoutData(data);
 			
 			
			
 	      return root;
 	    }
 
		protected void createButtonsForButtonBar(Composite parent) {
			super.createButtonsForButtonBar(parent);
			String newtxt=Newtext.getText().trim();
			if (newtxt.equals(""))
			{
			this.getButton(IDialogConstants.OK_ID).setEnabled(false);
			}
		}
		
		protected void okPressed() {
			if ((ClassList.indexOf(ClassList.getText())>=0)&&(!Newtext.getText().trim().equals("")))
			{
				setDirty(true);
				Map<String,String> values = am.getMetaValues();
				if (am.getMetaValues()==null)
					values = new HashMap<String,String>();
				if (value!=null) values.remove(value);
				values.put(Newtext.getText().trim(),ClassList.getText());
				am.setMetaValues(values);
				setReturnCode(OK); 	    	
			}
			else setReturnCode(CANCEL);
			this.close();
		}	 	
	}
	
	// #changed# by Konstantin Mishin on 16.09.2005 fixed for ESORM-40
	public FieldMappingAnyEditor(Composite parent,IMapping mod,String persistentClassName) {
		super(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2,false);
		setLayout(layout);
		// #changed# by Konstantin Mishin on 24.11.2005 fixed for ESORM-335	
/*		Composite composite1 = new Composite(this, SWT.NONE);
		Composite composite2 = new Composite(this, SWT.NONE);
		GridLayout glayout = new GridLayout(1,false);
		composite1.setLayout(glayout);
		composite2.setLayout(glayout);
		this.mod=mod;
		fieldlist = new List(composite1,SWT.BORDER|SWT.V_SCROLL);
		fieldlist.setSize(420,160);//160
		fieldlist.setBackground(new Color(null,255,255,255));
		fieldlist.addSelectionListener(new SelectionListener()
				{
			
			public void widgetSelected(SelectionEvent e) {
				SelectFieldButton.setEnabled(true);
				RemoveFieldButton.setEnabled(true);
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}
				}
		);
		
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		data.verticalSpan= 3;
		data.grabExcessHorizontalSpace=true;
		data.grabExcessVerticalSpace=true;
		fieldlist.setLayoutData(data);
		page = new PropertySheetPageWithDescription();
		page.createControl(composite1);
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_FILL);
		data.grabExcessHorizontalSpace=true;
		data.grabExcessVerticalSpace=true;
		composite1.setLayoutData(data);
		data.grabExcessHorizontalSpace=false;
		composite2.setLayoutData(data);
		page.getControl().setLayoutData(data);
		page.getControl().setSize(420,60); */

		this.mod=mod;

		fieldlist = new List(this,SWT.BORDER|SWT.V_SCROLL);
		fieldlist.addSelectionListener(new SelectionListener(){		
			public void widgetSelected(SelectionEvent e) {
				SelectFieldButton.setEnabled(true);
				RemoveFieldButton.setEnabled(true);
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_FILL);
		data.grabExcessHorizontalSpace=true;
		fieldlist.setLayoutData(data);
		
		Composite composite2 = new Composite(this, SWT.NONE);
		Composite composite1 = new Composite(this, SWT.NONE);
		GridLayout glayout = new GridLayout(1,false);
		composite1.setLayout(glayout);
		composite2.setLayout(glayout);
				
		data = new GridData();
		composite2.setLayoutData(data);
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_FILL);
		data.grabExcessHorizontalSpace=true;
		data.grabExcessVerticalSpace=true;
		composite1.setLayoutData(data);
		
		page = new PropertySheetPageWithDescription();
		page.createControl(composite1);
		page.getControl().setLayoutData(data);
		page.getControl().setSize(410,90); 
		// #changed#		
		
		AddFieldButton = new Button(composite2, SWT.NONE);
		AddFieldButton.setText(BUNDLE.getString("FieldMappingAnyEditor.addbutton"));
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		AddFieldButton.setLayoutData(data);
		AddFieldButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AddValue();
			}
		});
		
		SelectFieldButton = new Button(composite2, SWT.NONE);
		SelectFieldButton.setText(BUNDLE.getString("FieldMappingAnyEditor.selectbutton"));
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		SelectFieldButton.setLayoutData(data);
		SelectFieldButton.setEnabled(false);
		SelectFieldButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				EditValue();
			}
		});
				
		RemoveFieldButton = new Button(composite2, SWT.NONE);
		RemoveFieldButton.setText(BUNDLE.getString("FieldMappingAnyEditor.removebutton"));
		RemoveFieldButton.setEnabled(false);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		RemoveFieldButton.setLayoutData(data);
		RemoveFieldButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				RemoveValue();
			}
		});
		
	}
	// #changed#
	
	private void EditValue() {
	 	if (fieldlist.getSelectionCount()==1) 
	 	{
		String value=fieldlist.getItem(fieldlist.getSelectionIndex()).substring(0,fieldlist.getItem(fieldlist.getSelectionIndex()).indexOf(":"));
		Dialog dlg=new AddDlg(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),value,BUNDLE.getString("FieldMappingAnyEditor.edittitle"));
	 	dlg.open();
	 	if (dlg.getReturnCode()==Window.OK)
	 	{
			FormList(am,prop);
	 	}
	 	}
	}

	private void RemoveValue() {
	 	if (fieldlist.getSelectionCount()==1) 
	 	{
		String value=fieldlist.getItem(fieldlist.getSelectionIndex()).substring(0,fieldlist.getItem(fieldlist.getSelectionIndex()).indexOf(":"));
 		if (MessageDialog.openConfirm(getShell(), BUNDLE.getString("FieldMappingAnyEditor.deldlgtitle"), BUNDLE.getString("FieldMappingAnyEditor.deldlgdescr")+'"'+value+'"'+"?"))
 		{
 			setDirty(true);
			Map values = am.getMetaValues();
			values.remove(value);
			am.setMetaValues(values);
			FormList(am,prop);
 		}
 		}
	}

	private void AddValue() {
//		IDatabaseTable table=((IHibernateClassMapping)(mod.findClass(persistentClassName).getPersistentClassMapping())).getDatabaseTable();
		Dialog dlg=new AddDlg(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),null,BUNDLE.getString("FieldMappingAnyEditor.addtitle"));
	 	dlg.open();
	 	if (dlg.getReturnCode()==Window.OK)
	 	{
			FormList(am,prop);
	 	}		
	}

	public void FormList(IAnyMapping am,IPropertyMapping prop)
	{
		this.am=am;
		this.prop=prop;
		fieldlist.removeAll();
		if ((am.getMetaValues()!=null)&&(am.getMetaValues().keySet()!=null))
		{
		Iterator iter=am.getMetaValues().keySet().iterator();
		while(iter.hasNext())
		{
			String fpm=(String)iter.next();
				fieldlist.add(fpm+":"+am.getMetaValues().get(fpm).toString()); 	 				
		}
		}
		if (prop.getValue() instanceof CollectionMapping)
		{
			bp=(BeanPropertySourceBase) ((AnyMapping)am).getPropertySource();
			page.selectionChanged(null, new StructuredSelection(bp));
		}
		else
		{
			bps=(CombinedBeanPropertySourceBase) prop.getPropertySource(am);
			page.selectionChanged(null, new StructuredSelection(bps));
		}
		SelectFieldButton.setEnabled(false);
		RemoveFieldButton.setEnabled(false);
	}
	
	public boolean isDirty() {
		if (bp!=null)
			return dirty||bp.isDirty();
		else
			if (bps!=null)
				return dirty||bps.isDirty();
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
