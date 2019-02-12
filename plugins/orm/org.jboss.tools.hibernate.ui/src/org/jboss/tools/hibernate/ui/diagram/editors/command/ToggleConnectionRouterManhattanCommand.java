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
public class ToggleConnectionRouterManhattanCommand extends Command {

	protected boolean stateConnectionRouterManhattan; 
	protected DiagramViewer diagramViewer;
	
	public ToggleConnectionRouterManhattanCommand(DiagramViewer diagramViewer) {
		this.diagramViewer = diagramViewer;
		stateConnectionRouterManhattan = diagramViewer.isManhattanConnectionRouter();
	}
	
	public void execute() {
		stateConnectionRouterManhattan = diagramViewer.isManhattanConnectionRouter();
		diagramViewer.setManhattanConnectionRouter(true);
	}

	public void undo() {
		diagramViewer.setManhattanConnectionRouter(stateConnectionRouterManhattan);
	}

	public boolean canUndo() {
		return (diagramViewer != null);
	}
}
