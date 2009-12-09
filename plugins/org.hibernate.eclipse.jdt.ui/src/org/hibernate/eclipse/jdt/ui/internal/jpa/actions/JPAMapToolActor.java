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
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.core.ClassFile;
import org.eclipse.jdt.internal.core.ExternalPackageFragmentRoot;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.core.JavaElementInfo;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.jdt.ui.Activator;
import org.hibernate.eclipse.jdt.ui.internal.JdtUiMessages;
import org.hibernate.eclipse.jdt.ui.internal.jpa.collect.AllEntitiesInfoCollector;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.Utils;
import org.hibernate.eclipse.jdt.ui.internal.jpa.process.AllEntitiesProcessor;

/**
 * Actor to execute annotation generation.
 * It is singleton.
 *
 * @author Vitali Yemialyanchyk
 */
@SuppressWarnings("restriction")
public class JPAMapToolActor {

	/**
	 * instance
	 */
	private static JPAMapToolActor actor = null;
	/**
	 * selection - start point to generate annotations
	 * could be java file, list of files, package, project, some other?
	 */
	protected ISelection selection = null;
	/**
	 * selected compilation units for startup processing,
	 * result of processing selection
	 */
	protected Set<ICompilationUnit> selectionCU = new HashSet<ICompilationUnit>();
	/**
	 * responsible to gather information
	 */
	protected AllEntitiesInfoCollector collector = new AllEntitiesInfoCollector();
	/**
	 * responsible to generate JPA annotations
	 */
	protected AllEntitiesProcessor processor = new AllEntitiesProcessor();

	protected JPAMapToolActor() {
		initPreferences();
	}

	public void initPreferences() {
		processor.initPreferences();
	}

	public static JPAMapToolActor getInstance() {
		if (actor == null) {
			actor = new JPAMapToolActor();
		}
		return actor;
	}

	/**
	 * Cleanup collection of selected elements for processing
	 */
	public void clearSelectionCU() {
		selectionCU.clear();
	}
	
	/**
	 * Adds compilation unit into collection of selected elements for processing
	 * @param cu compilation unit
	 */
	public void addCompilationUnit(ICompilationUnit cu) {
		if (cu != null) {
			IType[] types = null;
			try {
				types = cu.getTypes();
			} catch (JavaModelException e) {
				// just ignore it
			}
			if (types != null) {
				
			}
			selectionCU.add(cu);
		}
	}

	/**
	 * updates selected compilation units collection 
	 */
	public void updateSelected() {
		if (selection != null) {
			updateSelectedItems(selection);
			selection = null;
		}
		else {
			if (selectionCU.size() == 0) {
				updateOpen();
				return;
			}
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
			Set<ICompilationUnit> set = mapJP_CUSet.get(cu.getJavaProject());
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
			if (collector.getNonInterfaceCUNumber() > 0) {
				processor.setAnnotationStylePreference(collector.getAnnotationStylePreference());
				processor.modify(javaProject, collector.getMapCUs_Info(), true);
			}
			else {
				MessageDialog.openInformation(getShell(), 
						JdtUiMessages.JPAMapToolActor_message_title, 
						JdtUiMessages.JPAMapToolActor_message);
			}
		}
		processor.savePreferences();
	}

