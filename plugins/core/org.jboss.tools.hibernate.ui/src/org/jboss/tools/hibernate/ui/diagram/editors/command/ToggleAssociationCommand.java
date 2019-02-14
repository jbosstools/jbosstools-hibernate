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
package org.jboss.tools.hibernate.ui.diagram.editors.command;

import org.eclipse.gef.commands.Command;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;

/**
 * 
 * @author Vitali Yemialyanchyk
 */
public class ToggleAssociationCommand extends Command {
	
	protected boolean stateConnectionsVisibilityAssociation; 
	protected DiagramViewer diagramViewer;
	
	public ToggleAssociationCommand(DiagramViewer diagramViewer) {
		this.diagramViewer = diagramViewer;
		stateConnectionsVisibilityAssociation = 
			diagramViewer.getConnectionsVisibilityAssociation();
	}
	
	public void execute() {
		stateConnectionsVisibilityAssociation = 
			diagramViewer.getConnectionsVisibilityAssociation();
		diagramViewer.setConnectionsVisibilityAssociation(
			!stateConnectionsVisibilityAssociation);
	}

	public void undo() {
		diagramViewer.setConnectionsVisibilityAssociation(
			stateConnectionsVisibilityAssociation);
	}

	public boolean canUndo() {
		return (diagramViewer != null);
	}
}
