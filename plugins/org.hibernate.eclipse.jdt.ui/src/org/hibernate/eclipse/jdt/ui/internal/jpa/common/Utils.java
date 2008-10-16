/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.jdt.ui.internal.jpa.common;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

/**
 * 
 * 
 * @author Vitali
 */
public class Utils {

	static public org.eclipse.jdt.core.dom.CompilationUnit getCompilationUnit(
			ICompilationUnit source, boolean bindings) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(source);
		parser.setResolveBindings(bindings);
		org.eclipse.jdt.core.dom.CompilationUnit result = (org.eclipse.jdt.core.dom.CompilationUnit) parser.createAST(null);
		return result;
	}

	static public ICompilationUnit findCompilationUnit(IJavaProject javaProject, 
			String fullyQualifiedName) {
		IType lwType = null;
		try {
			lwType = javaProject.findType(fullyQualifiedName);
		} catch (JavaModelException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
		}
		ICompilationUnit resCompilationUnit = null;
		if (lwType != null) {
			resCompilationUnit = lwType.getCompilationUnit();
		}
		return resCompilationUnit;
	}

	static public ICompilationUnit findCompilationUnit(String fullyQualifiedName) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();
		ICompilationUnit resCompilationUnit = null;
		for (int i = 0; i < projects.length; i++) {
			IJavaProject javaProject = JavaCore.create(projects[i]);
			IType lwType = null;
			try {
				lwType = javaProject.findType(fullyQualifiedName);
			} catch (JavaModelException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
			}
			if (lwType != null) {
				resCompilationUnit = lwType.getCompilationUnit();
			}
			if (resCompilationUnit != null) {
				break;
			}
		}
		return resCompilationUnit;
	}
	
	static public ICompilationUnit[] findCompilationUnits(IJavaProject javaProject,
			IPath path) {
		IJavaElement javaElement = null;
		try {
			javaElement = javaProject.findElement(path.makeRelative());
		} catch (JavaModelException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
		}
		ICompilationUnit[] res = null;
		if (javaElement instanceof ICompilationUnit) {
			res = new ICompilationUnit[]{ (ICompilationUnit)javaElement };
		}
		else if (javaElement instanceof IPackageFragment) {
			try {
				res = ((IPackageFragment)javaElement).getCompilationUnits();
			} catch (JavaModelException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
			}
		}
		else if (javaElement instanceof IClassFile) {
		}
		return res;
	}

}
