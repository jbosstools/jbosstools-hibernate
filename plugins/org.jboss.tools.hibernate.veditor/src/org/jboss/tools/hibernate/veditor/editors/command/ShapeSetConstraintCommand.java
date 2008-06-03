/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.veditor.editors.command;


import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.jboss.tools.hibernate.veditor.editors.model.OrmShape;


public class ShapeSetConstraintCommand extends Command {
	private final Point newLocation;
	private Point oldLocation;
	private final ChangeBoundsRequest request;
	
	private final OrmShape shape;
	
	public ShapeSetConstraintCommand(OrmShape shape, ChangeBoundsRequest req, 
			Point newLocation) {
		if (shape == null || req == null || newLocation == null) {
			throw new IllegalArgumentException();
		}
		this.shape = shape;
		this.request = req;
		this.newLocation = newLocation.getCopy();
		setLabel("move");
	}
	
	public boolean canExecute() {
		Object type = request.getType();
		return (RequestConstants.REQ_MOVE.equals(type)
				|| RequestConstants.REQ_MOVE_CHILDREN.equals(type)); 
	}
	
	public void execute() {
		oldLocation = shape.getLocation();
		redo();
	}
	
	public void redo() {
		shape.setLocation(newLocation);
	}
	
	public void undo() {
		shape.setLocation(oldLocation);
	}
}
