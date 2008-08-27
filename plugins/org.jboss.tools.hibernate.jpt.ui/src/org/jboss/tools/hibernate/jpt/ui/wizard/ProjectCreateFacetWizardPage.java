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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jpt.core.internal.facet.JpaFacetDataModelProperties;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.VersionFormatException;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.jboss.tools.hibernate.jpt.ui.ConsoleConfigurationFacetModel;
import org.jboss.tools.hibernate.jpt.ui.HibernateJptUIPlugin;

public class ProjectCreateFacetWizardPage extends AbstractFacetWizardPage {

	private String connectioProfileName;

	private String projectName;

	private boolean firstTime = true;

	private ConsoleConfigurationFacetModel config;

	private StringDialogField ccName;

	private SelectionButtonDialogField select;

	private IDialogFieldListener completeListener = new IDialogFieldListener() {
		public void dialogFieldChanged(DialogField field) {
			updateFields();
			updateButtons();
		}

	};

	public ProjectCreateFacetWizardPage() {
		super(new String());
		setMessage(Messages.wizardMessage); //$NON-NLS-1$
	}

	/**
	 * @param pageName
	 */
	public ProjectCreateFacetWizardPage(String pageName) {
		super(pageName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage
	 * #createTopLevelComposite(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		int nColumns = 2;
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(nColumns, false);
		composite.setLayout(layout);

		select = new SelectionButtonDialogField(SWT.CHECK);
		select.setLabelText(Messages.selectName);
		select.setSelection(true);

		ccName = new StringDialogField();
		ccName.setLabelText(Messages.ccName); 

		select.attachDialogField(ccName);
		select.setDialogFieldListener(completeListener);
		ccName.setDialogFieldListener(completeListener);

		Control[] controls = select.doFillIntoGrid(composite, nColumns);
		((GridData) controls[0].getLayoutData()).grabExcessHorizontalSpace = true;
		ccName.doFillIntoGrid(composite, nColumns);

		Dialog.applyDialogFont(parent);
		setControl(composite);
	}

	public void setConfig(Object config) {
		this.config = (ConsoleConfigurationFacetModel) config;
	}

	@Override
	public void transferStateToConfig() {
		if (select.isSelected()) {
			config.setProjectName(projectName);
			config.setConsoleConfigurationName(ccName.getText());
			config.setConnectionProfileName(connectioProfileName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			updateFields();
			updateButtons();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		updateFields();
		if (!select.isSelected()) {
			return true;
		} else {
			return connectioProfileName != null && ccName.getText().length() > 0;
		}
	}

	private void updateButtons() {
		if (!select.isSelected()) {
			setErrorMessage(null);
		} else {
			if (connectioProfileName == null) {
				setErrorMessage(Messages.connectionProfileError); 
			} else {
				setErrorMessage(verifyName());				
			}
		}
		setPageComplete(getErrorMessage() == null);
	}

	private void updateFields() {
		connectioProfileName = null;
		projectName = context.getProjectName();

		if (firstTime && projectName != null) {
			firstTime = false;
			ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
			ccName.setText(lm.generateUniqueLaunchConfigurationNameFrom(projectName));
		}

		IProjectFacet pf = ProjectFacetsManager.getProjectFacet("jpt.jpa");  //$NON-NLS-1$
		IProjectFacetVersion fv;
		try {
			fv = pf.getLatestVersion();
			Action a = context.getAction(Action.Type.INSTALL, fv);
			if (a != null) {
				IDataModel model = (IDataModel) a.getConfig();
				connectioProfileName = (String) model.getProperty(JpaFacetDataModelProperties.CONNECTION);
			}
		} catch (VersionFormatException e) {
			HibernateJptUIPlugin.logException(e);
		} catch (CoreException e) {
			HibernateJptUIPlugin.logException(e);
		}
	}

	private String verifyName() {
			String currentName = ccName.getText().trim();

		if (currentName.length() < 1) {
			return HibernateConsoleMessages.ConsoleConfigurationWizardPage_name_must_specified;
		}

		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		try {
			if (lm.isExistingLaunchConfigurationName(currentName)) {
				return HibernateConsoleMessages.ConsoleConfigurationWizardPage_config_name_already_exist;
			}
		} catch (CoreException e) {
			HibernateJptUIPlugin.logException(e);
		}

		if (Platform.OS_WIN32.equals(Platform.getOS())) {
			String[] badnames = new String[] { "aux", "clock$", "com1", "com2", "com3", "com4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ 
					"com5", "com6", "com7", "com8", "com9", "con", "lpt1", "lpt2", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
					"lpt3", "lpt4", "lpt5", "lpt6", "lpt7", "lpt8", "lpt9", "nul", "prn" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
			for (int i = 0; i < badnames.length; i++) {
				if (currentName.equals(badnames[i])) {
					return NLS.bind(HibernateConsoleMessages.ConsoleConfigurationWizardPage_bad_name, currentName);
				}
			}
		}
		// See if name contains any characters that we deem illegal.
		// '@' and '&' are disallowed because they corrupt menu items.
		char[] disallowedChars = new char[] { '@', '&', '\\', '/', ':', '*', '?', '"', '<', '>', '|', '\0' };
		for (int i = 0; i < disallowedChars.length; i++) {
			char c = disallowedChars[i];
			if (currentName.indexOf(c) > -1) {
				return NLS.bind(HibernateConsoleMessages.ConsoleConfigurationWizardPage_bad_char, c);
			}
		}

		return null;
	}	
	
}
