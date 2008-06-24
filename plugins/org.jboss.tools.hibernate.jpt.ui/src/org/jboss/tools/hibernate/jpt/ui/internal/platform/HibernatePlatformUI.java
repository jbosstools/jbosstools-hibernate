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
package org.jboss.tools.hibernate.jpt.ui.internal.platform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.db.Table;
import org.eclipse.jpt.gen.internal.EntityGenerator;
import org.eclipse.jpt.gen.internal.PackageGenerator;
import org.eclipse.jpt.ui.internal.platform.generic.GenericPlatformUi;
import org.hibernate.eclipse.launch.HibernateLaunchConstants;
import org.jboss.tools.hibernate.jpt.ui.wizard.GenerateDdlWizard;
import org.jboss.tools.hibernate.jpt.ui.wizard.GenerateEntitiesWizard;

/**
 * @author Dmitry Geraskov
 * 
 */
public class HibernatePlatformUI extends GenericPlatformUi {

	public static final String LaunchConfigurationType_ID = "org.hibernate.eclipse.launch.CodeGenerationLaunchConfigurationType";

	public static String exporter_id = "hbmexporter";
	public static String full_exporter_id = HibernateLaunchConstants.ATTR_EXPORTERS + '.' + "hbmexporter";

	@Override
	public void generateEntities(JpaProject project, IStructuredSelection selection) {
		GenerateEntitiesWizard wizard = new GenerateEntitiesWizard(project, selection);
		
		WizardDialog dialog = new WizardDialog(null, wizard);
		dialog.create();
		/*int returnCode = */dialog.open();
		/*if (returnCode == Window.OK) {
			ILaunchConfigurationWorkingCopy wc = createDefaultLaunchConfig(project.getName());
			if (wc != null) {
				// SHOULD PRESENT THE CONFIGURATION!!!
				//unknown - ccname, outputdir, packagename
				wc.setAttribute(HibernateLaunchConstants.ATTR_CONSOLE_CONFIGURATION_NAME, project.getName());

				wc.setAttribute(HibernateLaunchConstants.ATTR_OUTPUT_DIR, project.getName() + "\\src"); //$NON-NLS-1$

				wc.setAttribute(HibernateLaunchConstants.ATTR_PACKAGE_NAME, "package_name");
				wc.setAttribute(HibernateLaunchConstants.ATTR_PREFER_BASIC_COMPOSITE_IDS, true);
				wc.setAttribute(HibernateLaunchConstants.ATTR_AUTOMATIC_MANY_TO_MANY, true);
				wc.setAttribute(HibernateLaunchConstants.ATTR_AUTOMATIC_VERSIONING, true);

				wc.setAttribute(HibernateLaunchConstants.ATTR_ENABLE_JDK5, true);
				wc.setAttribute(HibernateLaunchConstants.ATTR_ENABLE_EJB3_ANNOTATIONS, true);

				wc.setAttribute(HibernateLaunchConstants.ATTR_EXPORTERS + '.' + exporter_id + ".extension_id", 
							HibernateLaunchConstants.ATTR_PREFIX + "hbm2java"); //$NON-NLS-1$ //$NON-NLS-2$
				runLaunchConfiguration(wc);
			}
		}	*/	
	}

	@Override
	public void generateDDL(JpaProject project, IStructuredSelection selection) {
		GenerateDdlWizard wizard = new GenerateDdlWizard(project, selection);
		
		WizardDialog dialog = new WizardDialog(null, wizard);
		dialog.create();
		/*int returnCode = */dialog.open();
		/*if (returnCode == Window.OK) {
			ILaunchConfigurationWorkingCopy wc = createDefaultLaunchConfig(project.getName());
			if (wc != null) {
				// Main
				//unknown - ccname, outputdir, filename
				wc.setAttribute(HibernateLaunchConstants.ATTR_CONSOLE_CONFIGURATION_NAME, project.getName());
				wc.setAttribute(HibernateLaunchConstants.ATTR_OUTPUT_DIR, project.getName() + "\\src"); //$NON-NLS-1$

				Map<String, String> prop = new HashMap<String, String>();
				prop.put("outputFileName", "schema.ddl");
				//prop.put("outputdir", project.getName() + "\\src");
				prop.put("format", "true");
				prop.put("scriptToConsole", "false");

				wc.setAttribute(full_exporter_id + ".properties", prop);
				wc.setAttribute(full_exporter_id + ".extension_id", HibernateLaunchConstants.ATTR_PREFIX + "hbm2ddl"); //$NON-NLS-1$ //$NON-NLS-2$
				runLaunchConfiguration(wc);
			}
		}*/		
	}

	public static ILaunchConfigurationWorkingCopy createDefaultLaunchConfig(String projectName) {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType launchConfigurationType = launchManager
				.getLaunchConfigurationType(LaunchConfigurationType_ID); //$NON-NLS-1$

		ILaunchConfigurationWorkingCopy wc = null;
		try {
			wc = launchConfigurationType.newInstance(null, projectName + "-hibernate-generate");
			wc.setAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER, true);
			// Create exporters
			List<String> exporters = new ArrayList<String>();
			exporters.add(exporter_id); //$NON-NLS-1$
			wc.setAttribute(HibernateLaunchConstants.ATTR_EXPORTERS, exporters);
			wc.setAttribute(full_exporter_id, true);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return wc;
	}

	public static void runLaunchConfiguration(ILaunchConfiguration configuration) {
		try {
			DebugPlugin.getDefault().getLaunchManager().addLaunch(configuration.launch(ILaunchManager.RUN_MODE, null));
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
