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
package org.jboss.tools.hibernate.wizard.tablesclasses;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.dialog.AutoMappingSetting;
import org.jboss.tools.hibernate.dialog.xpl.CreatePackageDialog;


/**
 * @author sushko and Gavrs
 *
 * TODO sushko Put more appropriate comments in all classes. 
 */
public class TablesClassesWizardPage2 extends WizardPage {

	public static final String BUNDLE_NAME = "tablesclasses"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(TablesClassesWizardPage1.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private IMapping ormmodel;
    private Button isGenerate;//,isUseHeuristicAlgorithms;
    private Text PackageText;
    private Button BrowseButton;
    private Button HibernateButton;
    public boolean checkGener,checkHeuristic;
    private  String namePackage;
    private boolean errorFlag=true;
    private IPackageFragmentRoot[] roots;
    private  IJavaProject fCurrJProject;
    private List<IPackageFragmentRoot> consideredRoots;
    IPackageFragment packageResource;
    public TablesClassesWizardPage2(IMapping ormmodel) {
		super("wizardPage");
		setTitle(BUNDLE.getString("TablesClassesWizardPage2.Title"));
		setDescription(BUNDLE.getString("TablesClassesWizardPage2.Description"));
		this.ormmodel = ormmodel;
		init();
	}
    
	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		container.setBounds(0,0,convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_MARGIN));
		container.setLayout(layout);
		
	    GridData data =   new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.horizontalSpan = 3;
		Label label = new Label(container, SWT.WRAP);		
		label.setText(BUNDLE.getString("TablesClassesWizardPage2.label"));
		label.setLayoutData(data);
		
	    data =   new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.horizontalSpan = 3;
		Label label1 = new Label(container, SWT.WRAP);		
		label1.setText(BUNDLE.getString("TablesClassesWizardPage2.label1"));
		label1.setLayoutData(data);

	    data =   new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.horizontalSpan = 3;
	    data.horizontalIndent=8;
	//Generate mapping check box	
		isGenerate= new Button(container, SWT.CHECK);
		isGenerate.setText(BUNDLE.getString("TablesClassesWizardPage2.isGenerate"));
		isGenerate.setLayoutData(data);
		isGenerate.setSelection(true);
		checkGener=true;
		checkHeuristic=false;
//		isUseHeuristicAlgorithms= new Button(container, SWT.CHECK);
//		data = new GridData();
//		data.horizontalSpan=3;
//		data.horizontalIndent=8;
//		isUseHeuristicAlgorithms.setLayoutData(data);
//		isUseHeuristicAlgorithms.setText("Use Heuristical Algorithms");
//		isUseHeuristicAlgorithms.addSelectionListener(new SelectionAdapter() {
//		
//			public void widgetSelected(SelectionEvent e) {
//				DoCheckHeuristic();
//			}
//		});

		isGenerate.addSelectionListener(new SelectionAdapter() {
		
			public void widgetSelected(SelectionEvent e) {
				DoCheckGener();
			}
		});

		
	    GridData Label2Data =   new GridData();
	    Label2Data.horizontalAlignment = GridData.FILL;
	    Label2Data.horizontalSpan = 1;
		
		Label label2 = new Label(container, SWT.NULL);		
		label2.setText(BUNDLE.getString("TablesClassesWizardPage2.label2"));
		label2.setLayoutData(Label2Data);
		
		
	    GridData PackageTextData =   new GridData();
	    PackageTextData.horizontalAlignment = GridData.FILL;
	    PackageTextData.horizontalSpan = 1;
		
