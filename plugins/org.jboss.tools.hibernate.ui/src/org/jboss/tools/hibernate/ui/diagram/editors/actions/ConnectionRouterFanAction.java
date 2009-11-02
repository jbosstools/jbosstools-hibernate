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

import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;
import org.jboss.tools.hibernate.ui.diagram.editors.parts.DiagramEditPart;

/**
 * Connect figures with direct line connections
 * 
 * @author Vitali Yemialyanchyk
 */
public class ConnectionRouterFanAction extends DiagramBaseAction {

	public static final String ACTION_ID = "connectionRouterFanId"; //$NON-NLS-1$
	public static final ImageDescriptor img = 
		ImageDescriptor.createFromFile(DiagramViewer.class, "icons/fanConnectionRouter.png"); //$NON-NLS-1$

	public ConnectionRouterFanAction(DiagramViewer editor) {
		super(editor, AS_RADIO_BUTTON);
		setId(ACTION_ID);
		setText(DiagramViewerMessages.ConnectionRouterFanAction_select_fan_connection_router);
		setToolTipText(DiagramViewerMessages.ConnectionRouterFanAction_select_fan_connection_router);
		setImageDescriptor(img);
	}

	public void run() {
		DiagramEditPart diagramEditPart = getDiagramViewer().getDiagramEditPart();
		if (diagramEditPart != null) {
			diagramEditPart.setupFanConnectionRouter();
		}
	}

	public boolean isChecked() {
		DiagramEditPart diagramEditPart = getDiagramViewer().getDiagramEditPart();
		if (diagramEditPart != null) {
			return diagramEditPart.isFanConnectionRouter();
		}
		return super.isChecked();
	}
}
