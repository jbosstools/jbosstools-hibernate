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
package org.jboss.tools.hibernate.wizard.hibernateconnection;

import java.util.ArrayList;
import java.util.ResourceBundle;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
//import org.eclipse.jdt.internal.ui.viewsupport.ListContentProvider; del for Eclipse 3.2
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ListDialog;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
//import org.eclipse.ui.internal.dialogs.ListContentProvider;

/**
 * @author sushko
 * Hibernate Connection Wizard page4(custom connection provider)
 */
public class HibernateConnectionWizardPage4 extends WizardPage {
	
	public static final String BUNDLE_NAME = "hibernateconnection"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(HibernateConnectionWizardPage1.class.getPackage().getName() + "." + BUNDLE_NAME);
	private Text PackageText;
	private IMapping mapping;
   private String classNameProvider;
    private Button BrowseButton;
   // private Button GenerateButton;
    private ArrayList<String> resultList = new ArrayList<String>();

	/** createControl for the  Page4
	 ** @param parent
	 **/
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		initializeDialogUnits(parent);
		classNameProvider=mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage4.property_hibernate_connection_provider_class"));
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		container.setLayout(layout);
		
	    GridData groupData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		Label label1 = new Label(container, SWT.NULL);		
		label1.setText(BUNDLE.getString("HibernateConnectionWizardPage4.label1"));
		groupData.horizontalSpan =3;
		label1.setLayoutData(groupData);
		
		Label label2 = new Label(container, SWT.NULL);		
		label2.setText(BUNDLE.getString("HibernateConnectionWizardPage4.label2"));
		label2.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		PackageText = new Text(container, SWT.BORDER | SWT.SINGLE);
		PackageText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false,1,1));
		PackageText.setText(classNameProvider);
		PackageText.addModifyListener(new ModifyListener()
					{
			public void modifyText(ModifyEvent e) {
					Text text = (Text) e.widget;
					if(text.getText()!=null)
						classNameProvider=text.getText();
						isPageComplete();
				}
		}); 

		
		BrowseButton= new Button(container, SWT.PUSH);
		BrowseButton.setText(BUNDLE.getString("HibernateConnectionWizardPage4.BrowseButton"));
		GridData gd=setButtonLayoutData( BrowseButton);
		//GridData gd=new GridData(GridData.HORIZONTAL_ALIGN_END);
		//gd.heightHint=23;
		//gd.widthHint= 80;
		BrowseButton.setLayoutData(gd);
		BrowseButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
			    IJavaProject javaProject =  JavaCore.create(mapping.getProject().getProject());
			    resultList.clear();
				//ArrayList resultList=new ArrayList();
				try {
				    // changed by Nick 29.06.2005
                    IPackageFragmentRoot[] roots = javaProject.getAllPackageFragmentRoots();
                    for (int i = 0; i < roots.length; i++) {
                        IPackageFragmentRoot root = roots[i];
                        IJavaElement[] packageFragments = root.getChildren();
                        for (int j = 0; j < packageFragments.length; j++) {
                            if (packageFragments[j].getElementType() == IJavaElement.PACKAGE_FRAGMENT)
                            {
                                IPackageFragment fragment = (IPackageFragment) packageFragments[j];

                                for (int k=0;k< fragment.getCompilationUnits().length;k++){
                                    IType itype=fragment.getCompilationUnits()[k].findPrimaryType();
                                    if (itype!=null){
                                        for (int m = 0; m < itype.getSuperInterfaceNames().length; m++)
                                        {   
                                            String interf= itype.getSuperInterfaceNames()[m].toString().toLowerCase();
                                            if (interf.indexOf(BUNDLE.getString("HibernateConnectionWizardPage4.ConnectionProvider").toLowerCase())>=0){
                                            	if(resultList.indexOf(itype.getElementName())<0 && !PackageText.getText().equals(itype.getElementName())
                                            			||resultList.indexOf(itype.getElementName())<0 && PackageText.getText().equals(""))//!mapping.getConfiguration().getProperty("hibernate.connection.provider_class").equals(itype.getElementName()))
                                            		resultList.add(itype.getElementName());
                                            }
                                            	//if (interf.indexOf("Comparator".toLowerCase())>=0)
                                            
                                            
                                        }
                                    }
                                }   
                            
                            }
                        }
                    }
                    
//				    for (int i=0;i< javaProject.getPackageFragments().length;i++){
//				        for (int k=0;k< javaProject.getPackageFragments()[i].getCompilationUnits().length;k++){
//				            IType itype=javaProject.getPackageFragments()[i].getCompilationUnits()[k].findPrimaryType();
//				            itype.getElementName();
//				            if (itype!=null){
//				                for (int m = 0; m < itype.getSuperInterfaceNames().length; m++)
//				                {	
//				                    String interf= itype.getSuperInterfaceNames()[m].toString().toLowerCase();
//				                    if (interf.indexOf(BUNDLE.getString("HibernateConnectionWizardPage4.ConnectionProvider").toLowerCase())>=0)
////				                        if (interf.indexOf("Comparator".toLowerCase())>=0)
//				                    {resultList.add(itype.getElementName());
//				                    };
//				                }
//				            }
//				        }	
//				    }

                    // by Nick
                    
				} catch (JavaModelException e1) {
	            	//TODO (tau-tau) for Exception					
					ExceptionHandler.logThrowableError(e1, e1.getMessage());
				}
				
				String txt=setProviderText();
				if(txt!=null){
					PackageText.setText(txt);
					setErrorMessage(null);
					setPageComplete(true);
				}
				getWizard().getContainer().updateButtons();
				
