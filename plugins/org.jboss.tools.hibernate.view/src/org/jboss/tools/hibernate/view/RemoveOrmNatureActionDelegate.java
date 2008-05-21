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
package org.jboss.tools.hibernate.view;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;


/**
 * @author Tau from Minsk
 * Created on 20.04.2006 
 *
 */
public class RemoveOrmNatureActionDelegate implements IObjectActionDelegate {

	private IProject project;
	
	/**
	 * 
	 */
	public RemoveOrmNatureActionDelegate() {
		super();
		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW )	ExceptionHandler.logInfo("RemoveOrmNatureAction()" );		
	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		
		if (action != null && !action.isEnabled()) return;
		
		RemoveOrmNatureAction removeOrmNatureAction = new RemoveOrmNatureAction(project);
		removeOrmNatureAction.runAction();
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection= (IStructuredSelection) selection;
			Object first= structuredSelection.getFirstElement();
			if (first instanceof IJavaProject) {
				project = ((IJavaProject) first).getProject();
			} else if (first instanceof IProject) {
				project = (IProject) first;
			}
		}
		if (project == null) return;
		try {
			if (project.getNature(OrmCore.ORM2NATURE_ID) == null) {
				action.setEnabled(false);
			}
		} catch (CoreException e) {
        	// TODO (tau-tau) for Exception
			ExceptionHandler.logThrowableError(e,null);
		}
		
	}
	
}