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
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ObjectPluginAction;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.mapping.PersistentClass;
import org.jboss.tools.hibernate.ui.view.ViewPlugin;

public class OpenDiagramActionDelegate extends OpenActionDelegate {
	private HashMap hashMap = new HashMap();

	public void run(IAction action) {
    	ObjectPluginAction objectPluginAction = (ObjectPluginAction)action;
    	TreePath[] paths = ((TreeSelection)objectPluginAction.getSelection()).getPaths();
    	for (int i = 0; i < paths.length; i++) {
    		Object last_el = paths[i].getLastSegment();
        	if (last_el instanceof PersistentClass) {
    			PersistentClass persClass = (PersistentClass) last_el;
    			ConsoleConfiguration consoleConfiguration = (ConsoleConfiguration)(paths[i].getFirstSegment());
    	    	
    	    	try {
    	    		openEditor(persClass, consoleConfiguration);
    	    	} catch (PartInitException e) {
    				ViewPlugin.getDefault().logError("Can't open mapping view.", e);		//$NON-NLS-1$
    			} 
    		}    
		}    		
	}

	public IEditorPart openEditor(PersistentClass persClass,
			ConsoleConfiguration consoleConfiguration) throws PartInitException {
		ObjectEditorInput input = (ObjectEditorInput)hashMap.get(persClass.getRootClass());
		
		
		IJavaProject proj = ProjectUtils.findJavaProject(consoleConfiguration);
			
		if(input == null) {
			input = new ObjectEditorInput(consoleConfiguration, persClass.getRootClass(), proj);
			hashMap.put(persClass.getRootClass(), input);
		}

		return IDE.openEditor(ViewPlugin.getPage(),input ,"org.jboss.tools.hibernate.ui.veditor.editors.visualeditor");		//$NON-NLS-1$
	}
}