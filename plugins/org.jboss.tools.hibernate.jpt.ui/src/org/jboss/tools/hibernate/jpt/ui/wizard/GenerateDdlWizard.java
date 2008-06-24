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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.ui.internal.JptUiMessages;
import org.hibernate.eclipse.launch.HibernateLaunchConstants;
import org.jboss.tools.hibernate.jpt.ui.internal.platform.HibernatePlatformUI;

/**
 * @author Dmitry Geraskov
 *
 */
public class GenerateDdlWizard extends Wizard {	

	private JpaProject jpaProject;

	private IStructuredSelection selection;
	
	private GenerateInitWizardPage initPage;
	
	private GenerateDdlWizardPage page2; 
	
	
	public GenerateDdlWizard(JpaProject jpaProject, IStructuredSelection selection) {
		super();
		this.jpaProject = jpaProject;
		this.selection = selection;
		this.setWindowTitle( JptUiMessages.GenerateEntitiesWizard_generateEntities);
	}
	
	@Override
	public void addPages() {
		super.addPages();
		initPage = new GenerateInitWizardPage(jpaProject);
		page2 = new GenerateDdlWizardPage("");
		addPage(initPage);
		addPage(page2);
	}
	
	@Override
	public boolean performFinish() {
		String projectName = jpaProject.getName();
		ILaunchConfigurationWorkingCopy wc = HibernatePlatformUI.createDefaultLaunchConfig(projectName);
		if (wc != null) {
			// Main
			//unknown - ccname, outputdir, filename
			wc.setAttribute(HibernateLaunchConstants.ATTR_CONSOLE_CONFIGURATION_NAME, initPage.getConfigurationName());
			wc.setAttribute(HibernateLaunchConstants.ATTR_OUTPUT_DIR, page2.getOutputDir()); //$NON-NLS-1$

			Map<String, String> prop = new HashMap<String, String>();
			prop.put("outputFileName", page2.getFilename());
			//prop.put("outputdir", project.getName() + "\\src");
			prop.put("format", "true");
			prop.put("scriptToConsole", "false");

			wc.setAttribute(HibernatePlatformUI.full_exporter_id + ".properties", prop);
			wc.setAttribute(HibernatePlatformUI.full_exporter_id + ".extension_id", HibernateLaunchConstants.ATTR_PREFIX + "hbm2ddl"); //$NON-NLS-1$ //$NON-NLS-2$
			HibernatePlatformUI.runLaunchConfiguration(wc);
		}
		return true;
	}
}
