/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.view.views;

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ObjectPluginAction;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.EclipseConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.EclipseLaunchConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;
import org.jboss.tools.hibernate.ui.view.ViewPlugin;

public abstract class OpenActionDelegate implements IObjectActionDelegate {

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	protected IJavaProject findJavaProject(ConsoleConfiguration consoleConfiguration) {
		IJavaProject proj = null;
		if (consoleConfiguration != null) {
			ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType( "org.hibernate.eclipse.launch.ConsoleConfigurationLaunchConfigurationType" );
			ILaunchConfiguration[] launchConfigurations;
			try {
				launchConfigurations = launchManager.getLaunchConfigurations( launchConfigurationType );
				for (int i = 0; i < launchConfigurations.length; i++) { // can't believe there is no look up by name API
					ILaunchConfiguration launchConfiguration = launchConfigurations[i];
					if(launchConfiguration.getName().equals(consoleConfiguration.getName())) {
						proj = ProjectUtils.findJavaProject(launchConfiguration.getAttribute(IConsoleConfigurationLaunchConstants.PROJECT_NAME, ""));
					}
				}								
			} catch (CoreException e1) {
				ViewPlugin.getDefault().logError("Can't find java project.", e1);
			}
		}
		return proj;
	}
//	protected IJavaProject findJavaProject(ConsoleConfiguration consoleConfiguration) {
//		IJavaProject proj = null;
//		if (consoleConfiguration != null) {
//			ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
//			
//			ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType( "org.hibernate.eclipse.launch.ConsoleConfigurationLaunchConfigurationType" );
//			ILaunchConfiguration[] launchConfigurations = null;
//			try {
//				launchConfigurations = launchManager.getLaunchConfigurations(launchConfigurationType);
//			} catch (Exception e) {
//			}
//			for (int i = 0; i < launchConfigurations.length; i++) {
//				
//			}
//
//			String projectName = ((EclipseConsoleConfigurationPreferences)consoleConfiguration.getPreferences()).getProjectName();
//			if (projectName != null) {
//				proj = ProjectUtils.findJavaProject(projectName);
//			}
//		}
//		return proj;
//	}
	
}