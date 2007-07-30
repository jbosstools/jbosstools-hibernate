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
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;
import org.jboss.tools.hibernate.ui.view.ViewPlugin;

public class OpenDiagramActionDelegate implements IObjectActionDelegate {

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
    	HashMap hashMap = new HashMap();
    	ObjectPluginAction objectPluginAction = (ObjectPluginAction)action;
    	Object rootClass = ((TreeSelection)objectPluginAction.getSelection()).getFirstElement();
		ObjectEditorInput input = (ObjectEditorInput)hashMap.get(rootClass);
//		Configuration configuration = (Configuration)(((TreeSelection)objectPluginAction.getSelection()).getPaths()[0]).getSegment(1);
		ConsoleConfiguration consoleConfiguration = (ConsoleConfiguration)(((TreeSelection)objectPluginAction.getSelection()).getPaths()[0]).getSegment(0);
		
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

		ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType( "org.hibernate.eclipse.launch.ConsoleConfigurationLaunchConfigurationType" );
		ILaunchConfiguration[] launchConfigurations;
		IJavaProject proj = null;
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
			
			
		if(input == null) {
			input = new ObjectEditorInput(consoleConfiguration, rootClass, proj);
			hashMap.put(rootClass, input);
		}
		try {
			IDE.openEditor(ViewPlugin.getPage(),input ,"org.jboss.tools.hibernate.ui.veditor.editors.visualeditor");
		} catch (PartInitException e) {
			ViewPlugin.getDefault().logError("Can't open mapping view.", e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}