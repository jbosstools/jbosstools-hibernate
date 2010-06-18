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

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.hibernate.eclipse.jdt.ui.Activator;

/**
 * Main menu action delegate for "Generate Hibernate/JPA annotations..."
 *
 * @author Vitali
 */
@SuppressWarnings("restriction")
public class JPAMapToolActionDelegate extends AbstractHandler implements IObjectActionDelegate,
	IEditorActionDelegate, IViewActionDelegate {

	protected JPAMapToolActor actor = new JPAMapToolActor();

	protected WeakReference<Object> refContextObject = null;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		runInternal();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		actor.setSelection(selection);
		if (action != null) {
			action.setEnabled(actor.getSelectedSourceSize() > 0);
		}
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (action != null) {
			action.setEnabled(actor.getSelectedSourceSize() > 0);
		}
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		runInternal();
		return null;
	}

	public void runInternal() {
		if (refContextObject != null) {
			processContextObjectElements();
		}
		actor.updateSelected(Integer.MAX_VALUE);
	}

	public void init(IViewPart view) {
	}
	public boolean isCUSelected() {
		IWorkbench workbench = Activator.getDefault().getWorkbench();
		if (workbench == null || workbench.getActiveWorkbenchWindow() == null) {
			return false;
		}
		IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
		if (page == null) {
			return false;
		}
		IEditorPart editor = page.getActiveEditor();
		if (editor instanceof CompilationUnitEditor) {
			return true;
		}
		return false;
	}

	public void setEnabled(Object evaluationContext) {
		if (evaluationContext instanceof EvaluationContext) {
			EvaluationContext ec = (EvaluationContext)evaluationContext;
			Object obj = ec.getDefaultVariable();
			refContextObject = new WeakReference<Object>(obj);
		} else {
			refContextObject = null;
		}
		setBaseEnabled(checkEnabled());
	}

	protected boolean checkEnabled() {
		boolean enable = false;
		Object obj = refContextObject != null ? refContextObject.get() : null;
		if (obj == null) {
			return enable;
		}
		List<?> list = null;
		if (obj instanceof List) {
			list = (List<?>)obj;
		} else {
			list = Collections.singletonList(obj);
		}
		Iterator<?> it = list.iterator();
		while (it.hasNext() && !enable) {
			Object obj2 = it.next();
			if (!(obj2 instanceof IJavaElement)) {
				continue;
			}
			int kind = IPackageFragmentRoot.K_SOURCE;
			if (obj2 instanceof IPackageFragment) {
				IPackageFragment pf = (IPackageFragment)obj2;
				try {
					kind = pf.getKind();
				} catch (JavaModelException e) {
					kind = IPackageFragmentRoot.K_BINARY;
				}
			} else if (obj2 instanceof IPackageFragmentRoot) {
				IPackageFragmentRoot pfr = (IPackageFragmentRoot)obj2;
				try {
					kind = pfr.getKind();
				} catch (JavaModelException e) {
					kind = IPackageFragmentRoot.K_BINARY;
				}
			}
			if (kind == IPackageFragmentRoot.K_SOURCE) {
				enable = true;
			}
		}
		if (!enable) {
			enable = isCUSelected();
		}
		return enable;
	}

	public void processContextObjectElements() {
		actor.setSelection(null);
		actor.clearSelectionCU();
		Object obj = refContextObject != null ? refContextObject.get() : null;
		if (obj == null) {
			return;
		}
		if (obj instanceof List) {
			@SuppressWarnings("rawtypes")
			Iterator it = ((List)obj).iterator();
			while (it.hasNext()) {
				obj = it.next();
				actor.processJavaElements(obj);
			}
		} else {
			actor.processJavaElements(obj);
		}
	}
}
