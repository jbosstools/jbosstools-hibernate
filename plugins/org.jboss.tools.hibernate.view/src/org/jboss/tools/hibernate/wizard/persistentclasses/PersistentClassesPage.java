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
package org.jboss.tools.hibernate.wizard.persistentclasses;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
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
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.dialog.ModelCheckedTreeSelectionDialog;
import org.jboss.tools.hibernate.wizard.treemodel.PersistentClassesLabelProvider;
import org.jboss.tools.hibernate.wizard.treemodel.TreeModel;
import org.jboss.tools.hibernate.wizard.treemodel.TreeModelContentProvider;




/**
 * @author kaa
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PersistentClassesPage extends WizardPage {
	public static final String BUNDLE_NAME = "persistentclasses"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(PersistentClassesPage.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private IMapping mod;	
    private Button PersistentClassesButton;
	private Button RemovePersistentClasses;
    private List list;
	private TreeModel allClasses;

    
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 3;
		container.setLayout(layout);
		
		Label label = new Label(container, SWT.NULL);
		label.setText(BUNDLE.getString("PersistentClassesPage.label"));
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
		PersistentClassesButton.setText(BUNDLE.getString("PersistentClassesPage.button"));
		data=  new GridData(SWT.FILL, SWT.BEGINNING, false,false, 1, 1);
		PersistentClassesButton.setLayoutData(data);	
		PersistentClassesButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				DoBrowse();	
			}
		});
		RemovePersistentClasses= new Button(container, SWT.PUSH);
		data=  new GridData(SWT.FILL, SWT.BEGINNING, false,false, 1, 1);
		RemovePersistentClasses.setText(BUNDLE.getString("PersistentClassesPage.removebutton"));
		RemovePersistentClasses.setLayoutData(data);
		RemovePersistentClasses.setEnabled(false);
		RemovePersistentClasses.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DoRemove();	
			}

		});	
		setControl(container);		
	}
	public PersistentClassesPage(IMapping mod){
		super("wizardPage");
		setTitle(BUNDLE.getString("PersistentClassesPage.title"));
		setDescription(BUNDLE.getString("PersistentClassesPage.description"));
		this.mod = mod;
	}
	
	public void DoBrowse(){
	    IJavaElement[] obj=null;
	    IJavaElement[] elemobj=null;
	    IJavaElement[] elemfragobj=null;
		allClasses = new TreeModel("root",null);		
//	    ArrayList objects=new ArrayList();
	    ArrayList readyobjects=new ArrayList();
		IProject project = null;
		
		if (mod!=null)
		{
   	 	project =  mod.getProject().getProject();
   	 	IPersistentClass[] PertsistentClassesobj=mod.getPertsistentClasses();
	 	for (int i = 0; i < PertsistentClassesobj.length; i++)
	 	{
	 		readyobjects.add(PertsistentClassesobj[i].getName());	
	 	}
 		 IJavaProject javaProject = JavaCore.create(project);
 		 try {
 		 	// changed by Nick 28.06.2005
             //obj=javaProject.getChildren();
             obj=javaProject.getAllPackageFragmentRoots();
             // by Nick
         } catch (JavaModelException e) {
         	//TODO (tau-tau) for Exception        	 
			ExceptionHandler.handle(e, getShell(), null, null);			
			//e.printStackTrace();
		}
		
	 	for (int i = 0; i < obj.length; i++)	
		if (obj[i] instanceof  IPackageFragmentRoot /*ICompilationUnit*/) {
			IPackageFragmentRoot elem;
			elem =(IPackageFragmentRoot)obj[i];
			try {
				elemobj=elem.getChildren();
				for (int z = 0; z < elemobj.length; z++)
					if (elemobj[z] instanceof  IPackageFragment) {
						elemfragobj=((IPackageFragment)elemobj[z]).getChildren();
						for (int m = 0; m < elemfragobj.length; m++)
						if (elemfragobj[m] instanceof  ICompilationUnit) {
							//add classes to list
							if (((ICompilationUnit)elemfragobj[m]).findPrimaryType()!=null)
							if ((!readyobjects.contains(elemobj[z].getElementName()+"."+(((ICompilationUnit)elemfragobj[m]).findPrimaryType().getElementName())))//GetTypes
								&& (list.indexOf(elemobj[z].getElementName()+"."+(((ICompilationUnit)elemfragobj[m]).findPrimaryType().getElementName()))<0))
							{
								
								 if (allClasses.getNode(elemobj[z].getElementName())==null)
									 	allClasses.addNode(elemobj[z].getElementName());
									 TreeModel classnode = (TreeModel) allClasses.getNode(elemobj[z].getElementName());					 
									 classnode.addNode((((ICompilationUnit)elemfragobj[m]).findPrimaryType().getElementName()));
								
//							objects.add(elemobj[z].getElementName()+"."+(((ICompilationUnit)elemfragobj[m]).findPrimaryType().getElementName()));
							}
					}	
					}
			} catch (JavaModelException e1) {
            	//TODO (tau-tau) for Exception				
				ExceptionHandler.handle(e1, getShell(), null, null);
				//e1.printStackTrace();
			}
	 	}
		}
		//make new dialog
		
		ModelCheckedTreeSelectionDialog dlg = new ModelCheckedTreeSelectionDialog(
				getShell(), new PersistentClassesLabelProvider(),
				new TreeModelContentProvider()
				) {
		};
		dlg.setTitle(BUNDLE.getString("PersistentClassesPage.dialogtitle"));
		dlg.setMessage(BUNDLE.getString("PersistentClassesPage.dialogtext"));
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
