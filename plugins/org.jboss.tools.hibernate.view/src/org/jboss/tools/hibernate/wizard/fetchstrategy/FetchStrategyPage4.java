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
package org.jboss.tools.hibernate.wizard.fetchstrategy;

import java.util.ArrayList;
import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.DataFormatException;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ToOneMapping;


/**
 * @author kaa - akuzmin@exadel.com
 * dial with Batch fetching associations
 */
public class FetchStrategyPage4 extends WizardPage {
	public static final String BUNDLE_NAME = "fetchstrategy"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(FetchStrategyPage4.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private IMapping mod;
	private List list;
	private Button BatchSizeButton;
	private ArrayList proplist;
	public class AddBatchSizeDlg extends ListDialog{
		private Text Oldtext;
		private Text Newtext;
		private IPropertyMapping pm;

	//New dialog to Simple ID property
 	    public AddBatchSizeDlg(Shell parent,IPropertyMapping pm) {
 			super(parent);
 			this.setTitle(BUNDLE.getString("FetchStrategyPage4.batchsizetitle"));
 			this.pm=pm;
 		}
 	    
 	    protected Control createDialogArea(Composite parent) {
 	        Composite root = new Composite(parent, SWT.NULL);
			root.setBounds(0,0,220,240);
 			
 			Label label2 = new Label(root, SWT.NULL);
			label2.setBounds(20,20,170,15);
			
 			Oldtext = new Text(root, SWT.BORDER | SWT.SINGLE);
			Oldtext.setBounds(20,40,150,20);
			if (pm.getValue() instanceof ICollectionMapping)
				Oldtext.setText(StringConverter.asString(((ICollectionMapping)pm.getValue()).getBatchSize()));
			else
				Oldtext.setText(StringConverter.asString(((IHibernateClassMapping)mod.findClass(((ToOneMapping)pm.getValue()).getReferencedEntityName()).getPersistentClassMapping()).getBatchSize()));
				
			Oldtext.setEditable(false);
			
 			Label label3 = new Label(root, SWT.NULL);
			label3.setBounds(20,80,120,15);

 			Newtext = new Text(root, SWT.BORDER | SWT.SINGLE);
			Newtext.setBounds(20,100,150,20);
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
			
			Newtext.setFocus();
			
 			Label label1 = new Label(root, SWT.NULL);
 			label1.setText("");
			label1.setBounds(20,130,170,15);

 			label2.setText(BUNDLE.getString("FetchStrategyPage4.oldvalue"));
 			label3.setText(BUNDLE.getString("FetchStrategyPage4.newvalue"));
			
 	      return root;
 	    }

		protected void createButtonsForButtonBar(Composite parent) {
			super.createButtonsForButtonBar(parent);
			this.getButton(IDialogConstants.OK_ID).setEnabled(false);			
		}
 	    
 	    protected void okPressed() {
 	    	String size=Newtext.getText().trim();
 	    	if (size.length()>0)
 	    	{
				int z;
		        try {
					z=StringConverter.asInt(size);//Lenghttext.getText().
		        } catch (DataFormatException e) {
					z=StringConverter.asInt(Oldtext.getText());
		        }
		        
				if (pm.getValue() instanceof ICollectionMapping) {
					((ICollectionMapping)pm.getValue()).setBatchSize(z);
				} else {
					((IHibernateClassMapping)mod.findClass(((ToOneMapping)pm.getValue()).getReferencedEntityName()).getPersistentClassMapping()).setBatchSize(z);
				}
				
		        setReturnCode(OK);

		        // add tau 31.03.2006		        
				pm.getPersistentField().getMasterClass().getPersistentClassMapping().getStorage().setDirty(true);		        
		        
 	    	}
 	    	else setReturnCode(CANCEL);
        this.close();
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
		list = new List(container,SWT.BORDER|SWT.V_SCROLL|SWT.SINGLE);
		list.setBackground(new Color(null,255,255,255));
		list.addSelectionListener(new SelectionListener()
				{
			
			public void widgetSelected(SelectionEvent e) {
				BatchSizeButton.setEnabled(true);
				
			}

			public void widgetDefaultSelected(SelectionEvent e) {

				
			}
	}
);		
//		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
//		data.horizontalSpan = 2;
//		data.verticalSpan = 3;
//        int listHeight = list.getItemHeight() * 12;
//        Rectangle trim = list.computeTrim(0, 0, 0, listHeight);
//        data.heightHint = trim.height;
//		
//		data.grabExcessVerticalSpace = true;
//		data.grabExcessHorizontalSpace = true;
//		list.setLayoutData(data);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.verticalSpan = 3;
        int listHeight = list.getItemHeight() * 12;
        Rectangle trim = list.computeTrim(0, 0, 0, listHeight);
        data.heightHint = trim.height;
		list.setLayoutData(data);
		
		BatchSizeButton= new Button(container, SWT.PUSH);
		BatchSizeButton.setText(BUNDLE.getString("FetchStrategyWizard.batchsizebutton"));
		data=  new GridData(SWT.FILL, SWT.BEGINNING, false,false, 1, 1);
		data.horizontalIndent=5;
		BatchSizeButton.setLayoutData(data);
		BatchSizeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				setBatchSize();	
			}
		});
	    
