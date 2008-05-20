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


import java.util.ResourceBundle;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.dialogs.Dialog;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.dialog.AutoMappingSetting;



/**
 * @author kaa
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PersistentClassesPage2 extends WizardPage {
	public static final String BUNDLE_NAME = "persistentclasses"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(PersistentClassesPage.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private IMapping mod;
    private Button PersistentClassesButton;
    private Button isGenerate;//,isUseHeuristicAlgorithms;
    private Text SchemaText;
//    private Text CatalogText;    
    public boolean Check,checkHeuristic;
	public boolean isFinish=false;    
	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		layout.marginHeight=0;
		layout.horizontalSpacing=0;
		layout.verticalSpacing = 3;
		container.setLayout(layout);
		Label label = new Label(container, SWT.NULL);		
		label.setText(BUNDLE.getString("PersistentClassesPage2.label"));
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 8;
		label.setLayoutData(data);
		
		Label label1 = new Label(container, SWT.NULL);		
		label1.setText(BUNDLE.getString("PersistentClassesPage2.label1"));
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 4;
		//data.heightHint = 26;
		label1.setLayoutData(data);
		
//add checkbutton generate
		isGenerate= new Button(container, SWT.CHECK);
		isGenerate.setText(BUNDLE.getString("PersistentClassesPage2.isGenerate"));
		data=new GridData();
		data.horizontalIndent=8;
		isGenerate.setLayoutData(data);
		isGenerate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DoCheck();
			}
		});
		isGenerate.setLayoutData(layout);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 4;
		//data.heightHint = 26;
		isGenerate.setLayoutData(data);
		isGenerate.setSelection(true);
		Check=true;
		checkHeuristic=false;
		
		//isUseHeuristicAlgorithms=new Button(container, SWT.CHECK);
		//data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		//data.horizontalSpan = 4;
		//data.heightHint = 26;
		//isUseHeuristicAlgorithms.setLayoutData(data);
		//isUseHeuristicAlgorithms.setSelection(false);
		//isUseHeuristicAlgorithms.setText("Use Heuristical Algorithms");
		//isUseHeuristicAlgorithms.addSelectionListener(new SelectionAdapter() {
			//public void widgetSelected(SelectionEvent e) {
			//	DoCheckHeuristic();
			//}
		//});
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		Label label2 = new Label(container, SWT.NULL);		
		label2.setText(BUNDLE.getString("PersistentClassesPage2.label2"));
		data.horizontalSpan = 1;
		data.horizontalIndent = 10;		
		label2.setLayoutData(data);
		
//add text field Shema		
		SchemaText = new Text(container, SWT.BORDER | SWT.SINGLE);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.widthHint=100;
		//data.heightHint=13;
		data.horizontalSpan = 2;
		data.horizontalIndent = 10;			
	    SchemaText.setLayoutData(data);		

		Label label7 = new Label(container, SWT.NULL);
		label7.setText("    ");
		data = new GridData(100,13);
		data.horizontalSpan = 1;
		label7.setLayoutData(data);
	    
	    
		Label label3 = new Label(container, SWT.NULL);		
		label3.setText("    ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
	    data.horizontalSpan = 3;
	    data.horizontalIndent = 10;	    
	    label3.setLayoutData(data);		
	    
////	  add text field Catalog		    
//		CatalogText = new Text(container, SWT.BORDER | SWT.SINGLE);
//		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		data.widthHint=100;
//		data.horizontalIndent = 10;	
//	    data.horizontalSpan = 2;		
//	    CatalogText.setLayoutData(data);
	    
		Label label6 = new Label(container, SWT.NULL);
		label6.setText("    ");
		data = new GridData(100,13);
		data.horizontalSpan = 1;
		label6.setLayoutData(data);
	    
		Label label5 = new Label(container, SWT.NULL);
		label5.setText("    ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 4;
		label5.setLayoutData(data);
	    
		Label label4 = new Label(container, SWT.NULL);		
		label4.setText(BUNDLE.getString("PersistentClassesPage2.label4"));
		data = new GridData(GridData.FILL_HORIZONTAL);
	    data.horizontalSpan = 2;
	    data.horizontalIndent = 10;	    
	    label4.setLayoutData(data);		
//		  add hibernate default button	    
		PersistentClassesButton= new Button(container, SWT.PUSH);
		PersistentClassesButton.setText(BUNDLE.getString("PersistentClassesPage2.Hibernate"));
		PersistentClassesButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DoHibernateDefault();
			}
		});
		data = new GridData(GridData.HORIZONTAL_ALIGN_END);
	    data.horizontalSpan = 2;
	    PersistentClassesButton.setLayoutData(data);
	    
//		if (mod.getProject().getConfiguration().getProperty("hibernate.default_catalog").equals(""))
//			CatalogText.setText(mod.getConfiguration().getProperty("hibernate.default_catalog"));
//		else CatalogText.setText(mod.getProject().getConfiguration().getProperty("hibernate.default_catalog"));
		if (mod.getProject().getOrmConfiguration().getProperty("hibernate.default_schema").equals(""))
			SchemaText.setText(mod.getConfiguration().getProperty("hibernate.default_schema"));
		else SchemaText.setText(mod.getProject().getOrmConfiguration().getProperty("hibernate.default_schema"));
		setControl(container);		
	}
	
	public PersistentClassesPage2(IMapping mod) {
		super("wizardPage");
		setTitle(BUNDLE.getString("PersistentClassesPage.title"));
		setDescription(BUNDLE.getString("PersistentClassesPage2.description"));
		this.mod = mod;
	}

	private void DoCheck() {
		Check=!Check;//process with chek button
		//if(!Check)
		//	isUseHeuristicAlgorithms.setEnabled(false);
		//else isUseHeuristicAlgorithms.setEnabled(true);
	}	
	
//	private void DoCheckHeuristic(){
//		checkHeuristic=!checkHeuristic;
//	}
	public String GetSchema() {
		return SchemaText.getText();//return Schema text
	}	

//	public String GetCatalog() {//return Catalog text
//		return CatalogText.getText();
//	}
	private void DoHibernateDefault() {
		//Execute AutoMapping Settings dialog
		Dialog dlg=new AutoMappingSetting(getShell(),mod.getProject());
	 	dlg.open();

	}
	
	public boolean isPageComplete() {
		if (isCurrentPage())
		return true;
		else return false;
	}
	
	public  boolean getCheckHeuristic(){
		 return checkHeuristic;
	 }
}
