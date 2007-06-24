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

import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
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
import org.jboss.tools.hibernate.dialog.xpl.CreatePackageDialog;


/**
 * @author kaa
 * akuzmin@exadel.com
 * Aug 16, 2005
 */
public class GenerateDAOPage2 extends WizardPage {
	private IMapping mod;
	private Button isGenerateInterfaces;
	private Button isGenerateLogging;
	private Button isGenerateTest;	
	private Text PackageText;
	private String namePackage;
	private Button BrowseButton;
	private boolean checkLogging;
	private boolean checkInterfaces;
	private boolean checkTest;	
	public static final String BUNDLE_NAME = "generateDAO"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(GenerateDAOPage2.class.getPackage().getName() + "." + BUNDLE_NAME);	
	
	protected GenerateDAOPage2(IMapping mod) {
		super("wizardPage");
		setTitle(BUNDLE.getString("GenerateDAOPage2.title"));
		setDescription(BUNDLE.getString("GenerateDAOPage2.description"));
		this.mod = mod;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		container.setBounds(0,0,convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_MARGIN));
		container.setLayout(layout);
		
	    GridData data =   new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.horizontalSpan = 3;
		Label label = new Label(container, SWT.WRAP);		
		label.setText(BUNDLE.getString("GenerateDAOPage2.label"));
		label.setLayoutData(data);

	    data =   new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.horizontalSpan = 1;
		Label label5 = new Label(container, SWT.WRAP);		
		label5.setText("");
		label5.setLayoutData(data);
		
	    data =   new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.horizontalSpan = 2;
		Label label1 = new Label(container, SWT.WRAP);		
		label1.setText(BUNDLE.getString("GenerateDAOPage2.label1"));
		label1.setLayoutData(data);

	    data =   new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.horizontalSpan = 2;
		Label label4 = new Label(container, SWT.WRAP);		
		label4.setText("");
		label4.setLayoutData(data);
		
	    data =   new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.horizontalSpan = 4;
	    data.horizontalIndent=8;
	//Generate mapping check box	
		isGenerateInterfaces= new Button(container, SWT.CHECK);
		isGenerateInterfaces.setText(BUNDLE.getString("GenerateDAOPage2.isGenerateInterfaces"));
		isGenerateInterfaces.setLayoutData(data);
		isGenerateInterfaces.setSelection(false);

		isGenerateInterfaces.addSelectionListener(new SelectionAdapter() {
		
			public void widgetSelected(SelectionEvent e) {
				DoCheckInterfaces();
			}
		});
		
	    data =   new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.horizontalSpan = 4;
	    data.horizontalIndent=8;

		isGenerateLogging= new Button(container, SWT.CHECK);
		isGenerateLogging.setText(BUNDLE.getString("GenerateDAOPage2.isGenerateLogging"));
		isGenerateLogging.setLayoutData(data);
		isGenerateLogging.setSelection(false);

		isGenerateLogging.addSelectionListener(new SelectionAdapter() {
		
			public void widgetSelected(SelectionEvent e) {
				DoCheckLogging();
			}
		});
		
	    data =   new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.horizontalSpan = 4;
	    data.horizontalIndent=8;

		isGenerateTest= new Button(container, SWT.CHECK);
		isGenerateTest.setText(BUNDLE.getString("GenerateDAOPage2.isGenerateTest"));
		isGenerateTest.setLayoutData(data);
		isGenerateTest.setSelection(false);

		isGenerateTest.addSelectionListener(new SelectionAdapter() {
		
			public void widgetSelected(SelectionEvent e) {
				DoCheckTest();
			}
		});
		
	    GridData Label2Data =   new GridData();
	    Label2Data.horizontalAlignment = GridData.FILL;
	    Label2Data.horizontalSpan = 1;
		
		Label label2 = new Label(container, SWT.NULL);		
		label2.setText(BUNDLE.getString("GenerateDAOPage2.label2"));
		label2.setLayoutData(Label2Data);
		
		
	    GridData PackageTextData =   new GridData();
	    PackageTextData.horizontalAlignment = GridData.FILL;
	    PackageTextData.grabExcessHorizontalSpace=true;
	    PackageTextData.horizontalSpan = 2;
		
