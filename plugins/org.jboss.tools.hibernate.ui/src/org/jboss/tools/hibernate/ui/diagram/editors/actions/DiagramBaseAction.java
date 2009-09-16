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

	protected DiagramViewer getDiagramViewer() {
		DiagramViewer res = editor;
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
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
}
