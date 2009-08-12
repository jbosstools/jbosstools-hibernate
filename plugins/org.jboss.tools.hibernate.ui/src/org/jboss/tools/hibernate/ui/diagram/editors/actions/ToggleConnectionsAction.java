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

/**
 * @author Vitali Yemialyanchyk
 */
public class ToggleConnectionsAction extends DiagramBaseAction {

	public static final String ACTION_ID = "toggleConnectionsId"; //$NON-NLS-1$
	public static final ImageDescriptor img = 
		ImageDescriptor.createFromFile(DiagramViewer.class, "icons/toggleconnections.png"); //$NON-NLS-1$

	public ToggleConnectionsAction(DiagramViewer editor) {
		super(editor);
		setId(ACTION_ID);
		setText(DiagramViewerMessages.ToggleConnectionsAction_toggle_connections);
		setToolTipText(DiagramViewerMessages.ToggleConnectionsAction_toggle_connections);
		setImageDescriptor(img);
	}

	public void run() {
		getDiagramViewer().getViewerContents().toggleConnections();
	}
}
