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
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ObjectPluginAction;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
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
    	Map<ConsoleConfiguration, Set<IPersistentClass>> mapCC_PCs = new HashMap<ConsoleConfiguration, Set<IPersistentClass>>();
    	TreePath[] paths = ((TreeSelection)objectPluginAction.getSelection()).getPaths();
    	for (int i = 0; i < paths.length; i++) {
    		final Object firstSegment = paths[i].getFirstSegment();
    		if (!(firstSegment instanceof ConsoleConfiguration)) {
    			continue;
    		}
    		final ConsoleConfiguration consoleConfig = (ConsoleConfiguration)(firstSegment);
			Set<IPersistentClass> setPC = mapCC_PCs.get(consoleConfig);
			if (null == setPC) {
				setPC = new HashSet<IPersistentClass>();
				mapCC_PCs.put(consoleConfig, setPC);
			}
    		Object last_el = paths[i].getLastSegment();
        	if (last_el instanceof IPersistentClass) {
    			IPersistentClass persClass = (IPersistentClass) last_el;
    			setPC.add(persClass);
    		} else if (last_el instanceof IConfiguration) {
    			IConfiguration config = (IConfiguration)last_el;
    			Iterator<IPersistentClass> it = config.getClassMappings();
    			while (it.hasNext()) {
        			setPC.add(it.next());
    			}
    		} else if (last_el instanceof ConsoleConfiguration) {
    			IConfiguration config = consoleConfig.getConfiguration();
    			if (config == null) {
    				try {
        				consoleConfig.build();
    				} catch (Exception he) {
    					HibernateConsolePlugin.getDefault().showError(
    						HibernateConsolePlugin.getShell(), 
    						DiagramViewerMessages.OpenDiagramActionDelegate_could_not_load_configuration + 
    						' ' + consoleConfig.getName(), he);
    				}
					if (consoleConfig.hasConfiguration()) {
						consoleConfig.buildMappings();
					}
    				config = consoleConfig.getConfiguration();
    			}
    			if (config != null) {
	    			Iterator<IPersistentClass> it = config.getClassMappings();
	    			while (it.hasNext()) {
	        			setPC.add(it.next());
	    			}
    			}
    		}
		}    		
    	for (Iterator<ConsoleConfiguration> it = mapCC_PCs.keySet().iterator(); it.hasNext(); ) {
    		ConsoleConfiguration consoleConfiguration = it.next();
    		Set<IPersistentClass> setPC = mapCC_PCs.get(consoleConfiguration);
	    	try {
	    		openEditor(setPC, consoleConfiguration);
	    	} catch (PartInitException e) {
	    		HibernateConsolePlugin.getDefault().logErrorMessage("Can't open mapping view.", e);		//$NON-NLS-1$
			} 
    	}
	}

	public IEditorPart openEditor(IPersistentClass persClass,
			ConsoleConfiguration consoleConfig) throws PartInitException {
		DiagramEditorInput input = new DiagramEditorInput(consoleConfig.getName(), persClass.getRootClass());
		IWorkbenchPage page = UiPlugin.getPage();
		IEditorPart result = IDE.openEditor(UiPlugin.getPage(), input, "org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer");		//$NON-NLS-1$
		return result;
	}

	public IEditorPart openEditor(Set<IPersistentClass> setPC, ConsoleConfiguration consoleConfig) throws PartInitException {
		
		IPersistentClass[] rcArr = new IPersistentClass[setPC.size()];
		IPersistentClass persClass = null;
		int i = 0;
    	for (Iterator<IPersistentClass> it = setPC.iterator(); it.hasNext(); ) {
    		persClass = it.next();
    		rcArr[i++] = persClass.getRootClass();
    	}
		DiagramEditorInput input = new DiagramEditorInput(consoleConfig.getName(), rcArr);
		return IDE.openEditor(UiPlugin.getPage(), input, "org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer");		//$NON-NLS-1$
	}
}