	private IWorkbenchWindow getActiveWorkbenchWindow() {
		return Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	private Shell getShell() {
		IWorkbenchWindow activeWorkbenchWindow = getActiveWorkbenchWindow();
		if (activeWorkbenchWindow != null) {
			return activeWorkbenchWindow.getShell();
		}
		return null;
	}

	/**
	 * update compilation unit of currently opened editor 
	 */
	public void updateOpen() {
		IWorkbenchWindow activeWorkbenchWindow = getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null) {
			return;
		}
		IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
		if (page == null) {
			return;
		}
		IEditorPart editor = page.getActiveEditor();
		if (editor instanceof CompilationUnitEditor) {
			CompilationUnitEditor cue = (CompilationUnitEditor)editor;
			ICompilationUnit cu = (ICompilationUnit)cue.getViewPartInput();
			if (cu != null) {
				IJavaProject javaProject = cu.getJavaProject();
				collector.initCollector(javaProject);
				collector.collect(cu);
				collector.resolveRelations();
				if (collector.getNonInterfaceCUNumber() > 0) {
					processor.modify(javaProject, collector.getMapCUs_Info(), true);
				}
				else {
					MessageDialog.openInformation(getShell(), 
							JdtUiMessages.JPAMapToolActor_message_title, 
							JdtUiMessages.JPAMapToolActor_message);
				}
			}
		}
	}

	public void makePersistent(ICompilationUnit cu) throws CoreException {
		collector.collect(cu);
	}
	
	/**
	 * @return probable number of available item to process
	 */
	synchronized public int getSelectedSourceSize() {
		int res = 0;
		if (selection == null) {
			//res = selectionCU.size();
			res = 1;
		}
		else if (selection instanceof TextSelection) {
			res = 1;
		}
		else if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection)selection;
			res = treeSelection.size();
			Iterator<?> it = treeSelection.iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				if (excludeElement(obj)) {
					res--;
				}
			}
		}
		return res;
	}

	/**
	 * @param sel - current selected workspace element for processing
	 */
	synchronized private void updateSelectedItems(ISelection sel) {
		//System.out.println("Blah! " + selection); //$NON-NLS-1$
		if (sel instanceof TextSelection) {
			String fullyQualifiedName = ""; //$NON-NLS-1$
			IDocument fDocument = null;
			SynchronizableDocument sDocument = null;
			org.eclipse.jdt.core.dom.CompilationUnit resultCU = null;
			Class<?> clazz = sel.getClass();
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
			Iterator<?> it = treeSelection.iterator();
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

	/**
	 * Check is the object in set of excluded elements
	 * @param obj
	 * @return exclusion result
	 */
	protected boolean excludeElement(Object obj) {
		boolean res = false;
		if (obj instanceof JarPackageFragmentRoot) {
			res = true;
		}
		else if (obj instanceof ClassFile) {
			res = true;
		}
		else if (obj instanceof PackageFragment) {
			PackageFragment pf = (PackageFragment)obj;
			try {
				if (pf.getKind() == IPackageFragmentRoot.K_BINARY) {
					res = true;
				}
			} catch (JavaModelException e) {
				// ignore
			}
		}
		else if (obj instanceof ExternalPackageFragmentRoot) {
			res = true;
		}
		return res;
	}
	
	/**
	 * Process object - java element to collect all it's children CompilationUnits
	 * @param obj
	 */
	public void processJavaElements(Object obj) {
		if (obj instanceof ICompilationUnit) {
			ICompilationUnit cu = (ICompilationUnit)obj;
			addCompilationUnit(cu);
		}
		else if (obj instanceof File) {
			File file = (File)obj;
			if (file.getProject() != null) {
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

	/**
	 * setup source selection node
	 * @param selection
	 */
	synchronized public void setSelection(ISelection selection) {
		//System.out.println("Blah! " + selection); //$NON-NLS-1$
		if (selection instanceof StructuredSelection && selection.isEmpty()) {
			//System.out.println("This! " + this.selection); //$NON-NLS-1$
			clearSelectionCU();
			this.selection = null;
			return;
		}
		this.selection = selection;
	}

	// setters for testing

	public void setAllEntitiesInfoCollector(AllEntitiesInfoCollector collector) {
		this.collector = collector;
	}

	public void setAllEntitiesProcessor(AllEntitiesProcessor processor) {
		this.processor = processor;
	}

	public void setSelectionCU(Set<ICompilationUnit> selectionCU) {
		this.selectionCU = selectionCU;
	}

	public int getSelectionCUSize() {
		return selectionCU.size();
	}
}
