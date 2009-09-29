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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.hibernate.util.StringHelper;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;

/**
 * @author Dmitry Geraskov
 *
 */
public class GenerateDdlWizardPage extends GenerateInitWizardPage {
	
	private StringDialogField filename;
	
	private Button exportToDB;
	
	protected GenerateDdlWizardPage(HibernateJpaProject jpaProject) {
		super(jpaProject);
	}

	@Override
	protected void createChildControls(Composite container) {
		filename = new StringDialogField();
		filename.setLabelText(Messages.GenerateDdlWizardPage_file_name);
		filename.setText("schema.ddl"); //$NON-NLS-1$
		filename.setDialogFieldListener(fieldlistener);
        filename.doFillIntoGrid(container, numColumns);
        
        exportToDB = new Button(container, SWT.CHECK);
        exportToDB.setText(Messages.GenerateInitWizardPage_export_to_db);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		exportToDB.setLayoutData(gd);	
        
	}
	
	protected void dialogChanged() {
		setErrorMessage(null);
		setMessage(null);
        
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
	
	public boolean isExportToDB(){
		return exportToDB.getSelection();
	}

}
