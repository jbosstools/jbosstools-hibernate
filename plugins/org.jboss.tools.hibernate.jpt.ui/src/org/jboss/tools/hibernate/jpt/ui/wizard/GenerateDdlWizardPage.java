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

import org.eclipse.core.internal.resources.LocationValidator;
import org.eclipse.core.internal.resources.OS;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
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
public class GenerateDdlWizardPage extends WizardPage {
	
	private StringButtonDialogField outputdir;
	
	private StringDialogField filename;
	
	private LocationValidator validator = new LocationValidator((Workspace) ResourcesPlugin.getWorkspace());

	/**
	 * @param pageName
	 */
	protected GenerateDdlWizardPage(String pageName) {
		super(pageName);
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
		
		filename = new StringDialogField();
		filename.setLabelText("File name");
		filename.setText("schema.ddl");
		filename.setDialogFieldListener(fieldlistener);
        filename.doFillIntoGrid(container, numColumns);
		
		setControl(container);
		setPageComplete( false );
	}

	protected void dialogChanged() {
		String msg = PathHelper.checkDirectory(getOutputDir(), HibernateConsoleMessages.CodeGenerationSettingsTab_output_directory, false);

        if (msg!=null) {
        	setErrorMessage( msg );
        	setPageComplete( false );
            return;
        }
        
        if (StringHelper.isEmpty(getFilename())) {
            setErrorMessage( "Please, input file name" );
            setPageComplete( false );
            return;
        }
        
        IStatus status = ResourcesPlugin.getWorkspace().validateName(getFilename(), IResource.FILE);
        if (status.getSeverity() != status.OK){
        	setErrorMessage( status.getMessage() );
            return;
        }
        
        setErrorMessage( null );
        setPageComplete( true );
	}
	
	public String getFilename(){
		return filename.getText();
	}
	
	public String getOutputDir(){
		return outputdir.getText();
	}

}
