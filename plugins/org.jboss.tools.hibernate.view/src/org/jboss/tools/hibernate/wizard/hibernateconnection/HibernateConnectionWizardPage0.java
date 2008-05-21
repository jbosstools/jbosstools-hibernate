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

import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateConfiguration;
import org.jboss.tools.hibernate.internal.core.properties.PropertySourceWrapper;



/**
 * @author sushko
 *
 */
public class HibernateConnectionWizardPage0 extends WizardPage {

	public static final String BUNDLE_NAME = "hibernateconnection"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(HibernateConnectionWizardPage1.class.getPackage().getName() + "." + BUNDLE_NAME);
	private IMapping mapping;
	private Text FileName;
	private Button SaveAsButton;
	private PropertySheetPage pageGenConf;
	private PropertySourceWrapper generalHibConf;
	boolean set=true;
	private String strPath;
	boolean wrongNameFile=false;
	private boolean newConfig=false;
	
	
	
	/**
	 * @param pageName
	 */
	public HibernateConnectionWizardPage0(IMapping mapping) {
		super(BUNDLE.getString("HibernateConnectionWizard.Title"));
		setTitle(BUNDLE.getString("HibernateConnectionWizard.Title"));
		setDescription(BUNDLE.getString("HibernateConnectionWizardPage0.Description"));
		this.mapping = mapping;
	}

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public HibernateConnectionWizardPage0(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

/**
 * @param parent
 */	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		 initializeDialogUnits(container);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);
		
	    Group grUrlInf = new Group(container, SWT.NONE);
		GridLayout Grlayout = new GridLayout();
		Grlayout.numColumns = 3;
		grUrlInf.setLayout(Grlayout);
	    GridData groupData =   new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
	    grUrlInf.setLayoutData(groupData);
		
		Label lblFileName = new Label(grUrlInf, SWT.NULL);		
		lblFileName.setText(BUNDLE.getString("HibernateConnectionWizardPage0.FileName"));
		lblFileName.setLayoutData(new GridData(SWT.BEGINNING, SWT.NONE, false, false, 1, 1));
		
