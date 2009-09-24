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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jpt.ui.internal.JptUiMessages;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.launch.HibernateLaunchConstants;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.ui.HibernateJptUIPlugin;
import org.jboss.tools.hibernate.jpt.ui.internal.platform.HibernateJpaPlatformUi;

/**
 * @author Dmitry Geraskov
 *
 */
public class GenerateDdlWizard extends Wizard {	

	private HibernateJpaProject jpaProject;

	private GenerateDdlWizardPage initPage;
	
	
	public GenerateDdlWizard(HibernateJpaProject jpaProject, IStructuredSelection selection) {
		super();
		this.jpaProject = jpaProject;
		this.setWindowTitle( JptUiMessages.GenericPlatformUiDialog_notSupportedMessageTitle);
	}
	
	@Override
	public void addPages() {
		super.addPages();
		initPage = new GenerateDdlWizardPage(jpaProject);
		addPage(initPage);
	}

	@Override
	public boolean performFinish() {
		String projectName = jpaProject.getName();
		ILaunchConfigurationWorkingCopy wc = HibernateJpaPlatformUi.createDefaultLaunchConfig(projectName);
		if (wc != null) {
			String concoleConfigurationName = initPage.getConfigurationName();
			wc.setAttribute(HibernateLaunchConstants.ATTR_CONSOLE_CONFIGURATION_NAME, concoleConfigurationName);
			wc.setAttribute(HibernateLaunchConstants.ATTR_OUTPUT_DIR, initPage.getOutputDir());

			Map<String, String> prop = new HashMap<String, String>();
			prop.put("outputFileName", initPage.getFilename());  //$NON-NLS-1$
			prop.put("format", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			prop.put("scriptToConsole", "false"); //$NON-NLS-1$  //$NON-NLS-2$
			prop.put("exportToDatabase", "false");//$NON-NLS-1$  //$NON-NLS-2$			
			
			wc.setAttribute(HibernateJpaPlatformUi.full_exporter_id + ".properties", prop);  //$NON-NLS-1$
			wc.setAttribute(HibernateJpaPlatformUi.full_exporter_id + ".extension_id", HibernateLaunchConstants.ATTR_PREFIX + "hbm2ddl"); //$NON-NLS-1$ //$NON-NLS-2$
			try {
				wc.launch(ILaunchManager.RUN_MODE, null);
			} catch (CoreException e) {
				HibernateJptUIPlugin.logException(e);
			} finally{
				if (initPage.isTemporaryConfiguration()){
					KnownConfigurations.getInstance().removeConfiguration(KnownConfigurations.getInstance().find(concoleConfigurationName), false);				
				}
			}
		}
		return true;
	}
}