		setControl(container);
		refreshList();
	
	}
	
	protected void setBatchSize() {
		// TODO akuzmin set new batch-size
		if (list.getSelectionCount()==1)
		{
		Dialog dlg=new AddBatchSizeDlg(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),(IPropertyMapping) proplist.get(list.getSelectionIndex()));
	 	dlg.open();
	 	if (dlg.getReturnCode()==Window.OK)
	 	{
	 		refreshList();
		}
		}
		
	}

	public FetchStrategyPage4(IMapping mod) {
		super("wizardPage");
		setTitle(BUNDLE.getString("FetchStrategyWizard.title"));
		setDescription(BUNDLE.getString("FetchStrategyPage4.description"));
		this.mod = mod;
	}
	/**
	 * refresh list of all associations
	 */
	public void refreshList()
	{
		list.removeAll();
		String size="0";
		proplist=((FetchStrategyWizard)getWizard()).findAssociations(this);
		if (proplist!=null)
		for(int i=0;i<proplist.size();i++)
		{
			if (((IPropertyMapping) proplist.get(i)).getValue() instanceof ICollectionMapping)
				size=StringConverter.asString(((ICollectionMapping)((IPropertyMapping) proplist.get(i)).getValue()).getBatchSize());
			else
				if (mod.findClass(((ToOneMapping)((IPropertyMapping) proplist.get(i)).getValue()).getReferencedEntityName())==null)
					ExceptionHandler.logInfo("Fetch Strategy Wizard : mapping for field "+
							((IPropertyMapping) proplist.get(i)).getValue().getFieldMapping().getPersistentField().getOwnerClass().getName()+"<->"+
							((IPropertyMapping) proplist.get(i)).getValue().getFieldMapping().getName()+" have a reference EntityName= "+((ToOneMapping)((IPropertyMapping) proplist.get(i)).getValue()).getReferencedEntityName()+
							" wich isn't Persistent Class");	
				else size=StringConverter.asString(((IHibernateClassMapping)mod.findClass(((ToOneMapping)((IPropertyMapping) proplist.get(i)).getValue()).getReferencedEntityName()).getPersistentClassMapping()).getBatchSize());
			
		list.add(((IPropertyMapping) proplist.get(i)).getPersistentField().getOwnerClass().getName()+"."+
				((IPropertyMapping) proplist.get(i)).getName()+":"+
				((IPropertyMapping) proplist.get(i)).getPersistentField().getType()+" = "+size);
		
		}
		BatchSizeButton.setEnabled(false);
		
	}

}
