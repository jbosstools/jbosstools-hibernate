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
package org.hibernate.eclipse.jdt.ui.internal.jpa.actions;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.internal.filebuffers.SynchronizableDocument;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.core.JavaElementInfo;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.jdt.ui.Activator;
import org.hibernate.eclipse.jdt.ui.internal.jpa.collect.AllEntitiesInfoCollector;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.Utils;
import org.hibernate.eclipse.jdt.ui.internal.jpa.process.AllEntitiesProcessor;

/**
 *
 *
 * @author Vitali
 */
public class JPAMapToolActor {

	protected static JPAMapToolActor actor = null; 
	protected ISelection selection = null;
	protected Set<ICompilationUnit> selectionCU = new HashSet<ICompilationUnit>();
	protected AllEntitiesInfoCollector collector = new AllEntitiesInfoCollector();
	protected AllEntitiesProcessor processor = new AllEntitiesProcessor();

	protected JPAMapToolActor() {
	}

	public static JPAMapToolActor getInstance() {
		if (actor == null) {
			actor = new JPAMapToolActor();
		}
		return actor;
	}
	
	protected org.eclipse.jdt.core.dom.CompilationUnit getCompilationUnit(ICompilationUnit source) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(source);
		org.eclipse.jdt.core.dom.CompilationUnit result = (org.eclipse.jdt.core.dom.CompilationUnit) parser.createAST(null);
		return result;
	}

	public void clearSelectionCU() {
		selectionCU.clear();
	}
	
	public void addCompilationUnit(ICompilationUnit cu) {
		if (cu != null) {
			selectionCU.add(cu);
		}
	}

	public void updateSelected() {
		if (selection != null) {
			updateSelectedItems(selection);
			selection = null;
		}
		if (selectionCU.size() == 0) {
			processor.modify(null, new HashMap<String, EntityInfo>(), true);
			return;
		}
		Iterator<ICompilationUnit> it = selectionCU.iterator();
		Map<IJavaProject, Set<ICompilationUnit>> mapJP_CUSet =
			new HashMap<IJavaProject, Set<ICompilationUnit>>();
		while (it.hasNext()) {
			ICompilationUnit cu = it.next();
			Set<ICompilationUnit> set =
				mapJP_CUSet.get(cu.getJavaProject());
			if (set == null) {
				set = new HashSet<ICompilationUnit>();
				mapJP_CUSet.put(cu.getJavaProject(), set);
			}
			set.add(cu);
		}
		Iterator<Map.Entry<IJavaProject, Set<ICompilationUnit>>>
			mapIt = mapJP_CUSet.entrySet().iterator();
		while (mapIt.hasNext()) {
			Map.Entry<IJavaProject, Set<ICompilationUnit>>
				entry = mapIt.next();
			IJavaProject javaProject = entry.getKey();
			Iterator<ICompilationUnit> setIt = entry.getValue().iterator();
			collector.initCollector(javaProject);
			while (setIt.hasNext()) {
				ICompilationUnit icu = setIt.next();
				collector.collect(icu);
			}
			collector.resolveRelations();
			processor.modify(javaProject, collector.getMapCUs_Info(), true);
		}
	}

	public void updateOpen() {
		IWorkbench workbench = Activator.getDefault().getWorkbench();
		IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();

		IEditorPart editor = page.getActiveEditor();
		if (editor instanceof CompilationUnitEditor) {
			CompilationUnitEditor cue = (CompilationUnitEditor)editor;
			ICompilationUnit cu = (ICompilationUnit)cue.getViewPartInput();
			if (cu != null) {
				IJavaProject javaProject = cu.getJavaProject();
				collector.initCollector(javaProject);
				collector.collect(cu);
				collector.resolveRelations();
				processor.modify(javaProject, collector.getMapCUs_Info(), true);
			}
		}
	}

	public void makePersistent(ICompilationUnit cu) throws CoreException {
		collector.collect(cu);
	}
	
	synchronized public int getSelectedSourceSize() {
		int res = 0;
		if (selection == null) {
			res = selectionCU.size();
		}
		else if (selection instanceof TextSelection) {
			res = 1;
		}
		else if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection)selection;
			res = treeSelection.size();
		}
		return res;
	}

	synchronized private void updateSelectedItems(ISelection sel) {
		//System.out.println("Blah! " + selection); //$NON-NLS-1$
		if (sel instanceof TextSelection) {
			String fullyQualifiedName = ""; //$NON-NLS-1$
			IDocument fDocument = null;
			SynchronizableDocument sDocument = null;
			org.eclipse.jdt.core.dom.CompilationUnit resultCU = null;
			Class clazz = sel.getClass();
			Field fd = null;
			try {
				fd = clazz.getDeclaredField("fDocument"); //$NON-NLS-1$
			} catch (NoSuchFieldException e) {
				// just ignore it!
			}
			if (fd != null) {
				try {
					fd.setAccessible(true);
					fDocument = (IDocument)fd.get(sel);
					if (fDocument instanceof SynchronizableDocument) {
						sDocument = (SynchronizableDocument)fDocument;
					}
					if (sDocument != null) {
						ASTParser parser = ASTParser.newParser(AST.JLS3);
						parser.setSource(sDocument.get().toCharArray());
						parser.setResolveBindings(false);
						resultCU = (org.eclipse.jdt.core.dom.CompilationUnit) parser.createAST(null);
					}
					if (resultCU != null && resultCU.types().size() > 0 ) {
						if (resultCU.getPackage() != null) {
							fullyQualifiedName = resultCU.getPackage().getName().getFullyQualifiedName() + "."; //$NON-NLS-1$
						}
						else {
							fullyQualifiedName = ""; //$NON-NLS-1$
						}
						Object tmp = resultCU.types().get(0);
						if (tmp instanceof AbstractTypeDeclaration) {
							fullyQualifiedName += ((AbstractTypeDeclaration)tmp).getName();
						}
						if (!(tmp instanceof TypeDeclaration)) {
							// ignore EnumDeclaration & AnnotationTypeDeclaration
							fullyQualifiedName = ""; //$NON-NLS-1$
						}
					}
				} catch (IllegalArgumentException e) {
					HibernateConsolePlugin.getDefault().logErrorMessage("IllegalArgumentException: ", e); //$NON-NLS-1$
				} catch (IllegalAccessException e) {
					HibernateConsolePlugin.getDefault().logErrorMessage("IllegalAccessException: ", e); //$NON-NLS-1$
				} catch (SecurityException e) {
					HibernateConsolePlugin.getDefault().logErrorMessage("SecurityException: ", e); //$NON-NLS-1$
				}
			}
			clearSelectionCU();
			if (fullyQualifiedName.length() > 0) {
				ICompilationUnit cu = Utils.findCompilationUnit(fullyQualifiedName);
				addCompilationUnit(cu);
			}
		}
		else if (sel instanceof TreeSelection) {
			clearSelectionCU();
			TreeSelection treeSelection = (TreeSelection)sel;
			Iterator it = treeSelection.iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				processJavaElements(obj);
			}
		}
		else {
			//System.out.println("2 Blah! " + selection); //$NON-NLS-1$
			sel = null;
		}
	}
	
	protected void processJavaElements(Object obj) {
		if (obj instanceof ICompilationUnit) {
			ICompilationUnit cu = (ICompilationUnit)obj;
			addCompilationUnit(cu);
		}
		else if (obj instanceof File) {
			File file = (File)obj;
			if (file != null && file.getProject() != null) {
				IJavaProject javaProject = JavaCore.create(file.getProject());
				ICompilationUnit[] cus = Utils.findCompilationUnits(javaProject,
						file.getFullPath());
				if (cus != null) {
					for (int i = 0; i < cus.length; i++) {
						addCompilationUnit(cus[i]);
					}
				}
			}
		}
		else if (obj instanceof JavaProject) {
			JavaProject javaProject = (JavaProject)obj;
			IPackageFragmentRoot[] pfr = null;
			try {
				pfr = javaProject.getAllPackageFragmentRoots();
			} catch (JavaModelException e) {
				// just ignore it!
				//HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
			}
			if (pfr != null) {
				for (int i = 0; i < pfr.length; i++) {
					processJavaElements(pfr[i]);
				}
			}
		}
		else if (obj instanceof PackageFragment) {
			PackageFragment packageFragment = (PackageFragment)obj;
			ICompilationUnit[] cus = null;
			try {
				cus = packageFragment.getCompilationUnits();
			} catch (JavaModelException e) {
				// just ignore it!
				//HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
			}
			if (cus != null) {
				for (int i = 0; i < cus.length; i++) {
					addCompilationUnit(cus[i]);
				}
			}
		}
		else if (obj instanceof PackageFragmentRoot) {
			PackageFragmentRoot packageFragmentRoot = (PackageFragmentRoot)obj;
			JavaElement javaElement = (JavaElement)obj;
			JavaElementInfo javaElementInfo = null;
			try {
				javaElementInfo = (JavaElementInfo)javaElement.getElementInfo();
			} catch (JavaModelException e) {
				// just ignore it!
				//HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
			}
			if (javaElementInfo != null) {
				IJavaElement[] je = javaElementInfo.getChildren();
				for (int i = 0; i < je.length; i++) {
					processJavaElements(je[i]);
				}
			}
		}
		else if (obj instanceof JavaElement) {
			JavaElement javaElement = (JavaElement)obj;
			ICompilationUnit cu = javaElement.getCompilationUnit();
			addCompilationUnit(cu);
		}
		else {
			// ignore
			//System.out.println("1 Blah! " + selection); //$NON-NLS-1$
		}
	}

	synchronized public void setSelection(ISelection selection) {
		if ((selection instanceof StructuredSelection) && selection.isEmpty()) {
			// just miss this selection
			return;
		}
		this.selection = selection;
		//System.out.println("Blah! " + selection); //$NON-NLS-1$
	}

}
