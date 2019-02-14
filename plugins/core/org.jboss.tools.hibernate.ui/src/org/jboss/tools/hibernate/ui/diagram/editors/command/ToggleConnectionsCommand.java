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
public class ToggleConnectionsCommand extends Command {

	protected boolean stateConnectionsVisibilityAssociation; 
	protected boolean stateConnectionsVisibilityClassMapping; 
	protected boolean stateConnectionsVisibilityForeignKeyConstraint; 
	protected boolean stateConnectionsVisibilityPropertyMapping; 
	protected DiagramViewer diagramViewer;
	
	public ToggleConnectionsCommand(DiagramViewer diagramViewer) {
		this.diagramViewer = diagramViewer;
		stateConnectionsVisibilityAssociation = 
			diagramViewer.getConnectionsVisibilityAssociation();
		stateConnectionsVisibilityClassMapping = 
			diagramViewer.getConnectionsVisibilityClassMapping();
		stateConnectionsVisibilityForeignKeyConstraint = 
			diagramViewer.getConnectionsVisibilityForeignKeyConstraint();
		stateConnectionsVisibilityPropertyMapping = 
			diagramViewer.getConnectionsVisibilityPropertyMapping();
	}
	
	public void execute() {
		stateConnectionsVisibilityAssociation = 
			diagramViewer.getConnectionsVisibilityAssociation();
		diagramViewer.setConnectionsVisibilityAssociation(
			!stateConnectionsVisibilityAssociation);
		stateConnectionsVisibilityClassMapping = 
			diagramViewer.getConnectionsVisibilityClassMapping();
		diagramViewer.setConnectionsVisibilityClassMapping(
			!stateConnectionsVisibilityClassMapping);
		stateConnectionsVisibilityForeignKeyConstraint = 
			diagramViewer.getConnectionsVisibilityForeignKeyConstraint();
		diagramViewer.setConnectionsVisibilityForeignKeyConstraint(
			!stateConnectionsVisibilityForeignKeyConstraint);
		stateConnectionsVisibilityPropertyMapping = 
			diagramViewer.getConnectionsVisibilityPropertyMapping();
		diagramViewer.setConnectionsVisibilityPropertyMapping(
			!stateConnectionsVisibilityPropertyMapping);
	}

	public void undo() {
		diagramViewer.setConnectionsVisibilityAssociation(
			stateConnectionsVisibilityAssociation);
		diagramViewer.setConnectionsVisibilityClassMapping(
			stateConnectionsVisibilityClassMapping);
		diagramViewer.setConnectionsVisibilityForeignKeyConstraint(
			stateConnectionsVisibilityForeignKeyConstraint);
		diagramViewer.setConnectionsVisibilityPropertyMapping(
			stateConnectionsVisibilityPropertyMapping);
	}

	public boolean canUndo() {
		return (diagramViewer != null);
	}
}
