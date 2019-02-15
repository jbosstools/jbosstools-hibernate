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
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.undo.CreateFileOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.LaunchHelper;
import org.hibernate.eclipse.launch.CodeGenXMLFactory;
import org.hibernate.eclipse.launch.ExporterAttributes;

/**
 * @author Vitali Yemialyanchyk
 */
@SuppressWarnings("restriction")
public class ExportAntCodeGenWizardPage extends WizardNewFileCreationPage implements Listener {

	protected ComboDialogField consoleConfigurationName;

	protected CodeGenXMLFactory codeGenXMLFactory = null;

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
			launchCfgs = LaunchHelper.findFilteredCodeGenerationConfigsSorted();
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
			} else {
				ILaunchConfiguration lc = getSelectedLaunchConfig();
				if (lc == null) {
					setErrorMessage(HibernateConsoleMessages.ExportAntCodeGenWizardPage_cannot_find_selected_hibernate_code_generation_configuration);
					res = false;
				} else {
					String checkMessage = checkCodeGenLaunchConfig(lc);
					if (checkMessage != null) {
						setMessage(checkMessage, IMessageProvider.WARNING);
					}
				}
			}
		}
		return res;
	}
	
	protected ConsoleConfigurationPreferences getConsoleConfigPreferences(String consoleConfigName) {
		ConsoleConfiguration consoleConfig = KnownConfigurations.getInstance().find(consoleConfigName);
		if (consoleConfig == null) {
			return null;
		}
		return consoleConfig.getPreferences();
	}

	protected String checkCodeGenLaunchConfig(ILaunchConfiguration lc) {
		String checkMessage = null;
		ExporterAttributes attributes = null;
		try {
			attributes = new ExporterAttributes(lc);
			checkMessage = attributes.checkExporterAttributes();
		} catch (CoreException e) {
			checkMessage = e.getMessage();
		}
		if (checkMessage != null) {
			checkMessage = NLS.bind(HibernateConsoleMessages.ExportAntCodeGenWizardPage_error_in_hibernate_code_generation_configuration, 
					checkMessage);
		}
		if (checkMessage == null && attributes != null) {
			String consoleConfigName = attributes.getConsoleConfigurationName();
			ConsoleConfigurationPreferences consoleConfigPrefs = 
				getConsoleConfigPreferences(consoleConfigName);
			String connProfileName = consoleConfigPrefs == null ? null : 
				consoleConfigPrefs.getConnectionProfileName();
			if (!CodeGenXMLFactory.isEmpty(connProfileName)) {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				String externalPropFileName = CodeGenXMLFactory.propFileNameSuffix;
				externalPropFileName = getFileName() + "." + externalPropFileName; //$NON-NLS-1$
				String problemMessage = NLS.bind(HibernateConsoleMessages.ExportAntCodeGenWizardPage_warning, 
						externalPropFileName);
				IPath resourcePath = getContainerFullPath().append(externalPropFileName);
				if (workspace.getRoot().getFile(resourcePath).exists()) {
					checkMessage = problemMessage;
				}
			}
		}
		return checkMessage;
	}

	public ILaunchConfiguration getSelectedLaunchConfig() {
		ILaunchConfiguration[] launchCfgs;
		try {
			launchCfgs = LaunchHelper.findFilteredCodeGenerationConfigsSorted();
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
		codeGenXMLFactory = new CodeGenXMLFactory(lc);
		String externalPropFileName = CodeGenXMLFactory.getExternalPropFileNameStandard(getFileName());
		codeGenXMLFactory.setExternalPropFileName(externalPropFileName);
		codeGenXMLFactory.setPlace2Generate(getContainerFullPath().toString());
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace != null && workspace.getRoot() != null && workspace.getRoot().getLocation() != null) {
			codeGenXMLFactory.setWorkspacePath(workspace.getRoot().getLocation().toString());
		}
		String buildXml = codeGenXMLFactory.createCodeGenXML();
		return new ByteArrayInputStream(buildXml.getBytes());
	}

	public IFile createNewFile() {
		codeGenXMLFactory = null;
		IFile res = super.createNewFile();
		if (codeGenXMLFactory != null && res != null) {
			final String propFileContentPreSave = codeGenXMLFactory.getPropFileContentPreSave();
			if (!CodeGenXMLFactory.isEmpty(propFileContentPreSave)) {
				IPath path = res.getFullPath();
				path = path.removeLastSegments(1);
				path = path.append(codeGenXMLFactory.getExternalPropFileName());
				final IFile newFileHandle = createFileHandle(path);
				final InputStream initialContents = new ByteArrayInputStream(
						propFileContentPreSave.getBytes());
				IRunnableWithProgress op = new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) {
						CreateFileOperation op = new CreateFileOperation(newFileHandle, null,
								initialContents,
								IDEWorkbenchMessages.WizardNewFileCreationPage_title);
						try {
							// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=219901
							// directly execute the operation so that the undo state is
							// not preserved. Making this undoable resulted in too many
							// accidental file deletions.
							op.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
						} catch (final ExecutionException e) {
							getContainer().getShell().getDisplay().syncExec(new Runnable() {
								public void run() {
									if (e.getCause() instanceof CoreException) {
										ErrorDialog
												.openError(
														getContainer().getShell(), // Was
														// Utilities.getFocusShell()
														IDEWorkbenchMessages.WizardNewFileCreationPage_errorTitle,
														null, // no special
														// message
														((CoreException) e.getCause()).getStatus());
									} else {
										IDEWorkbenchPlugin.log(getClass(),
												"createNewFile()", e.getCause()); //$NON-NLS-1$
										MessageDialog
												.openError(
														getContainer().getShell(),
														IDEWorkbenchMessages.WizardNewFileCreationPage_internalErrorTitle,
														NLS.bind(
																IDEWorkbenchMessages.WizardNewFileCreationPage_internalErrorMessage,
																e.getCause().getMessage()));
									}
								}
							});
						}
					}
				};
				try {
					getContainer().run(true, true, op);
				} catch (InterruptedException e) {
				} catch (InvocationTargetException e) {
					// Execution Exceptions are handled above but we may still get
					// unexpected runtime errors.
					IDEWorkbenchPlugin.log(getClass(), "createNewFile()", e.getTargetException()); //$NON-NLS-1$
					MessageDialog
							.open(MessageDialog.ERROR,
									getContainer().getShell(),
									IDEWorkbenchMessages.WizardNewFileCreationPage_internalErrorTitle,
									NLS.bind(
											IDEWorkbenchMessages.WizardNewFileCreationPage_internalErrorMessage,
											e.getTargetException().getMessage()), SWT.SHEET);

				}
			}
		}
		return res;
	}
}
