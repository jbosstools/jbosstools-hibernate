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
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;
import org.jboss.tools.hibernate.ui.diagram.editors.command.ToggleAssociationCommand;

/**
 * Show|Hide connections which type is "Associations" (class associations).
 * 
 * @author Vitali Yemialyanchyk
 */
public class ToggleAssociationAction extends DiagramBaseAction {

	public static final String ACTION_ID = "toggleAssociationId"; //$NON-NLS-1$
	public static final ImageDescriptor img = 
		ImageDescriptor.createFromFile(DiagramViewer.class, "icons/toggleassociation.png"); //$NON-NLS-1$

	public ToggleAssociationAction(DiagramViewer editor) {
		super(editor);
		setId(ACTION_ID);
		setText(DiagramViewerMessages.ToggleAssociationAction_associations);
		setToolTipText(DiagramViewerMessages.ToggleAssociationAction_associations);
		setImageDescriptor(img);
		boolean state = getDiagramViewer().getConnectionsVisibilityAssociation();
		setChecked(state);
	}

	public void run() {
		execute(getCommand());
	}

	public Command getCommand() {
		CompoundCommand cc = new CompoundCommand();
		cc.add(new ToggleAssociationCommand(getDiagramViewer()));
		return cc;
	}
}
