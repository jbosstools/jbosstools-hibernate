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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.jboss.tools.hibernate.ui.diagram.editors.model.ExpandableShape;

/**
 * 
 * @author Vitali Yemialyanchyk
 */
public class ToggleShapeExpandStateCommand extends Command {
	
	protected ExpandableShape primalElement;
	protected List<ExpandableShape> selectedShape;
	protected List<Boolean> selectedShapeStates;
	
	public ToggleShapeExpandStateCommand(List<ExpandableShape> selectedShape, ExpandableShape primalElement) {
		this.primalElement = primalElement;
		this.selectedShape = selectedShape;
		selectedShapeStates = new ArrayList<Boolean>();
		for (ExpandableShape shape : selectedShape) {
			selectedShapeStates.add(shape.isExpanded());
		}
	}
	
	public void execute() {
		boolean expState = false;
		if (primalElement != null) {
			expState = primalElement.isExpanded();
		} else if (selectedShape.size() > 0) {
			expState = selectedShape.get(0).isExpanded();
		}
		for (ExpandableShape shape : selectedShape) {
			shape.setExpanded(!expState);
		}
	}

	public void undo() {
		for (int i = 0; i < selectedShape.size(); i++) {
			ExpandableShape shape = selectedShape.get(i);
			shape.setExpanded(selectedShapeStates.get(i));
		}
	}

	public boolean canUndo() {
		return (selectedShape != null);
	}
}
