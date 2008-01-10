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
package org.hibernate.eclipse.console.test.mappingproject;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Dmitry Geraskov
 * @deprecated - never used
 * just to test that our external Unit tests work right with MappingTestProject
 */
public class OpenJavaEditors extends TestCase {
	
	public void testOpenJavaEditor() throws JavaModelException {
		MappingTestProject mapProject = MappingTestProject.getTestProject();
		
		IPackageFragmentRoot[] roots = mapProject.getIJavaProject().getAllPackageFragmentRoots();	
	    for (int i = 0; i < roots.length; i++) {
	    	if (roots[i].getClass() != PackageFragmentRoot.class) continue;
			PackageFragmentRoot packageFragmentRoot = (PackageFragmentRoot) roots[i];
			IJavaElement[] els = packageFragmentRoot.getChildren();//.getCompilationUnits();
			for (int j = 0; j < els.length; j++) {
				IJavaElement javaElement = els[j];
				if (javaElement instanceof IPackageFragment){
					ICompilationUnit[] cus = ((IPackageFragment)javaElement).getCompilationUnits();
					for (int k = 0; k < cus.length; k++) {
						ICompilationUnit compilationUnit = cus[k];
						FileEditorInput input = new FileEditorInput((IFile) compilationUnit.getCorrespondingResource());						
						try {
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, JavaUI.ID_CU_EDITOR );
						} catch (PartInitException e) {
							fail("Error opening CompilationUnit: " + e.getMessage());
						}
					}
				}
			}		
		}

		//PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		}
}
