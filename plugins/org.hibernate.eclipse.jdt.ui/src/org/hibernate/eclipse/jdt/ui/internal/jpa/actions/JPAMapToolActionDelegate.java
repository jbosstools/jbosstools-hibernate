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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 *
 *
 * @author Vitali
 */
public class JPAMapToolActionDelegate implements IObjectActionDelegate,
	IEditorActionDelegate, IViewActionDelegate, IHandler {

	public JPAMapToolActor actor = JPAMapToolActor.getInstance();

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		actor.updateSelected();
		//actor.updateOpen();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		actor.updateSelectedItems(selection);
		if (action != null) {
			action.setEnabled(actor.getSelectedSize() > 0);
		}
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (action != null) {
			action.setEnabled(actor.getSelectedSize() > 0);
		}
	}

	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	public void dispose() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		actor.updateSelected();
		//actor.updateOpen();
		return null;
	}

	public boolean isEnabled() {
		return (actor.getSelectedSize() > 0);
	}

	public boolean isHandled() {
		return true;
	}

	public void removeHandlerListener(IHandlerListener handlerListener) {
	}

	public void init(IViewPart view) {
		view = null;
	}
}
