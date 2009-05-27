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

@SuppressWarnings("restriction")
public class OpenDiagramActionDelegate extends OpenActionDelegate {

	private HashMap<Object, ObjectEditorInput> hashMap = new HashMap<Object, ObjectEditorInput>();

	public void run(IAction action) {
    	ObjectPluginAction objectPluginAction = (ObjectPluginAction)action;
    	Map<ConsoleConfiguration, Set<PersistentClass>> mapCC_PCs = new HashMap<ConsoleConfiguration, Set<PersistentClass>>();
    	TreePath[] paths = ((TreeSelection)objectPluginAction.getSelection()).getPaths();
    	for (int i = 0; i < paths.length; i++) {
    		Object last_el = paths[i].getLastSegment();
        	if (last_el instanceof PersistentClass) {
    			PersistentClass persClass = (PersistentClass) last_el;
    			ConsoleConfiguration consoleConfiguration = (ConsoleConfiguration)(paths[i].getFirstSegment());
    			Set<PersistentClass> setPC = mapCC_PCs.get(consoleConfiguration);
    			if (null == setPC) {
    				setPC = new HashSet<PersistentClass>();
    				mapCC_PCs.put(consoleConfiguration, setPC);
    			}
    			setPC.add(persClass);
    		}    
		}    		
    	for (Iterator<ConsoleConfiguration> it = mapCC_PCs.keySet().iterator(); it.hasNext(); ) {
    		ConsoleConfiguration consoleConfiguration = it.next();
    		Set<PersistentClass> setPC = mapCC_PCs.get(consoleConfiguration);
	    	try {
	    		openEditor(setPC, consoleConfiguration);
	    	} catch (PartInitException e) {
				ViewPlugin.getDefault().logError("Can't open mapping view.", e);		//$NON-NLS-1$
			} 
    	}
	}

	public IEditorPart openEditor(PersistentClass persClass,
			ConsoleConfiguration consoleConfiguration) throws PartInitException {
		ObjectEditorInput input = hashMap.get(persClass.getRootClass());
		if(input == null) {
			input = new ObjectEditorInput(consoleConfiguration, persClass.getRootClass());
			hashMap.put(persClass.getRootClass(), input);
		}

		return IDE.openEditor(ViewPlugin.getPage(),input ,"org.jboss.tools.hibernate.ui.veditor.editors.visualeditor");		//$NON-NLS-1$
	}

	public IEditorPart openEditor(Set<PersistentClass> setPC, ConsoleConfiguration consoleConfiguration) throws PartInitException {
		
		if (0 >= setPC.size()) {
			return null;
		}
		
		RootClass[] rcArr = new RootClass[setPC.size()];
		String id = ""; //$NON-NLS-1$
		PersistentClass persClass = null;
		int i = 0;
    	for (Iterator<PersistentClass> it = setPC.iterator(); it.hasNext(); ) {
    		persClass = it.next();
    		id += "@" + persClass.toString(); //$NON-NLS-1$
    		rcArr[i++] = persClass.getRootClass();
    	}
		ObjectEditorInput input = hashMap.get(id);
		if (null == input) {
			input = new ObjectEditorInput(consoleConfiguration, rcArr);
			hashMap.put(id, input);
		}

		return IDE.openEditor(ViewPlugin.getPage(),input ,"org.jboss.tools.hibernate.ui.veditor.editors.visualeditor");		//$NON-NLS-1$
	}
}