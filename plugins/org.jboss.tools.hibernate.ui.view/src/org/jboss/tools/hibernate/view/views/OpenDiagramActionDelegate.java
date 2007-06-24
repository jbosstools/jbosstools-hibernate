/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.view.views;

import java.util.HashMap;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ObjectPluginAction;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.view.ViewPlugin;

public class OpenDiagramActionDelegate implements IObjectActionDelegate {

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
    	HashMap hashMap = new HashMap();
    	ObjectPluginAction objectPluginAction = (ObjectPluginAction)action;
//    	TreeSelection treeSelection = (TreeSelection)objectPluginAction.getSelection();
    	Object rootClass = ((TreeSelection)objectPluginAction.getSelection()).getFirstElement();
		ObjectEditorInput input = (ObjectEditorInput)hashMap.get(rootClass);
		Configuration configuration = (Configuration)(((TreeSelection)objectPluginAction.getSelection()).getPaths()[0]).getSegment(1);
		if(input == null) {
			input = new ObjectEditorInput(configuration, rootClass);
			hashMap.put(rootClass, input);
		}
		try {
//			IDE.openEditor(ViewPlugin.getPage(),input ,"org.hibernate.eclipse.orm.veditor.editors.VizualEditor");
			IDE.openEditor(ViewPlugin.getPage(),input ,"org.jboss.tools.hibernate.veditor.editors.vizualeditor");
		} catch (PartInitException e) {
//			ExceptionHandler.logThrowableError(e,"OpenEditor");              
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
