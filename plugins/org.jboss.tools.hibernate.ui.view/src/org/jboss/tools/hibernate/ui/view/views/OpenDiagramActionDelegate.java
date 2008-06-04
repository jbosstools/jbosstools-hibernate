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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.ui.view.ViewPlugin;

public class OpenDiagramActionDelegate extends OpenActionDelegate {

	private HashMap hashMap = new HashMap();

	public void run(IAction action) {
    	ObjectPluginAction objectPluginAction = (ObjectPluginAction)action;
    	Map mapCC_PCs = new HashMap();
    	TreePath[] paths = ((TreeSelection)objectPluginAction.getSelection()).getPaths();
    	for (int i = 0; i < paths.length; i++) {
    		Object last_el = paths[i].getLastSegment();
        	if (last_el instanceof PersistentClass) {
    			PersistentClass persClass = (PersistentClass) last_el;
    			ConsoleConfiguration consoleConfiguration = (ConsoleConfiguration)(paths[i].getFirstSegment());
    			Set setPC = (Set)mapCC_PCs.get(consoleConfiguration);
    			if (null == setPC) {
    				setPC = new HashSet();
    				mapCC_PCs.put(consoleConfiguration, setPC);
    			}
    			setPC.add(persClass);
    		}    
		}    		
    	for (Iterator it = mapCC_PCs.keySet().iterator(); it.hasNext(); ) {
    		ConsoleConfiguration consoleConfiguration = (ConsoleConfiguration)it.next();
    		Set setPC = (Set)mapCC_PCs.get(consoleConfiguration);
	    	try {
	    		openEditor(setPC, consoleConfiguration);
	    	} catch (PartInitException e) {
				ViewPlugin.getDefault().logError("Can't open mapping view.", e);		//$NON-NLS-1$
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

	public IEditorPart openEditor(Set setPC, ConsoleConfiguration consoleConfiguration) throws PartInitException {
		
		if (0 >= setPC.size()) {
			return null;
		}
		
		RootClass[] rcArr = new RootClass[setPC.size()];
		String id = ""; //$NON-NLS-1$
		PersistentClass persClass = null;
		int i = 0;
    	for (Iterator it = setPC.iterator(); it.hasNext(); ) {
    		persClass = (PersistentClass)it.next();
    		id += "@" + persClass.toString(); //$NON-NLS-1$
    		rcArr[i++] = persClass.getRootClass();
    	}
		
		ObjectEditorInput input = (ObjectEditorInput)hashMap.get(id);
		IJavaProject proj = ProjectUtils.findJavaProject(consoleConfiguration);
			
		if (null == input) {
			input = new ObjectEditorInput(consoleConfiguration, rcArr, proj);
			hashMap.put(id, input);
		}

		return IDE.openEditor(ViewPlugin.getPage(),input ,"org.jboss.tools.hibernate.ui.veditor.editors.visualeditor");		//$NON-NLS-1$
	}
}