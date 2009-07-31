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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
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

/**
 * @author Dmitry Geraskov
 *
 */
public class GenerateEntitiesWizardPage extends GenerateInitWizardPage {

	private StringButtonDialogField outputdir;
	
	private StringDialogField packageName;

	/**
	 * @param pageName
	 */
	public GenerateEntitiesWizardPage(JpaProject jpaProject) {
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
		
		packageName = new StringDialogField();
        packageName.setDialogFieldListener(fieldlistener);
        packageName.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_package);
        packageName.doFillIntoGrid(container, numColumns);
                		
		setPageComplete( false );		
	}


	protected void dialogChanged() {
		setErrorMessage(null);
		setMessage(null);
		/*validate package name*/
		String packName= getPackageName();
		if (packName.length() > 0) {
			IStatus val= validatePackageName(packName, getJpaProject());
			if (val.getSeverity() == IStatus.ERROR) {
				setErrorMessage(Messages.format(NewWizardMessages.NewTypeWizardPage_error_InvalidPackageName, val.getMessage())); 
				setPageComplete( false );
				return;
			} else if (val.getSeverity() == IStatus.WARNING) {
				setWarningMessage(Messages.format(NewWizardMessages.NewTypeWizardPage_warning_DiscouragedPackageName, val.getMessage())); 
			}
		} else {
			setWarningMessage(NewWizardMessages.NewTypeWizardPage_warning_DefaultPackageDiscouraged); 
		}
		
		String msg = PathHelper.checkDirectory(getOutputDir(), HibernateConsoleMessages.CodeGenerationSettingsTab_output_directory, false);
		
        if (msg!=null) {
        	setErrorMessage( msg );
        	setPageComplete( false );
            return;
        }
        
        super.dialogChanged();
	}
	
	private static IStatus validatePackageName(String text, JpaProject project) {
		if (project == null || !project.getJavaProject().exists()) {
			return JavaConventions.validatePackageName(text, JavaCore.VERSION_1_3, JavaCore.VERSION_1_3);
		}
		return JavaConventionsUtil.validatePackageName(text, project.getJavaProject());
	}
	
	public String getPackageName(){
		return packageName.getText();
	}
	
	public String getOutputDir(){
		return outputdir.getText();
	}

}
