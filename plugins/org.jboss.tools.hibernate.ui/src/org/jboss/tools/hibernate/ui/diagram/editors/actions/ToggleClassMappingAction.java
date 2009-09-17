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
 * Show|Hide connections which type is "Class Mappings" (class->table).
 * 
 * @author Vitali Yemialyanchyk
 */
public class ToggleClassMappingAction extends DiagramBaseAction {

	public static final String ACTION_ID = "toggleClassMappingId"; //$NON-NLS-1$
	public static final ImageDescriptor img = 
		ImageDescriptor.createFromFile(DiagramViewer.class, "icons/toggleclassmapping.png"); //$NON-NLS-1$

	public ToggleClassMappingAction(DiagramViewer editor) {
		super(editor);
		setId(ACTION_ID);
		setText(DiagramViewerMessages.ToggleClassMappingAction_class_mappings);
		setToolTipText(DiagramViewerMessages.ToggleClassMappingAction_class_mappings);
		setImageDescriptor(img);
		boolean state = getDiagramViewer().getConnectionsVisibilityClassMapping();
		setChecked(state);
	}

	public void run() {
		boolean state = getDiagramViewer().getConnectionsVisibilityClassMapping();
		getDiagramViewer().setConnectionsVisibilityClassMapping(!state);
	}
}
