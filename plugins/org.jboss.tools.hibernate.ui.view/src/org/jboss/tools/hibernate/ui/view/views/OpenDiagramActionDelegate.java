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

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ObjectPluginAction;
import org.hibernate.console.ConsoleConfiguration;
import org.jboss.tools.hibernate.ui.view.ViewPlugin;

public class OpenDiagramActionDelegate extends OpenActionDelegate {
	private HashMap hashMap = new HashMap();

	public void run(IAction action) {
    	ObjectPluginAction objectPluginAction = (ObjectPluginAction)action;
    	Object rootClass = ((TreeSelection)objectPluginAction.getSelection()).getFirstElement();
		ObjectEditorInput input = (ObjectEditorInput)hashMap.get(rootClass);
		ConsoleConfiguration consoleConfiguration = (ConsoleConfiguration)(((TreeSelection)objectPluginAction.getSelection()).getPaths()[0]).getSegment(0);
		
		IJavaProject proj = OpenFileActionUtils.findJavaProject(consoleConfiguration);
			
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
}