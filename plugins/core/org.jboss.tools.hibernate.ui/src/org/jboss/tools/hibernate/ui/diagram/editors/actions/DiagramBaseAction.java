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
package org.jboss.tools.hibernate.ui.diagram.editors.actions;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramContentOutlinePage;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;

/**
 * @author Vitali Yemialyanchyk
 */
public class DiagramBaseAction extends Action {
	
	protected DiagramViewer editor;
	
	public DiagramBaseAction(DiagramViewer editor) {
		this.editor = editor;
	}
	
	public DiagramBaseAction(DiagramViewer editor, int style) {
		super(null, style);
		this.editor = editor;
	}

	protected DiagramViewer getDiagramViewer() {
		DiagramViewer res = editor;
		final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (workbenchWindow == null) {
			return res;
		}
		final IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
		if (workbenchPage == null) {
			return res;
		}
		IWorkbenchPart part = workbenchPage.getActivePart();
		if (part instanceof DiagramViewer) {
			res = (DiagramViewer)part;
		} else if (part instanceof ContentOutline) {
			ContentOutline co = (ContentOutline)part;
			if (co.getCurrentPage() instanceof DiagramContentOutlinePage) {
				DiagramContentOutlinePage dcop = (DiagramContentOutlinePage)co.getCurrentPage();
				res = dcop.getEditor();
			}
		}
		return res;
	}
	
	/**
	 * Executes the given {@link Command} using the command stack.  The stack is obtained by
	 * calling {@link #getCommandStack()}, which uses <code>IAdapatable</code> to retrieve the
	 * stack from the workbench part.
	 * @param command the command to execute
	 */
	protected void execute(Command command) {
		if (command == null || !command.canExecute()) {
			return;
		}
		getCommandStack().execute(command);
	}

	/**
	 * Returns the editor's command stack. This is done by asking the workbench part for its
	 * CommandStack via 
	 * {@link org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)}.
	 * @return the command stack
	 */
	protected CommandStack getCommandStack() {
		return (CommandStack)getDiagramViewer().getAdapter(CommandStack.class);
	}
}
