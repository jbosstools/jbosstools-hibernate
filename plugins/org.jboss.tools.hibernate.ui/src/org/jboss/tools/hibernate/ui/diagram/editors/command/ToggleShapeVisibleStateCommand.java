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
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmShape;

/**
 * 
 * @author Vitali Yemialyanchyk
 */
public class ToggleShapeVisibleStateCommand extends Command {
	
	protected List<OrmShape> selectedShape;
	
	public ToggleShapeVisibleStateCommand(List<OrmShape> selectedShape) {
		this.selectedShape = selectedShape;
	}
	
	public void execute() {
		for (OrmShape shape : selectedShape) {
			Object ormElement = shape.getOrmElement();
			if (ormElement instanceof PersistentClass || ormElement instanceof Table) {
				shape.setVisible(!shape.isVisible());
			}
		}
	}

	public void undo() {
		for (OrmShape shape : selectedShape) {
			Object ormElement = shape.getOrmElement();
			if (ormElement instanceof PersistentClass || ormElement instanceof Table) {
				shape.setVisible(!shape.isVisible());
			}
		}
	}

	public boolean canUndo() {
		return (selectedShape != null);
	}
}
