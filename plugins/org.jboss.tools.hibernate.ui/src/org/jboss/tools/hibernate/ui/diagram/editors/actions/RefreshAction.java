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

import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.ui.actions.ActionFactory;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramContentOutlinePage;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;

/**
 * Refresh diagram action
 * 
 * @author Vitali Yemialyanchyk
 */
public class RefreshAction extends WorkbenchPartAction {

	public static final String ACTION_ID = ActionFactory.REFRESH.getId();
	
	protected DiagramContentOutlinePage outline = null;

	public RefreshAction(DiagramViewer editor) {
		super(editor);
		setId(ACTION_ID);
	}
	
	protected DiagramViewer getDiagramViewer() {
		return (DiagramViewer)getWorkbenchPart();
	}

	public void run() {
		getDiagramViewer().getViewerContents().refresh();
		if (outline != null) {
			// synchronize contents of outline page
			outline.setContents(outline.getOrmDiagram());
		}
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}
	
	public void setOutlinePage(DiagramContentOutlinePage outline) {
		this.outline = outline;
	}
}
