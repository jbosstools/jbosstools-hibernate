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
 * Show|Hide connections which type is "Foreign key constraints" 
 * (foreign keys which associations are based on).
 * 
 * @author Vitali Yemialyanchyk
 */
public class ToggleForeignKeyConstraintAction extends DiagramBaseAction {

	public static final String ACTION_ID = "toggleForeignKeyConstraintId"; //$NON-NLS-1$
	public static final ImageDescriptor img = 
		ImageDescriptor.createFromFile(DiagramViewer.class, "icons/toggleforeignkeyconstraint.png"); //$NON-NLS-1$

	public ToggleForeignKeyConstraintAction(DiagramViewer editor) {
		super(editor);
		setId(ACTION_ID);
		setText(DiagramViewerMessages.ToggleForeignKeyConstraintAction_foreign_key_constraints);
		setToolTipText(DiagramViewerMessages.ToggleForeignKeyConstraintAction_foreign_key_constraints);
		setImageDescriptor(img);
		boolean state = getDiagramViewer().getConnectionsVisibilityForeignKeyConstraint();
		setChecked(state);
	}

	public void run() {
		boolean state = getDiagramViewer().getConnectionsVisibilityForeignKeyConstraint();
		getDiagramViewer().setConnectionsVisibilityForeignKeyConstraint(!state);
	}
}
