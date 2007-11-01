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

import org.dom4j.Document;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.ObjectPluginAction;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.mapping.RootClass;
import org.hibernate.util.XMLHelper;
import org.jboss.tools.hibernate.ui.view.ViewPlugin;

public class OpenMappingActionDelegate extends OpenActionDelegate {
	private static XMLHelper helper = new XMLHelper();

	public void run(IAction action) {
    	ObjectPluginAction objectPluginAction = (ObjectPluginAction)action;
    	RootClass rootClass = (RootClass)((TreeSelection)objectPluginAction.getSelection()).getFirstElement();
		ConsoleConfiguration consoleConfiguration = (ConsoleConfiguration)(((TreeSelection)objectPluginAction.getSelection()).getPaths()[0]).getSegment(0);
		IJavaProject proj = ProjectUtils.findJavaProject(consoleConfiguration);
		java.io.File configXMLFile = consoleConfiguration.getPreferences().getConfigXMLFile();
		Document doc = OpenFileActionUtils.getDocument(consoleConfiguration, configXMLFile);
//    	IResource resource = OpenFileActionUtils.getResource(consoleConfiguration, proj, doc, configXMLFile, rootClass);
    	IResource resource = OpenFileActionUtils.getResource(consoleConfiguration, proj, configXMLFile, rootClass);

        if (resource == null) {
    		String fullyQualifiedName = HibernateUtils.getPersistentClassName(rootClass);
    		try {
    			resource = proj.findType(fullyQualifiedName).getResource();
    		} catch (JavaModelException e) {
    			ViewPlugin.getDefault().logError("Can't find mapping file.", e);
    		}
        }

    	if (resource != null && resource instanceof IFile){
            try {
            	OpenFileActionUtils.openEditor(ViewPlugin.getPage(), (IFile) resource);
            } catch (PartInitException e) {
    			ViewPlugin.getDefault().logError("Can't open mapping or source file.", e);
            }               
        }
	}
}