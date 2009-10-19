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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
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

	public JPAMapToolActor actor = JPAMapToolActor.getInstance();

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		actor.updateSelected();
		//actor.updateOpen();
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
		actor.updateSelected();
		//actor.updateOpen();
		return null;
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
		boolean enable = isCUSelected();
		actor.setSelection(null);
		actor.clearSelectionCU();
		setBaseEnabled(enable);
	}
}
