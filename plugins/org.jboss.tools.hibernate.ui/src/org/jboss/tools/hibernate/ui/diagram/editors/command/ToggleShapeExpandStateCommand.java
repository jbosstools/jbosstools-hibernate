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

import java.util.List;

import org.eclipse.gef.commands.Command;
import org.jboss.tools.hibernate.ui.diagram.editors.model.ExpandableShape;

/**
 * 
 * @author Vitali Yemialyanchyk
 */
public class ToggleShapeExpandStateCommand extends Command {
	
	protected List<ExpandableShape> selectedShape;
	
	public ToggleShapeExpandStateCommand(List<ExpandableShape> selectedShape) {
		this.selectedShape = selectedShape;
	}
	
	public void execute() {
		for (ExpandableShape shape : selectedShape) {
			shape.setExpanded(!shape.isExpanded());
		}
	}

	public void undo() {
		for (ExpandableShape shape : selectedShape) {
			shape.setExpanded(!shape.isExpanded());
		}
	}

	public boolean canUndo() {
		return (selectedShape != null);
	}
}
