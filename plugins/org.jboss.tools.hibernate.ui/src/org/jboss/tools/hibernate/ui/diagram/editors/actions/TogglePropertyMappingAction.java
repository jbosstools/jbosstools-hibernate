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
 * Show|Hide connections which type is "Property Mappings" (property->column).
 * 
 * @author Vitali Yemialyanchyk
 */
public class TogglePropertyMappingAction extends DiagramBaseAction {

	public static final String ACTION_ID = "togglePropertyMappingId"; //$NON-NLS-1$
	public static final ImageDescriptor img = 
		ImageDescriptor.createFromFile(DiagramViewer.class, "icons/togglepropertymapping.png"); //$NON-NLS-1$

	public TogglePropertyMappingAction(DiagramViewer editor) {
		super(editor);
		setId(ACTION_ID);
		setText(DiagramViewerMessages.TogglePropertyMappingAction_property_mappings);
		setToolTipText(DiagramViewerMessages.TogglePropertyMappingAction_property_mappings);
		setImageDescriptor(img);
		boolean state = getDiagramViewer().getConnectionsVisibilityPropertyMapping();
		setChecked(state);
	}

	public void run() {
		boolean state = getDiagramViewer().getConnectionsVisibilityPropertyMapping();
		getDiagramViewer().setConnectionsVisibilityPropertyMapping(!state);
	}
}
