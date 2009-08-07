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

import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;

/**
 * @author Vitali Yemialyanchyk
 */
public class CollapseAllAction extends DiagramBaseAction {

	public static final String ACTION_ID = "collapse_all_id"; //$NON-NLS-1$

	public CollapseAllAction(DiagramViewer editor) {
		super(editor);
		setId(ACTION_ID);
		setText(DiagramViewerMessages.CollapseAllAction_collapse_all);
		//setImageDescriptor(ImageDescriptor.createFromFile(
		//		DiagramViewer.class, "icons/export.png"));
	}

	public void run() {
		getDiagramViewer().getViewerContents().collapseAll();
	}
}