		FileName = new Text(grUrlInf, SWT.BORDER | SWT.SINGLE);
		FileName.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
		FileName.setText(mapping.getConfiguration().getResource().getFullPath().toString());
		strPath=mapping.getConfiguration().getResource().getFullPath().toString();
		FileName.addModifyListener(new ModifyListener()
				{
			public void modifyText(ModifyEvent e) {
				IPath shortPath;
				int count;
				String projName="/"+mapping.getProject().getName()+"/";
				Text text = (Text) e.widget;
				strPath=text.getText();
				int ind=strPath.indexOf(projName);
				if (ind==-1){
					setErrorMessage(BUNDLE.getString("HibernateConnectionWizardPage0.ErrorMessageValidName"));
					wrongNameFile=true;
					getWizard().getContainer().updateButtons();
					FileName.setFocus();
					isPageComplete();
					return;
				}
				IPath p=new Path(strPath);
				count=p.matchingFirstSegments(p);
				if(count>1)
					shortPath=p.removeFirstSegments(1);
				else {
					setErrorMessage(BUNDLE.getString("HibernateConnectionWizardPage0.ErrorMessageEnterName"));
					wrongNameFile=true;
					getWizard().getContainer().updateButtons();
					FileName.setFocus();
					isPageComplete();
					return;
				}
					IFile file = mapping.getProject().getProject().getFile(shortPath);
					// #changed# by Konstantin Mishin on 13.01.2006 fixed for ESORM-403
					//if(file.exists()){
					if(file.exists() && !file.getFullPath().equals(mapping.getConfiguration().getResource().getFullPath())){
					// #changed#
						setErrorMessage(BUNDLE.getString("HibernateConnectionWizardPage0.ErrorMessageFileExist"));
						wrongNameFile=true;
						getWizard().getContainer().updateButtons();
						FileName.setFocus();
						
					}else {
						setErrorMessage(null);
						setMessage(null);
						wrongNameFile=false;
						getWizard().getContainer().updateButtons();
					}
					if(count>2){
						if ( checkClassPath(p.removeLastSegments(1).toString())== false){
							setMessage(BUNDLE.getString("HibernateConnectionWizardPage0.ClassPathMessage"),2);
							getWizard().getContainer().updateButtons();
						}else{ 
							setMessage(null);
							getWizard().getContainer().updateButtons();
						}
					}
					
			}
		}); 
		SaveAsButton= new Button(grUrlInf, SWT.PUSH);
		SaveAsButton.setText(BUNDLE.getString("HibernateConnectionWizardPage0.SaveAs"));
		GridData d=setButtonLayoutData( SaveAsButton);
		SaveAsButton.setLayoutData(d);
		SaveAsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doSaveAs();
				getWizard().getContainer().updateButtons();
			}
		});	
		
		//String nameDriver=mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.connection.driver_class"));
		String nameDialect=mapping.getConfiguration().getProperty("hibernate.dialect");
		if(nameDialect.equals("")){
			// #changed# by Konstantin Mishin on 27.03.2006 fixed for ESORM-477
			//mapping.getConfiguration().setProperty("hibernate.dialect","org.hibernate.dialect.MySQLDialect");
			mapping.getConfiguration().setPropertyValue("hibernate.dialect","org.hibernate.dialect.MySQLDialect");
			// #changed#
			newConfig=true;
		}

		//if(nameDriver.equals(""))
			//generalHibConf=((HibernateConfiguration)mapping.getConfiguration()).getGeneralConfiguration(true);		
		//else 
		generalHibConf=((HibernateConfiguration)mapping.getConfiguration()).getGeneralConfiguration(true);
	    pageGenConf=new PropertySheetPage();
	    
	    pageGenConf.createControl(grUrlInf);
		//akuzmin 29.07.2005
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        Menu menu = menuMgr.createContextMenu(pageGenConf.getControl());
		pageGenConf.getControl().setMenu(menu);
		//
	    
		pageGenConf.getControl().setSize(650,570);
	    GridData  gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan=3;

		pageGenConf.getControl().setLayoutData(gd);
		if(generalHibConf!=null){
			pageGenConf.selectionChanged(null, new StructuredSelection(generalHibConf));
			
		}
		setControl(container);		
	}
	
		
 public IPath getPath(){
	 return new Path(strPath);
 }
 public boolean canFlipToNextPage() {
	 if(wrongNameFile==true)
		 return false;
	 else	return true;
	}	
 
 public boolean isPageComplete() {
	 
		
		if(mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.dialect"))==""&&
				FileName.getText().equals(""))
			return false;
		else return true;
		
	}
 
 private void doSaveAs(){
	IFile ifile;
	
	//IJavaProject fCurrJProject =  JavaCore.create(mapping.getProject().getProject());
	SaveAsDialog dialog=new SaveAsDialog(getShell());
	IPath p=new Path(strPath);
	int count=p.matchingFirstSegments(p);
	if(count>1){
	    ifile =mapping.getProject().getProject().getFile(FileName.getText());
		dialog.setOriginalFile(ifile);
	}
	if (dialog.open() == Dialog.CANCEL)
		return ;
	FileName.setText(dialog.getResult().toString());
		
	//IClasspathEntry[] oldClasspath = fCurrJProject.readRawClasspath();
	//if ( checkClassPath(oldClasspath,dialog.getResult().toString())== false){
	//	setMessage(BUNDLE.getString("HibernateConnectionWizardPage0.ClassPathMessage"),2);
	//}

		
	 
 }
public boolean checkClassPath(IClasspathEntry[] oldClasspath,String path){
		boolean result=false;
		IPath p = new Path(path);
		
		p=p.removeLastSegments(1);
		for (int i=0; i < oldClasspath.length;i++)	
		{  
			if (oldClasspath[i].getPath().toString().equals("/"+p.segment(0)+"/"+p.segment(1))||("/"+ mapping.getProject().getProject().getName()).equals(p.toString().trim())){
			//if (oldClasspath[i].getPath().toString().equals(p.toString().trim())||("/"+ mapping.getProject().getProject().getName()).equals(p.toString().trim())){
			result=true;
			break;
			}
		}
		return result;
	}

 public boolean checkClassPath(String path){ 
	IJavaProject fCurrJProject =  JavaCore.create(mapping.getProject().getProject());
	IClasspathEntry[] oldClasspath = fCurrJProject.readRawClasspath();
	boolean result=false;
		
		IPath p = new Path(path);
		for (int i=0; i < oldClasspath.length;i++)	
		{  
			if (oldClasspath[i].getPath().toString().equals("/"+p.segment(0)+"/"+p.segment(1))||("/"+ mapping.getProject().getProject().getName()).equals(p.toString().trim())){
			//if (oldClasspath[i].getPath().toString().equals(p.toString().trim())||("/"+ mapping.getProject().getProject().getName()).equals(p.toString().trim())){
			result=true;
			break;
			}
		}
		return result;
	}
 
public boolean getWrongNameFile(){
	return wrongNameFile;
}


 public void setErrorMessage(String newMessage) {
		super.setErrorMessage(newMessage);
 }
 
 public void formList(){
	 generalHibConf=((HibernateConfiguration)mapping.getConfiguration()).getGeneralConfiguration(true);
	 pageGenConf.selectionChanged(null, new StructuredSelection(generalHibConf));
 }

public PropertySourceWrapper getGeneralHibConf() {
	return generalHibConf;
}

public boolean isNewConfig() {
	return newConfig;
}
  
}