		PackageText = new Text(container, SWT.BORDER | SWT.SINGLE);
		PackageText.setBounds(85,70,180,20);
		PackageText.setLayoutData(PackageTextData);
		namePackage=(ormmodel.getConfiguration().getProperty("hibernate.default_package"));
		if(namePackage==null)
			namePackage="";
		if(namePackage=="")
			setErrorMessage(BUNDLE.getString("TablesClassesWizardPage2.NoPackageMessage"));
		PackageText.setText(namePackage);
		PackageText.addModifyListener(new ModifyListener(){
				
			public void modifyText(ModifyEvent e) {
				String nameParent;
				Text text = (Text) e.widget;
				namePackage=text.getText();
				IStatus status=org.eclipse.jdt.core.JavaConventions.validatePackageName(namePackage);
					setMessageStatus(status);
					String OS = System.getProperty("os.name").toLowerCase();
					if (OS.indexOf("windows") > -1) 
						packageNameToLowerCase();
						
					if(packageResource!=null)
						nameParent=packageResource.getParent().getElementName();
					else nameParent="src";
					if(fCurrJProject.getProject().getFile(nameParent+"/"+namePackage).exists() || fCurrJProject.getProject().getFile("src"+"/"+namePackage).exists()){
						setMessage(BUNDLE.getString("TablesClassesWizardPage2.ResourceExistMessage"),IStatus.ERROR);
						setErrorMessage(BUNDLE.getString("TablesClassesWizardPage2.ResourceExistMessage"));
					}
				
			}
		}); 
		BrowseButton= new Button(container, SWT.PUSH);
		GridData d=setButtonLayoutData( BrowseButton);
		BrowseButton.setLayoutData(d);
		BrowseButton.setText(BUNDLE.getString("TablesClassesWizardPage2.TablesClassesButton"));
		BrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DoBrowse();
			}
		});		
	
	    GridData Label4Data =   new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		
	    Label4Data.horizontalAlignment = GridData.FILL;
	    Label4Data.horizontalSpan = 2;
		
		Label label4 = new Label(container, SWT.NULL);		
		label4.setText(BUNDLE.getString("TablesClassesWizardPage2.label4"));
		label4.setLayoutData(Label4Data);

	    GridData HibernateButtonData =   new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
	    HibernateButtonData.horizontalSpan = 1;
		HibernateButton= new Button(container, SWT.PUSH);
		HibernateButton.setText(BUNDLE.getString("TablesClassesWizardPage2.TablesClassesButtonHib"));
		HibernateButton.setLayoutData(HibernateButtonData);
		HibernateButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Dialog dlg=new AutoMappingSetting(getShell(),ormmodel.getProject());
			 	dlg.open();
			}
		});	
		this.setPageComplete(true);
		setMessage(  BUNDLE.getString("TablesClassesWizardPage2.Message"),  0);
		setControl(container);		
	}
	
	/**
	 * constructor of the TablesClassesWizardPage2
	 * @param ormmodel
	 * 
	 */
	
	
	/**
	 * canFlipToNextPage of the TablesClassesWizardPage2
	 */
	public boolean canFlipToNextPage() {
		return  false;
	}
	
	/**
	 * isPageComplete of the TablesClassesWizardPage2
	 */
	public boolean isPageComplete() {
		
		if(errorFlag)
			return false;
		else return true;
		
	}
	private void DoCheckGener() {
		checkGener=!checkGener;
		
			
	}	

	public String getPackage() {
		return PackageText.getText();
		
	}
	public String getNamePackage()
	{
		return namePackage;
	}
	public void DoBrowse(){
		try{
			//IPackageFragment pf = null;
		//IJavaProject fCurrJProject =  JavaCore.create(ormmodel.getProject().getProject());//CONSIDER_REQUIRED_PROJECTS
		//SelectionDialog dialog1 = JavaUI.createPackageDialog(getShell(),fCurrJProject,IJavaElementSearchConstants.CONSIDER_REQUIRED_PROJECTS,"Default");
		SelectionDialog dialog1 = CreatePackageDialog.createPackageDialog(getShell(),fCurrJProject,IJavaElementSearchConstants.CONSIDER_REQUIRED_PROJECTS,null);
		dialog1.setTitle(BUNDLE.getString("TablesClassesWizardPage2.TitleSelectPackage"));
	
		
		if (dialog1.open() == Window.OK) {
			Object resource = dialog1.getResult()[0];
			
		    packageResource = (IPackageFragment) resource;
			PackageText.setFocus();	
			namePackage=packageResource.getElementName();
			IStatus status=org.eclipse.jdt.core.JavaConventions.validatePackageName(namePackage);
			if(status.isOK())
			PackageText.setText(namePackage);
			else setMessageStatus(status);
				//MessageDialog.openError(getShell(), null, status.getMessage());
			
		}
		else{
			PackageText.setFocus();
			PackageText.setText(namePackage);
		}
		}catch(Exception ex){
        	//TODO (tau-tau) for Exception			
			ExceptionHandler.logThrowableError(ex, ex.getMessage());
		}
}



	public void setNext(){
	
		this.getWizard().getContainer().updateButtons();
	}