		PackageText = new Text(container, SWT.BORDER | SWT.SINGLE);
		PackageText.setBounds(85,70,180,20);
		PackageText.setLayoutData(PackageTextData);
		namePackage=(mod.getConfiguration().getProperty("hibernate.default_package"));
		if(namePackage==null)
			namePackage="";
		if(namePackage=="")
			setErrorMessage(BUNDLE.getString("GenerateDAOPage2.ErrorMessage"));
		PackageText.setText(namePackage);
		PackageText.addModifyListener(new ModifyListener(){
				
			public void modifyText(ModifyEvent e) {
				Text text = (Text) e.widget;
				namePackage = text.getText();
				IStatus status = org.eclipse.jdt.core.JavaConventions.validatePackageName(namePackage);
				setMessageStatus(status);
				// add tau 20.01.2006
				if (status.isOK()){
					setPageComplete(true);
				} else {
					setPageComplete(false);
				}
			}
		}); 
		
		BrowseButton= new Button(container, SWT.PUSH);
//		GridData d=setButtonLayoutData( BrowseButton);
		GridData d=  new GridData(SWT.END, SWT.BEGINNING, false,false, 1, 1);
		BrowseButton.setLayoutData(d);
		BrowseButton.setText(BUNDLE.getString("GenerateDAOPage2.TablesClassesButton"));
		BrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DoBrowse();
			}
		});		
	
		setControl(container);		
	}

	private void DoCheckInterfaces() {
		checkInterfaces=!checkInterfaces;
			
	}	

	private void DoCheckLogging() {
		checkLogging=!checkLogging;
			
	}	

	private void DoCheckTest() {
		checkTest=!checkTest;
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
		IJavaProject fCurrJProject =  JavaCore.create(mod.getProject().getProject());//CONSIDER_REQUIRED_PROJECTS
		SelectionDialog dialog1 = CreatePackageDialog.createPackageDialog(getShell(),fCurrJProject,IJavaElementSearchConstants.CONSIDER_REQUIRED_PROJECTS,null);
		dialog1.setTitle(BUNDLE.getString("GenerateDAOPage2.TitleSelectPackage"));
	
		
		if (dialog1.open() == Window.OK) {
			Object resource = dialog1.getResult()[0];
			
			IPackageFragment packageResource = (IPackageFragment) resource;
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
		 getWizard().getContainer().updateButtons();		
}

	public void setMessageStatus(IStatus status) {
		if (status != null && !status.isOK()) {
			String message= status.getMessage();
			if (message != null && message.length() > 0){ 
				if(status.matches(IStatus.ERROR))
					setErrorMessage(message);
				else if(status.matches(IStatus.WARNING)){
						
						setErrorMessage(null);
						setMessage(message,IStatus.WARNING);
						
				}
				else if(status.matches(IStatus.INFO)){
					
					setErrorMessage(null);
					setMessage(message,IStatus.INFO);
				}

			}
		}
		else {
			setMessage( BUNDLE.getString("GenerateDAOPage2.Message"),  0);//
			setErrorMessage(null);
			}

		 getWizard().getContainer().updateButtons();		
	}

	/**
	 * @return Returns the checkInterfaces.
	 */
	public boolean isCheckInterfaces() {
		return checkInterfaces;
	}

	/**
	 * @return Returns the checkLogging.
	 */
	public boolean isCheckLogging() {
		return checkLogging;
	}

	/**
	 * @return Returns the checkTest.
	 */
	public boolean isCheckTest() {
		return checkTest;
	}	

	public void setErrorMessage(String newMessage) {
		super.setErrorMessage(newMessage);
//		setPageComplete(false);
//		getWizard().getContainer().updateButtons();		
	}
	public void setMessage(String newMessage, int newType) {//INFORMATION,WARNING
		super.setMessage(newMessage, newType);
//		setPageComplete(true);
//		if ((getWizard()!=null)&&(getWizard().getContainer()!=null)&&(newMessage!=null))
//    	getWizard().getContainer().updateButtons();		
	}

	// del tau 20.01.2006
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	/*
	public boolean canFlipToNextPage() {
		IStatus status=org.eclipse.jdt.core.JavaConventions.validatePackageName(PackageText.getText());
		if(status.isOK())
			return true;
		else return false;
	}
	*/
}
