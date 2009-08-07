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

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;

/**
 * @author Vitali Yemialyanchyk
 */
public class DiagramBaseAction extends Action {
	
	protected DiagramViewer editor;
	
	public DiagramBaseAction(DiagramViewer editor) {
		this.editor = editor;
	}

	protected DiagramViewer getDiagramViewer() {
		DiagramViewer res = editor;
		if (res == null && PlatformUI.getWorkbench() != null && 
				PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null &&
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() != null &&
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart() != null) {
			IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
			if (part instanceof DiagramViewer) {
				res = (DiagramViewer)part;
			}
		}
		return res;
	}

}
