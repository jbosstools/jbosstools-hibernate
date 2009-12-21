/*******************************************************************************
  * Copyright (c) 2007-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.jdt.ui.internal.jpa.collect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.internal.resources.File;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.core.JavaElementInfo;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jface.viewers.StructuredSelection;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.Utils;

/**
 * @author Vitali
 */
@SuppressWarnings("restriction")
public class CompilationUnitCollector {
	/**
	 * selection - start point for wizard selection update page
	 */
	protected List<Object> selection2UpdateList = new ArrayList<Object>();
	/**
	 * selected compilation units for startup processing,
	 * result of processing selection
	 */
	protected Set<ICompilationUnit> selectionCU = new HashSet<ICompilationUnit>();

	/**
	 * Cleanup collection of selected elements for processing
	 */
	public void clearSelectionCU() {
		selectionCU.clear();
	}

	public Iterator<ICompilationUnit> setSelectionCUIterator() {
		return this.selectionCU.iterator();
	}

	public void setSelectionCU(Set<ICompilationUnit> selectionCU) {
		this.selectionCU = selectionCU;
	}

	public int getSelectionCUSize() {
		return selectionCU.size();
	}

	public void clearSelection2UpdateList() {
		selection2UpdateList.clear();
	}

	public StructuredSelection createSelection2Update() {
		return new StructuredSelection(selection2UpdateList);
	}

	/**
	 * Adds compilation unit into collection of selected elements for processing
	 * @param cu compilation unit
	 */
	public void addCompilationUnit(ICompilationUnit cu, boolean bCollect) {
		if (cu != null) {
			IType[] types = null;
			try {
				types = cu.getTypes();
			} catch (JavaModelException e) {
				// just ignore it
			}
			if (types != null) {
				if (bCollect) {
					for (int i = 0; i < types.length; i++) {
						if (types[i] instanceof SourceType) {
							selection2UpdateList.add(types[i]);
						}
					}
					bCollect = false;
				}
			}
			selectionCU.add(cu);
		}
	}
	
	/**
	 * Process object - java element to collect all it's children CompilationUnits
	 * @param obj
	 */
	public void processJavaElements(Object obj, boolean bCollect) {
		if (obj instanceof ICompilationUnit) {
			ICompilationUnit cu = (ICompilationUnit)obj;
			addCompilationUnit(cu, bCollect);
		} else if (obj instanceof File) {
			File file = (File)obj;
			if (file.getProject() != null) {
				IJavaProject javaProject = JavaCore.create(file.getProject());
				ICompilationUnit[] cus = Utils.findCompilationUnits(javaProject,
						file.getFullPath());
				if (cus != null) {
					for (int i = 0; i < cus.length; i++) {
						addCompilationUnit(cus[i], bCollect);
					}
				}
			}
		} else if (obj instanceof JavaProject) {
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
					processJavaElements(pfr[i], bCollect);
				}
			}
		} else if (obj instanceof PackageFragment) {
			PackageFragment packageFragment = (PackageFragment)obj;
			ICompilationUnit[] cus = null;
			try {
				cus = packageFragment.getCompilationUnits();
			} catch (JavaModelException e) {
				// just ignore it!
				//HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
			}
			if (cus != null && cus.length > 0) {
				if (bCollect) {
					selection2UpdateList.add(obj);
					bCollect = false;
				}
				for (int i = 0; i < cus.length; i++) {
					addCompilationUnit(cus[i], bCollect);
				}
			}
		} else if (obj instanceof PackageFragmentRoot) {
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
					processJavaElements(je[i], true);
				}
			}
		} else if (obj instanceof JavaElement) {
			JavaElement javaElement = (JavaElement)obj;
			ICompilationUnit cu = javaElement.getCompilationUnit();
			addCompilationUnit(cu, bCollect);
		} else if (obj instanceof SourceType) {
			if (bCollect) {
				selection2UpdateList.add(obj);
				bCollect = false;
			}
			SourceType sourceType = (SourceType)obj;
			processJavaElements(sourceType.getJavaModel(), bCollect);
		} else {
			// ignore
			//System.out.println("1 Blah! " + selection); //$NON-NLS-1$
		}
	}
	
	public void collectFromSelection2UpdateList() {
		clearSelectionCU();
		Iterator<Object> it = selection2UpdateList.iterator();
		while (it.hasNext()) {
			processJavaElements(it.next(), true);
		}
	}
}