//				ListDialog dialog2= new ListDialog(getShell());
//				dialog2.setTitle(BUNDLE.getString("HibernateConnectionWizardPage4.selectclass"));
//				dialog2.setAddCancelButton(true);
//				dialog2.setLabelProvider(new LabelProvider());
//				dialog2.setContentProvider(new ListContentProvider());
//				dialog2.setMessage(BUNDLE.getString("HibernateConnectionWizardPage4.selectclassfromlist"));
//				dialog2.setInput(resultList);
//				int res=dialog2.open();
//				if (res==0){
//					Object[] classname =  dialog2.getResult();//[0];
//					PackageText.setText(classname[1].toString());
//					setErrorMessage(null);
//					setPageComplete(true);
//					getWizard().getContainer().updateButtons();				
//				}
				
				} // end widgetSelected
			
				//XXX (sushko)? Antonov  What should be done by pressing Browse Button of page4?
				//Execute dialog to let user select a Java class from the current project
				//DoCheck();
		});		
	
		setControl(container);		

	}
	
	
/** canFlipToNextPage of the HibernateConnectionWizardPage4
 * 
**/
	public boolean canFlipToNextPage() {
		return false;
	}

/** setErrorMessage() for the  HibernateConnectionWizardPage4
 * @param newMessage
**/
	public void setErrorMessage(String newMessage) {
		super.setErrorMessage(newMessage);
}

	/** setPageComplete() for the  HibernateConnectionWizardPage4
	 * @param complete
	**/
	public void setPageComplete(boolean complete) {
		super.setPageComplete(complete);
	}
	
/** isPageComplete() for the  HibernateConnectionWizardPage4
 * 
**/
	public boolean isPageComplete() {
		if (classNameProvider!="" && getErrorMessage()==null)
			return true;
		else return false;
	}
	
/** constructor of the HibernateConnectionWizardPage4
 * @param ormmodel
 **/
	public HibernateConnectionWizardPage4 (IMapping mapping) {
		super("wizardPage4");
		setTitle(BUNDLE.getString("HibernateConnectionWizardPage4.Title"));
		setDescription(BUNDLE.getString("HibernateConnectionWizardPage4.Description"));
		this.mapping = mapping;
	}

	/** saveConfiguration()
	 * The all necessary data are saved here in to OrmModel
	 **/
	public void saveConfiguration() {
		mapping.getConfiguration().setProperty(BUNDLE.getString("HibernateConnectionWizardPage4.property_hibernate_connection_provider_class"),classNameProvider);
	}
	
	public String setProviderText(){
		final ListDialog dialog2= new ListDialog(getShell()){
			protected Control createDialogArea(Composite container){// added 8/8/2005
				Control toReturn = super.createDialogArea(container);
				super.getTableViewer().addSelectionChangedListener(new ISelectionChangedListener(){

					public void selectionChanged(SelectionChangedEvent event) {
						getOkButton().setEnabled(true);
						
					}
					
				});
				return toReturn;
			}
		};
		dialog2.setTitle(BUNDLE.getString("HibernateConnectionWizardPage4.selectclass"));
		dialog2.setAddCancelButton(true);
		dialog2.setLabelProvider(new LabelProvider());
		dialog2.setContentProvider(new ListContentProvider());
		dialog2.setMessage(BUNDLE.getString("HibernateConnectionWizardPage4.selectclassfromlist"));
		dialog2.setInput(resultList);
		dialog2.create();// added 8/8/2005
		//if(resultList.isEmpty())
			dialog2.getOkButton().setEnabled(false);
		if (dialog2.open() == IDialogConstants.CANCEL_ID)
	        return null;

		
		
			Object[] classname =  dialog2.getResult();//[0];
			if (classname == null || classname.length == 0)
				return null;
//			setErrorMessage(null);
//			setPageComplete(true);
//			getWizard().getContainer().updateButtons();		
		    return classname[0].toString();       
		
					
		
	}
	
	public void setProvider(){
		classNameProvider=mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage4.property_hibernate_connection_provider_class"));
		PackageText.setText(classNameProvider);
		
	}

}
