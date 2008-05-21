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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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


/**
 * @author kaa
 * akuzmin@exadel.com
 * Aug 19, 2005
 */
public class GenerateDAOPage3 extends WizardPage {
	private IMapping mod;
	private Button isSpring;
	private boolean generateSpring = false;
	private PropertySheetPage page;
	private Text FileName;
	private Button SaveAsButton;
	private String strPath;
	public static final String BUNDLE_NAME = "generateDAO"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(GenerateDAOPage3.class.getPackage().getName() + "." + BUNDLE_NAME);	

	protected GenerateDAOPage3(IMapping mod) {
		super("wizardPage");
		setTitle(BUNDLE.getString("GenerateDAOPage3.title"));
		setDescription(BUNDLE.getString("GenerateDAOPage3.description"));
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
		data.horizontalSpan = 4;
		data.horizontalIndent=8;
		//Generate mapping check box	
		isSpring= new Button(container, SWT.CHECK);
		isSpring.setText(BUNDLE.getString("GenerateDAOPage3.isspring"));
		isSpring.setLayoutData(data);
		isSpring.setSelection(false);
		isSpring.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (((Button)e.widget).getSelection())
				{
					generateSpring = true;
					page.getControl().setEnabled(true);
					FileName.setEnabled(true);
					SaveAsButton.setEnabled(true);
				}
				else 
				{
					generateSpring = false;
					page.getControl().setEnabled(false);
					FileName.setEnabled(false);
					SaveAsButton.setEnabled(false);			
				}
				canFinish();
			}
		});


		Group groupe = new Group(container, SWT.NONE);
		GridLayout Grlayout = new GridLayout();
		Grlayout.numColumns = 3;
		groupe.setLayout(Grlayout);
		GridData groupData =   new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		groupe.setLayoutData(groupData);

		Label lblFileName = new Label(groupe, SWT.NULL);		
		lblFileName.setText(BUNDLE.getString("GenerateDAOPage3.FileName"));
		lblFileName.setLayoutData(new GridData(SWT.BEGINNING, SWT.NONE, false, false, 1, 1));

		FileName = new Text(groupe, SWT.BORDER | SWT.SINGLE);
		FileName.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
		FileName.setEnabled(false);
		FileName.setText(mod.getConfiguration().getResource().getFullPath().removeLastSegments(1)+"/context.xml");
		strPath=mod.getConfiguration().getResource().getFullPath().removeLastSegments(1)+"/context.xml";
		FileName.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e) {
				canFinish();

			}
		}); 
		SaveAsButton= new Button(groupe, SWT.PUSH);
		SaveAsButton.setText(BUNDLE.getString("GenerateDAOPage3.SaveAs"));
		GridData d=setButtonLayoutData( SaveAsButton);
		SaveAsButton.setLayoutData(d);
		SaveAsButton.setEnabled(false);
		SaveAsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doSaveAs();
				getWizard().getContainer().updateButtons();
			}
		});	


		GridData lData =   new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		page=new PropertySheetPage();

		page.createControl(groupe);
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		Menu menu = menuMgr.createContextMenu(page.getControl());
		page.getControl().setMenu(menu);
		//

		page.getControl().setLayoutData(lData);
		page.getControl().setSize(500,480);
		page.selectionChanged(null, new StructuredSelection(mod.getProject().getSpringConfiguration()));
		page.getControl().setEnabled(false);
		page.selectionChanged(null, new StructuredSelection(((HibernateConfiguration)mod.getConfiguration()).getSpringConfiguration()));
		setControl(groupe);	


		setControl(container);		
	}



	private void doSaveAs(){
		IFile ifile;

		//IJavaProject fCurrJProject =  JavaCore.create(mapping.getProject().getProject());
		SaveAsDialog dialog=new SaveAsDialog(getShell());
		IPath p=new Path(strPath);
		int count=p.matchingFirstSegments(p);
		if(count>1){
			ifile =mod.getProject().getProject().getFile(FileName.getText());
			dialog.setOriginalFile(ifile);
		}
		if (dialog.open() == Dialog.CANCEL)
			return ;
		FileName.setText(dialog.getResult().toString());
	}

	/**
	 * @return Returns the strPath.
	 */
	public String getStrPath() {
		return strPath;
	}

	public boolean generateSpring() {
		return generateSpring;
	}


	private void canFinish() {
		if(!isSpring.getSelection()) {
			setMessage(null);
			setErrorMessage(null);
			setPageComplete(true);

		} else {
			strPath=FileName.getText();
			if (ResourcesPlugin.getWorkspace().validatePath(strPath, IResource.FILE).isOK()) {
				setMessage(null);
				setErrorMessage(null);
				setPageComplete(true);

			}else {
				setErrorMessage(BUNDLE.getString("GenerateDAOPage3.ErrorMessageEnterFileName"));
				FileName.setFocus();
				setPageComplete(false);
			}
		}
		getWizard().getContainer().updateButtons();
	}	 

}
