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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.swt.widgets.Composite;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.launch.PathHelper;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;

/**
 * @author Dmitry Geraskov
 *
 */
public class GenerateEntitiesWizardPage extends GenerateInitWizardPage {

	private StringDialogField packageName;

	/**
	 * @param pageName
	 */
	public GenerateEntitiesWizardPage(HibernateJpaProject jpaProject) {
		super(jpaProject);
	}

	@Override
	protected void createChildControls(Composite container) {
		packageName = new StringDialogField();
        packageName.setDialogFieldListener(fieldlistener);
        packageName.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_package);
        packageName.doFillIntoGrid(container, numColumns);
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		if (!"".equals(getOutputDir())){//$NON-NLS-1$
			packageName.setFocus();
		}
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

}
