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
package org.jboss.tools.hibernate.ui.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ObjectPluginAction;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.ui.diagram.UiPlugin;

@SuppressWarnings("restriction")
public class OpenDiagramActionDelegate implements IObjectActionDelegate {

	//private IWorkbenchPart fPart;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		//this.fPart = targetPart;
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	@SuppressWarnings("unchecked")
	public void run(IAction action) {
    	ObjectPluginAction objectPluginAction = (ObjectPluginAction)action;
    	Map<ConsoleConfiguration, Set<PersistentClass>> mapCC_PCs = new HashMap<ConsoleConfiguration, Set<PersistentClass>>();
    	TreePath[] paths = ((TreeSelection)objectPluginAction.getSelection()).getPaths();
    	for (int i = 0; i < paths.length; i++) {
    		final Object firstSegment = paths[i].getFirstSegment();
    		if (!(firstSegment instanceof ConsoleConfiguration)) {
    			continue;
    		}
    		final ConsoleConfiguration consoleConfig = (ConsoleConfiguration)(firstSegment);
			Set<PersistentClass> setPC = mapCC_PCs.get(consoleConfig);
			if (null == setPC) {
				setPC = new HashSet<PersistentClass>();
				mapCC_PCs.put(consoleConfig, setPC);
			}
    		Object last_el = paths[i].getLastSegment();
        	if (last_el instanceof PersistentClass) {
    			PersistentClass persClass = (PersistentClass) last_el;
    			setPC.add(persClass);
    		} else if (last_el instanceof Configuration) {
    			Configuration config = (Configuration)last_el;
    			Iterator<PersistentClass> it = (Iterator<PersistentClass>)(config.getClassMappings());
    			while (it.hasNext()) {
        			setPC.add(it.next());
    			}
    		} else if (last_el instanceof ConsoleConfiguration) {
    			Configuration config = consoleConfig.getConfiguration();
    			if (config == null) {
    				consoleConfig.build();
    				consoleConfig.execute( new ExecutionContext.Command() {
    					public Object execute() {
    						if (consoleConfig.hasConfiguration()) {
    							consoleConfig.getConfiguration().buildMappings();
    						}
    						return consoleConfig;
    					}
    				} );
    				config = consoleConfig.getConfiguration();
    			}
    			Iterator<PersistentClass> it = (Iterator<PersistentClass>)(config.getClassMappings());
    			while (it.hasNext()) {
        			setPC.add(it.next());
    			}
    		}
		}    		
    	for (Iterator<ConsoleConfiguration> it = mapCC_PCs.keySet().iterator(); it.hasNext(); ) {
    		ConsoleConfiguration consoleConfiguration = it.next();
    		Set<PersistentClass> setPC = mapCC_PCs.get(consoleConfiguration);
	    	try {
	    		openEditor(setPC, consoleConfiguration);
	    	} catch (PartInitException e) {
	    		HibernateConsolePlugin.getDefault().logErrorMessage("Can't open mapping view.", e);		//$NON-NLS-1$
			} 
    	}
	}

	public IEditorPart openEditor(PersistentClass persClass,
			ConsoleConfiguration consoleConfig) throws PartInitException {
		DiagramEditorInput input = new DiagramEditorInput(consoleConfig.getName(), persClass.getRootClass());
		return IDE.openEditor(UiPlugin.getPage(), input, "org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer");		//$NON-NLS-1$
	}

	public IEditorPart openEditor(Set<PersistentClass> setPC, ConsoleConfiguration consoleConfig) throws PartInitException {
		
		if (setPC.size() <= 0) {
			return null;
		}
		RootClass[] rcArr = new RootClass[setPC.size()];
		PersistentClass persClass = null;
		int i = 0;
    	for (Iterator<PersistentClass> it = setPC.iterator(); it.hasNext(); ) {
    		persClass = it.next();
    		rcArr[i++] = persClass.getRootClass();
    	}
		DiagramEditorInput input = new DiagramEditorInput(consoleConfig.getName(), rcArr);
		return IDE.openEditor(UiPlugin.getPage(), input, "org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer");		//$NON-NLS-1$
	}
}