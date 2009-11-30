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
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmShape;

/**
 * 
 * @author Vitali Yemialyanchyk
 */
public class ToggleShapeVisibleStateCommand extends Command {
	
	protected OrmShape primalElement;
	protected List<OrmShape> selectedShape;
	protected List<Boolean> selectedShapeStates;
	
	public ToggleShapeVisibleStateCommand(List<OrmShape> selectedShape, OrmShape primalElement) {
		this.primalElement = primalElement;
		this.selectedShape = selectedShape;
		selectedShapeStates = new ArrayList<Boolean>();
		for (OrmShape shape : selectedShape) {
			selectedShapeStates.add(shape.isVisible());
		}
	}
	
	public void execute() {
		boolean visState = false;
		if (primalElement != null) {
			visState = primalElement.isVisible();
		} else if (selectedShape.size() > 0) {
			visState = selectedShape.get(0).isVisible();
		}
		for (OrmShape shape : selectedShape) {
			Object ormElement = shape.getOrmElement();
			if (ormElement instanceof PersistentClass || ormElement instanceof Table) {
				shape.setVisible(!visState);
			}
		}
	}

	public void undo() {
		for (int i = 0; i < selectedShape.size(); i++) {
			OrmShape shape = selectedShape.get(i);
			Object ormElement = shape.getOrmElement();
			if (ormElement instanceof PersistentClass || ormElement instanceof Table) {
				shape.setVisible(selectedShapeStates.get(i));
			}
		}
	}

	public boolean canUndo() {
		return (selectedShape != null);
	}
}
