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
public class ToggleConnectionRouterFanCommand extends Command {

	protected boolean stateConnectionRouterFan; 
	protected DiagramViewer diagramViewer;
	
	public ToggleConnectionRouterFanCommand(DiagramViewer diagramViewer) {
		this.diagramViewer = diagramViewer;
		stateConnectionRouterFan = diagramViewer.isFanConnectionRouter();
	}
	
	public void execute() {
		stateConnectionRouterFan = diagramViewer.isFanConnectionRouter();
		diagramViewer.setFanConnectionRouter(true);
	}

	public void undo() {
		diagramViewer.setFanConnectionRouter(stateConnectionRouterFan);
	}

	public boolean canUndo() {
		return (diagramViewer != null);
	}
}
