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
public class ToggleClassMappingCommand extends Command {

	protected boolean stateConnectionsVisibilityClassMapping; 
	protected DiagramViewer diagramViewer;
	
	public ToggleClassMappingCommand(DiagramViewer diagramViewer) {
		this.diagramViewer = diagramViewer;
		stateConnectionsVisibilityClassMapping = 
			diagramViewer.getConnectionsVisibilityClassMapping();
	}
	
	public void execute() {
		stateConnectionsVisibilityClassMapping = 
			diagramViewer.getConnectionsVisibilityClassMapping();
		diagramViewer.setConnectionsVisibilityClassMapping(
			!stateConnectionsVisibilityClassMapping);
	}

	public void undo() {
		diagramViewer.setConnectionsVisibilityClassMapping(
			stateConnectionsVisibilityClassMapping);
	}

	public boolean canUndo() {
		return (diagramViewer != null);
	}
}
