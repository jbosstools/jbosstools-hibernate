/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.codegen;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.LaunchHelper;
import org.hibernate.eclipse.launch.CodeGenXMLFactory;

/**
 * @author Vitali Yemialyanchyk
 */
@SuppressWarnings("restriction")
public class ExportAntCodeGenWizardPage extends WizardNewFileCreationPage implements Listener {

	protected ComboDialogField consoleConfigurationName;

	/**
	 * Creates a new file creation (Ant code generation) wizard page. If the initial resource
	 * selection contains exactly one container resource then it will be used as the default
	 * container resource.
	 * 
	 * @param pageName
	 *            the name of the page
	 * @param selection
	 *            the current resource selection
	 */
	public ExportAntCodeGenWizardPage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
		setPageComplete(false);
	}

	/**
	 * @see #setControl(Control)
	 */
	protected void setControl(Control newControl) {
		newControl.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		super.setControl(newControl);
	}

	/**
	 * @see #createControl(Composite)
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		Composite topLevel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		topLevel.setLayout(layout);
		topLevel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		topLevel.setFont(parent.getFont());
		setControlCombo(topLevel);
		super.createControl(topLevel);
		setControl(topLevel);
	}

	protected void setControlCombo(Control newControl) {
		consoleConfigurationName = new ComboDialogField(SWT.READ_ONLY);
		consoleConfigurationName
				.setLabelText(HibernateConsoleMessages.ExportAntCodeGenWizardPage_hibernate_code_generation_configurations);
		ILaunchConfiguration[] launchCfgs;
		try {
			launchCfgs = LaunchHelper.findFilteredCodeGenerationConfigs();
		} catch (CoreException e) {
			launchCfgs = new ILaunchConfiguration[0];
		}
		String[] names = new String[launchCfgs.length];
		for (int i = 0; i < launchCfgs.length; i++) {
			ILaunchConfiguration launchCfg = launchCfgs[i];
			names[i] = launchCfg.getName();
		}
		consoleConfigurationName.setItems(names);
		IDialogFieldListener fieldlistener = new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				setPageComplete(validatePage());
			}
		};
		consoleConfigurationName.setDialogFieldListener(fieldlistener);
		consoleConfigurationName.doFillIntoGrid((Composite) newControl, 2);
	}

	/**
	 * @see #validatePage()
	 */
	protected boolean validatePage() {
		boolean res = super.validatePage();
		if (res) {
			if (consoleConfigurationName.getSelectionIndex() == -1) {
				setErrorMessage(HibernateConsoleMessages.ExportAntCodeGenWizardPage_empty_hibernate_code_generation_configuration);
				res = false;
			}
		}
		return res;
	}

	public ILaunchConfiguration getSelectedLaunchConfig() {
		ILaunchConfiguration[] launchCfgs;
		try {
			launchCfgs = LaunchHelper.findFilteredCodeGenerationConfigs();
		} catch (CoreException e) {
			launchCfgs = new ILaunchConfiguration[0];
		}
		int n = consoleConfigurationName.getSelectionIndex();
		if (0 <= n && n < launchCfgs.length) {
			return launchCfgs[n];
		}
		return null;
	}

	protected InputStream getInitialContents() {
		ILaunchConfiguration lc = getSelectedLaunchConfig();
		if (lc == null) {
			return null;
		}
		final CodeGenXMLFactory codeGenXMLFactory = new CodeGenXMLFactory(lc);
		String buildXml = codeGenXMLFactory.createCodeGenXML();
		return new ByteArrayInputStream(buildXml.getBytes());
	}
}
