/*******************************************************************************
  * Copyright (c) 2008-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.wizard;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.swt.layout.GridData;
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
public class GenerateDdlWizardPage extends GenerateInitWizardPage {
	
	private StringButtonDialogField outputdir;
	
	private StringDialogField filename;
	
	protected GenerateDdlWizardPage(JpaProject jpaProject) {
		super(jpaProject);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.jpt.ui.wizard.GenerateInitWizardPage#createChildControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createChildControls(Composite container) {
		int numColumns = 3;
	
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
		outputdir.setText(getDefaultOutput());
        outputdir.setDialogFieldListener(fieldlistener);
		outputdir.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_output_dir);
		outputdir.setButtonLabel(HibernateConsoleMessages.CodeGenerationSettingsTab_browse);
				
		Control[] controls = outputdir.doFillIntoGrid(container, numColumns);
		// Hack to tell the text field to stretch!
		( (GridData)controls[1].getLayoutData() ).grabExcessHorizontalSpace = true;
		
		filename = new StringDialogField();
		filename.setLabelText(Messages.GenerateDdlWizardPage_file_name);
		filename.setText("schema.ddl"); //$NON-NLS-1$
		filename.setDialogFieldListener(fieldlistener);
        filename.doFillIntoGrid(container, numColumns);

		setPageComplete( false );		
	}	
	
	protected void dialogChanged() {
		setErrorMessage(null);
		setMessage(null);
		String msg = PathHelper.checkDirectory(getOutputDir(), HibernateConsoleMessages.CodeGenerationSettingsTab_output_directory, false);

        if (msg!=null) {
        	setErrorMessage( msg );
        	setPageComplete( false );
            return;
        }
        
        if (StringHelper.isEmpty(getFilename())) {
            setErrorMessage(Messages.GenerateDdlWizardPage_err_msg_input_file_name);
            setPageComplete( false );
            return;
        }
        
        IStatus status = ResourcesPlugin.getWorkspace().validateName(getFilename(), IResource.FILE);
        if (status.getSeverity() != IStatus.OK){
        	setErrorMessage( status.getMessage() );
            return;
        }
        
        super.dialogChanged();
	}
	
	public String getFilename(){
		return filename.getText();
	}
	
	public String getOutputDir(){
		return outputdir.getText();
	}


}
