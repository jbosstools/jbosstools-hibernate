/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation, JBoss Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Max Rydahl Andersen - extracted to use in general 
 *******************************************************************************/
package org.hibernate.eclipse.console.utils.xpl;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.viewsupport.IViewPartInputProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

public class SelectionHelper {

	static public IType getClassFromElement(IJavaElement element) {
		IType classToTest= null;
		if (element != null) {			
			// evaluate the enclosing type
			IType typeInCompUnit= (IType) element.getAncestor(IJavaElement.TYPE);
			if (typeInCompUnit != null) {
				if (typeInCompUnit.getCompilationUnit() != null) {
					classToTest= typeInCompUnit;
				}
			} else {
				ICompilationUnit cu= (ICompilationUnit) element.getAncestor(IJavaElement.COMPILATION_UNIT);
				if (cu != null) 
					classToTest= cu.findPrimaryType();
				else {
					if (element instanceof IClassFile) {
						try {
							IClassFile cf= (IClassFile) element;
							if (cf.isStructureKnown())
								classToTest= cf.getType();
						} catch(JavaModelException e) {
							HibernateConsolePlugin.getDefault().log(e);
						}
					}					
				}
			}		
		}
		return classToTest;
	}
	
	/**
	 * Utility method to inspect a selection to find a Java element. 
	 * 
	 * @param selection the selection to be inspected
	 * @return a Java element to be used as the initial selection, or <code>null</code>,
	 * if no Java element exists in the given selection
	 */
	static public IJavaElement getInitialJavaElement(ISelection simpleSelection) {
		
		IJavaElement jelem= null;
		if (simpleSelection != null && !simpleSelection.isEmpty() && simpleSelection instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) simpleSelection;
			Object selectedElement= selection.getFirstElement();
			if (selectedElement instanceof IAdaptable) {
				IAdaptable adaptable= (IAdaptable) selectedElement;			
				
				jelem= (IJavaElement) adaptable.getAdapter(IJavaElement.class);
				if (jelem == null) {
					IResource resource= (IResource) adaptable.getAdapter(IResource.class);
					if (resource != null && resource.getType() != IResource.ROOT) {
						while (jelem == null && resource.getType() != IResource.PROJECT) {
							resource= resource.getParent();
							jelem= (IJavaElement) resource.getAdapter(IJavaElement.class);
						}
						if (jelem == null) {
							jelem= JavaCore.create(resource); // java project
						}
					}
				}
			}
		}
		if (jelem == null) {
			IWorkbenchPart part= getActivePage().getActivePart();
			if (part instanceof ContentOutline) {
				part= getActivePage().getActiveEditor();
			}
			
			if (part instanceof IViewPartInputProvider) {
				Object elem= ((IViewPartInputProvider)part).getViewPartInput();
				if (elem instanceof IJavaElement) {
					jelem= (IJavaElement) elem;
				}
			}
		}

		/*if (jelem == null || jelem.getElementType() == IJavaElement.JAVA_MODEL) {
			try {
				IJavaProject[] projects= JavaCore.create(getWorkspaceRoot()).getJavaProjects();
				if (projects.length == 1) {
					jelem= projects[0];
				}
			} catch (JavaModelException e) {
				JavaPlugin.log(e);
			}
		}*/
		return jelem;
	}
	
	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow window= getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			return null;
		return getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}
	
	static public IWorkbench getWorkbench() {
        return PlatformUI.getWorkbench();
    }
	
}
