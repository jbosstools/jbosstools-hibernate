/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.wizard;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.DialogSelectionHelper;
import org.hibernate.eclipse.launch.PathHelper;
import org.hibernate.util.StringHelper;

/**
 * @author Dmitry Geraskov
 *
 */
public class GenerateEntitiesWizardPage extends WizardPage {
	
	private StringButtonDialogField outputdir;
	
	private StringDialogField packageName;

	/**
	 * @param pageName
	 */
	public GenerateEntitiesWizardPage(String pageName) {
		super(pageName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public GenerateEntitiesWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		int numColumns = 3;

		container.setLayout(layout);
		layout.numColumns = numColumns;
		layout.verticalSpacing = 10;
		
		
		IDialogFieldListener fieldlistener = new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				dialogChanged();
			}
		};
		
		outputdir = new StringButtonDialogField(new IStringButtonAdapter() {
			public void changeControlPressed(DialogField field) {
				IPath[] paths = DialogSelectionHelper.chooseFolderEntries(getShell(),  PathHelper.pathOrNull(outputdir.getText()), HibernateConsoleMessages.CodeGenerationSettingsTab_select_output_dir, HibernateConsoleMessages.CodeGenerationSettingsTab_choose_dir_for_generated_files, false);
				if(paths!=null && paths.length==1) {
					outputdir.setText( ( (paths[0]).toOSString() ) );
				}
			}
		});
        outputdir.setDialogFieldListener(fieldlistener);
		outputdir.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_output_dir);
		outputdir.setButtonLabel(HibernateConsoleMessages.CodeGenerationSettingsTab_browse);
				
		Control[] controls = outputdir.doFillIntoGrid(container, numColumns);
		// Hack to tell the text field to stretch!
		( (GridData)controls[1].getLayoutData() ).grabExcessHorizontalSpace = true;
		
		packageName = new StringDialogField();
        packageName.setDialogFieldListener(fieldlistener);
        packageName.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_package);
        packageName.doFillIntoGrid(container, numColumns);
        		
		setControl(container);
		//default package is acceptable
		setPageComplete( true );
	}

	protected void dialogChanged() {
		String msg = PathHelper.checkDirectory(getOutputDir(), HibernateConsoleMessages.CodeGenerationSettingsTab_output_directory, false);

        if (msg!=null) {
        	setErrorMessage( msg );
        	setPageComplete( false );
            return;
        }
		
        if (StringHelper.isNotEmpty(getPackageName())){
        	IStatus status= JavaConventions.validatePackageName(getPackageName() );
            if (status.getSeverity() == IStatus.ERROR || status.getSeverity() == IStatus.WARNING) {
                setErrorMessage( status.getMessage() );
                return;
            }
        }
        
        setErrorMessage( null );
        setPageComplete( true );
	}
	
	public String getPackageName(){
		return packageName.getText();
	}
	
	public String getOutputDir(){
		return outputdir.getText();
	}

}
