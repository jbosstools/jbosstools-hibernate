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
package org.jboss.tools.hibernate.wizard.generateDAO;

import java.util.Arrays;
import java.util.ResourceBundle;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.dialog.ModelCheckedTreeSelectionDialog;
import org.jboss.tools.hibernate.wizard.treemodel.PersistentClassesLabelProvider;
import org.jboss.tools.hibernate.wizard.treemodel.TreeModel;
import org.jboss.tools.hibernate.wizard.treemodel.TreeModelContentProvider;



/**
 * @author kaa
 * akuzmin@exadel.com
 * Aug 16, 2005
 */
public class GenerateDAOPage1 extends WizardPage {
	private IMapping mod;	
	public static final String BUNDLE_NAME = "generateDAO"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(GenerateDAOPage1.class.getPackage().getName() + "." + BUNDLE_NAME);	
	
	protected GenerateDAOPage1(IMapping mod) {
		super("wizardPage");
		setTitle(BUNDLE.getString("GenerateDAOPage1.title"));
		setDescription(BUNDLE.getString("GenerateDAOPage1.description"));
		this.mod = mod;
	}

    private Button PersistentClassesButton;
	private Button RemovePersistentClasses;
    private List list;
    //del tau 04.04.2006
	//private TreeModel allClasses;

    
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 3;
		container.setLayout(layout);
		
		Label label = new Label(container, SWT.NULL);
		label.setText(BUNDLE.getString("GenerateDAOPage1.label"));
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 3;
		label.setLayoutData(data);
		
		list = new List(container,SWT.BORDER|SWT.V_SCROLL|SWT.MULTI);
		list.setBackground(new Color(null,255,255,255));
	    data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		data.verticalSpan = 2;		
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		list.setLayoutData(data);
		list.addSelectionListener(new SelectionListener()
				{
			
			public void widgetSelected(SelectionEvent e) {
				RemovePersistentClasses.setEnabled(true);
				
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
	}
);
		
		PersistentClassesButton= new Button(container, SWT.PUSH);
		PersistentClassesButton.setText(BUNDLE.getString("GenerateDAOPage1.button"));
		data=  new GridData(SWT.FILL, SWT.BEGINNING, false,false, 1, 1);
		PersistentClassesButton.setLayoutData(data);	
		PersistentClassesButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				DoBrowse();	
			}
		});
		RemovePersistentClasses= new Button(container, SWT.PUSH);
		data=  new GridData(SWT.FILL, SWT.BEGINNING, false,false, 1, 1);
		RemovePersistentClasses.setText(BUNDLE.getString("GenerateDAOPage1.removebutton"));
		RemovePersistentClasses.setLayoutData(data);
		RemovePersistentClasses.setEnabled(false);
		RemovePersistentClasses.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DoRemove();	
			}

		});	
		setControl(container);		
	}

	public void DoBrowse(){
		TreeModel allClasses = new TreeModel("root",null);
		if (mod!=null)
		{
   	 	IPersistentClass[] PertsistentClassesobj=mod.getPertsistentClasses();
	 	for (int i = 0; i < PertsistentClassesobj.length; i++)
	 	{
	 		if (list.indexOf(PertsistentClassesobj[i].getName())<0)
	 		{
			 if (allClasses.getNode(PertsistentClassesobj[i].getPackage().getName())==null)
				 	allClasses.addNode(PertsistentClassesobj[i].getPackage().getName());
				 TreeModel classnode = (TreeModel) allClasses.getNode(PertsistentClassesobj[i].getPackage().getName());					 
				 classnode.addNode(PertsistentClassesobj[i].getShortName());
	 		}
	 	}
					
		}
		//make new dialog
		
		ModelCheckedTreeSelectionDialog dlg = new ModelCheckedTreeSelectionDialog(
				getShell(), new PersistentClassesLabelProvider(),
				new TreeModelContentProvider()
				) {
					/* (non-Javadoc)
					 * @see org.jboss.tools.hibernate.dialog.ModelCheckedTreeSelectionDialog#create()
					 */
					public void create() {
						super.create();
						setAllSelections();
					}
			
		};
		dlg.setTitle(BUNDLE.getString("GenerateDAOPage1.dialogtitle"));
		dlg.setMessage(BUNDLE.getString("GenerateDAOPage1.dialogtext"));
		dlg.setInput(allClasses);
		dlg.open();
		if (dlg.getReturnCode() == 0) {
			Object[] selectedClasses = dlg.getResult();
			for (int z = 0; z < selectedClasses.length; z++)
				if (selectedClasses[z] instanceof TreeModel)
				{
					TreeModel elem = (TreeModel) selectedClasses[z];
					if (elem.getChildren().length==0)
					{
						if (elem.getParent().getName() == null || elem.getParent().getName().trim().length() == 0 )
							list.add(elem.getName());
						else
							list.add(elem.getParent().getName()+"."+elem.getName());
					}
						
				}
			list.setRedraw(false);
			sortList();
			list.setRedraw(true);
		}
		
		 getWizard().getContainer().updateButtons();
	}

	private void sortList() {
		String [] values=list.getItems();
		Arrays.sort(values);
		list.removeAll();
		list.setItems(values);
	}
	
	private void DoRemove() {
	 	if (list.getSelectionCount()>0) 
	 	{
			list.setRedraw(false);
			list.remove(list.getSelectionIndices());
			sortList();
			list.setRedraw(true);
			RemovePersistentClasses.setEnabled(false);			
			if (list.getItemCount()==0)
				getWizard().getContainer().updateButtons();
	 	}
	}

	public String getItem(int i)
	{
		return list.getItem(i);//return element i rom list
	}

	public int getItemCount()
	{
		return list.getItemCount();//return elements count
	}
	public boolean canFlipToNextPage() {
		if (list.getItemCount()==0)
			return false;
		else
		return true;
	}

}