public void setErrorMessage(String newMessage) {
		super.setErrorMessage(newMessage);
		errorFlag=true;
		this.setPageComplete(false);
}
public void setMessage(String newMessage, int newType) {//INFORMATION,WARNING
    super.setMessage(newMessage, newType);
    
}





public void setMessageStatus(IStatus status) {
	if (status != null && !status.isOK()) {
		String message= status.getMessage();
		if (message != null && message.length() > 0){ 
			if(status.matches(IStatus.ERROR))
				setErrorMessage(message);
			else if(status.matches(IStatus.WARNING)){
					
					setErrorMessage(null);
					errorFlag=false;
					this.setPageComplete(true);
					setMessage(message,IStatus.WARNING);
					
			}
			else if(status.matches(IStatus.INFO)){
				
				setErrorMessage(null);
				errorFlag=false;
				this.setPageComplete(true);
				setMessage(message,IStatus.INFO);
			}
			return;
		}
	}
	else {
		setMessage( BUNDLE.getString("TablesClassesWizardPage2.Message"),  0);//
		setErrorMessage(null);
		errorFlag=false;
		this.setPageComplete(true);
		}
	return;
		
}	



public boolean packageNameToLowerCase(){
	boolean result=true;
	IJavaElement[] h=null;
	String namepack;
		try {
	
			Iterator iter= consideredRoots.iterator();
			while(iter.hasNext()) {
				IPackageFragmentRoot root= (IPackageFragmentRoot)iter.next();
				h = root.getChildren();
				for( int i=0;i<h.length;i++){
					namepack=h[i].getElementName();
					if(!namePackage.equals(namepack))
						if(namePackage.equalsIgnoreCase(namepack)&& !namePackage.equals("") ){
						
						namePackage=h[i].getElementName();
						setMessage(BUNDLE.getString("TablesClassesWizardPage2.PackageExistMessage"),IStatus.WARNING);
						//PackageText.setText(namePackage);
						//return false;			
					}else result=true;
				}	
			}
		} catch (JavaModelException ex) {
        	//TODO (tau-tau) for Exception			
		}
	return result;
}

 private void init(){
	 try {
		 fCurrJProject =  JavaCore.create(ormmodel.getProject().getProject());
		roots= fCurrJProject.getPackageFragmentRoots();
	} catch (JavaModelException ex) {
    	//TODO (tau-tau) for Exception		
		ExceptionHandler.logThrowableError(ex, ex.getMessage());
	}
		consideredRoots= new ArrayList<IPackageFragmentRoot>(roots.length);
		for (int i= 0; i < roots.length; i++) {
			IPackageFragmentRoot root= roots[i];
				consideredRoots.add(root);
		}
								
 }

 public boolean getErrorFlag(){
	 return errorFlag;
 }
 public boolean getCheckGenerate(){
	 return checkGener;
 }
 public  boolean getCheckHeuristic(){
	 return checkHeuristic;
 }
	
 
}
