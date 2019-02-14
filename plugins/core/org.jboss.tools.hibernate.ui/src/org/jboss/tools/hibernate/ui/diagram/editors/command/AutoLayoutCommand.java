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

import java.util.HashMap;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;

/**
 * 
 * @author Vitali Yemialyanchyk
 */
public class AutoLayoutCommand extends Command {
	
	protected HashMap<String, Point> elLocations;
	protected OrmDiagram ormDiagram;
	
	public AutoLayoutCommand(OrmDiagram ormDiagram) {
		this.ormDiagram = ormDiagram;
		elLocations = null;
	}
	
	public void execute() {
		elLocations = ormDiagram.getElementsLocations();
		ormDiagram.autolayout();
	}

	public void undo() {
		if (elLocations != null) {
			ormDiagram.setElementsLocations(elLocations);
		}
	}

	public boolean canUndo() {
		return (elLocations != null);
	}
}
