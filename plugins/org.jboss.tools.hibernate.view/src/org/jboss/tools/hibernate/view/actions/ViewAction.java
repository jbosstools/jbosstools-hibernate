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
package org.jboss.tools.hibernate.view.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.view.ViewPlugin;
import org.jboss.tools.hibernate.view.views.ExplorerBase;
import org.jboss.tools.hibernate.view.views.ExplorerClass;


public class ViewAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	/**
	 * The constructor.
	 */
	public ViewAction() {
	}

	/**
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
	    showViews();
	}
	
	private IWorkbenchPage getPage(){
	    IWorkbench workbench = PlatformUI.getWorkbench();
	    IWorkbenchWindow window1 = workbench.getActiveWorkbenchWindow();
	    return window1.getActivePage();
	}

	private void showViews(){
		
	    try {
			ExplorerClass explorer = (ExplorerClass) getPage().showView(ExplorerClass.getExplorerId(), null, IWorkbenchPage.VIEW_ACTIVATE);
			explorer.setOrmProjects(OrmCore.getDefault().getOrmModel());
		} catch (PartInitException e) {
			ViewPlugin.getPluginLog().logError(e);			
		}
		
		try {
			ExplorerBase explorerBase = (ExplorerBase) getPage().showView(ExplorerBase.getExplorerId(),null, IWorkbenchPage.VIEW_VISIBLE);
			explorerBase.setOrmProjects(OrmCore.getDefault().getOrmModel());
		} catch (PartInitException e) {
			ViewPlugin.getPluginLog().logError(e);			
		}
	}

	/**
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	
	private Object createBox1(){
		
		String name = "ormHibernate-jsf";
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name); 

		IJavaProject javaProject = JavaCore.create(project);
		IJavaModel javaModel = javaProject.getJavaModel();
		
		return javaModel;		
	}
}