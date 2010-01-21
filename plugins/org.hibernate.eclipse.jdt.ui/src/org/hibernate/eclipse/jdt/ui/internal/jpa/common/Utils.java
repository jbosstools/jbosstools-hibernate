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
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * Compilation unit common functions
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
		IType lwType = findType(javaProject, fullyQualifiedName);
		if (lwType != null) {
			return lwType.getCompilationUnit();
		}
		return null;
	}
	
	static public IType findType(IJavaProject javaProject, 
			String fullyQualifiedName) {
		IType lwType = null;
		if (javaProject != null) {
			try {
				lwType = javaProject.findType(fullyQualifiedName);
			} catch (JavaModelException e) {
				// just ignore it!
				//HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
			}
		}
		return lwType;
	}

	static public ICompilationUnit findCompilationUnit(String fullyQualifiedName) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();
		ICompilationUnit resCompilationUnit = null;
		for (int i = 0; i < projects.length; i++) {
			if (projects[i].exists()){/*It is not required project be opened, so use exists method*/
				IJavaProject javaProject = JavaCore.create(projects[i]);
				resCompilationUnit = findCompilationUnit(javaProject, fullyQualifiedName);

				if (resCompilationUnit != null) {
					break;
				}
			}
		}
		return resCompilationUnit;
	}

	static public IProject findProject(String projectName) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();
		IProject res = null;
		for (int i = 0; i < projects.length && res == null; i++) {
			if (projects[i].exists() && projects[i].getName().compareTo(projectName) == 0) {
				res = projects[i];
			}
		}
		return res;
	}

	static public IJavaProject findJavaProject(String projectName) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();
		IJavaProject res = null;
		for (int i = 0; i < projects.length && res == null; i++) {
			if (projects[i].exists() && projects[i].getName().compareTo(projectName) == 0) {
				IJavaProject javaProject = JavaCore.create(projects[i]);
				res = javaProject;
			}
		}
		return res;
	}
	
	static public ICompilationUnit[] findCompilationUnits(IJavaProject javaProject,
			IPath path) {
		IJavaElement javaElement = null;
		try {
			javaElement = javaProject.findElement(path.makeRelative());
		} catch (JavaModelException e) {
			// just ignore it!
			//HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
		}
		ICompilationUnit[] res = null;
		if (javaElement != null) {
			if (javaElement instanceof ICompilationUnit) {
				res = new ICompilationUnit[]{ (ICompilationUnit)javaElement };
			}
			else if (javaElement instanceof IPackageFragment) {
				try {
					res = ((IPackageFragment)javaElement).getCompilationUnits();
				} catch (JavaModelException e) {
					// just ignore it!
					//HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
				}
			}
			else if (javaElement instanceof IClassFile) {
			}
		}
		return res;
	}

	static public String refTypeToStr(RefType rt) {
		if (rt == RefType.ONE2ONE) {
			return "1-to-1"; //$NON-NLS-1$
		}
		else if (rt == RefType.ONE2MANY) {
			return "1-to-n"; //$NON-NLS-1$
		}
		else if (rt == RefType.MANY2ONE) {
			return "n-to-1"; //$NON-NLS-1$
		}
		else if (rt == RefType.MANY2MANY) {
			return "n-to-n"; //$NON-NLS-1$
		}
		return "undef"; //$NON-NLS-1$
	}

	static public RefType strToRefType(String str) {
		if ("1-to-1".equals(str)) { //$NON-NLS-1$
			return RefType.ONE2ONE;
		}
		else if ("1-to-n".equals(str)) { //$NON-NLS-1$
			return RefType.ONE2MANY;
		}
		else if ("n-to-1".equals(str)) { //$NON-NLS-1$
			return RefType.MANY2ONE;
		}
		else if ("n-to-n".equals(str)) { //$NON-NLS-1$
			return RefType.MANY2MANY;
		}
		return RefType.UNDEF;
	}

	static public String ownerTypeToStr(OwnerType ot) {
		if (ot == OwnerType.YES) {
			return "yes"; //$NON-NLS-1$
		}
		else if (ot == OwnerType.NO) {
			return "no"; //$NON-NLS-1$
		}
		return "undef"; //$NON-NLS-1$
	}

	static public OwnerType strToOwnerType(String str) {
		str = str.toLowerCase();
		if ("yes".equals(str)) { //$NON-NLS-1$
			return OwnerType.YES;
		}
		else if ("no".equals(str)) { //$NON-NLS-1$
			return OwnerType.NO;
		}
		return OwnerType.UNDEF;
	}
	
	public static boolean isImplementInterface(ITypeBinding[] interfaces, String parentInterface){
		for (int i = 0; i < interfaces.length; i++) {
			ITypeBinding typeBinding = interfaces[i];
			if (typeBinding==null) continue;
			if (parentInterface.equals(typeBinding.getBinaryName())) return true;
			if (isImplementInterface(typeBinding.getInterfaces(), parentInterface)) return true;
		}
		return false;
	}
	
	public static ITypeBinding[] getAllInterfaces(ITypeBinding tb){
		ITypeBinding[] interfaces = tb.getInterfaces();
		if (tb.isInterface()) {				
			ITypeBinding[] allInterfaces = new ITypeBinding[interfaces.length + 1];
			System.arraycopy(interfaces, 0, allInterfaces, 0, interfaces.length);
			allInterfaces[interfaces.length] = tb;
			interfaces = allInterfaces;
		}	
		return interfaces;
	}
	
	public static String getFieldNameByGetter(MethodDeclaration node){
		if (node.parameters().size() != 0) return null;
		String methodName = node.getName().getIdentifier();
		if (methodName.startsWith("get") && methodName.length() > 3){ //$NON-NLS-1$
			methodName = methodName.substring(3);
			return Character.toLowerCase(methodName.charAt(0)) + methodName.substring(1);
		}
		else if (methodName.startsWith("is") && methodName.length() > 2){ //$NON-NLS-1$
			methodName = methodName.substring(2);
			return Character.toLowerCase(methodName.charAt(0)) + methodName.substring(1);
		}
		return null;
	}
}
