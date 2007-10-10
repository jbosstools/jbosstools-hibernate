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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.ObjectPluginAction;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.ui.view.ViewPlugin;

public class OpenSourceActionDelegate extends OpenActionDelegate {

	public void run(IAction action) {
    	ObjectPluginAction objectPluginAction = (ObjectPluginAction)action;
    	RootClass rootClass = (RootClass)((TreeSelection)objectPluginAction.getSelection()).getFirstElement();
		ConsoleConfiguration consoleConfiguration = (ConsoleConfiguration)(((TreeSelection)objectPluginAction.getSelection()).getPaths()[0]).getSegment(0);
		IJavaProject proj = OpenFileActionUtils.findJavaProject(consoleConfiguration);

		IResource resource = null;
		String fullyQualifiedName = HibernateUtils.getPersistentClassName(rootClass);
		try {
			IType type = proj.findType(fullyQualifiedName);
			if (type != null) resource = type.getResource();
		} catch (JavaModelException e) {
			ViewPlugin.getDefault().logError("Can't find source file.", e);
		}
		
		if (resource instanceof IFile){
            try {
            	OpenFileActionUtils.openEditor(ViewPlugin.getPage(), (IFile) resource);
            } catch (PartInitException e) {
    			ViewPlugin.getDefault().logError("Can't open source file.", e);
            }               
        }
		if (resource == null) {
			MessageDialog.openInformation(ViewPlugin.getActiveWorkbenchShell(), "Open Source File", "Source file for class '" + fullyQualifiedName + "' not found.");
		}
	}
}