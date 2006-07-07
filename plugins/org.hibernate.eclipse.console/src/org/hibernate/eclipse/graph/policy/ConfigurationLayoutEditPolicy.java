/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.graph.policy;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.hibernate.eclipse.graph.command.MoveGraphNodeEditPartCommand;
import org.hibernate.eclipse.graph.model.GraphNode;
import org.hibernate.eclipse.graph.parts.GraphNodeEditPart;

public class ConfigurationLayoutEditPolicy extends XYLayoutEditPolicy {

	protected Command createAddCommand(EditPart child, Object constraint) {
		// TODO Auto-generated method stub
		return null;
	}

	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
		if(child instanceof GraphNodeEditPart) {
			GraphNodeEditPart classPart = (GraphNodeEditPart) child;
			GraphNode classView = classPart.getGraphNode();
			IFigure figure = classPart.getFigure();
			Rectangle oldBounds = figure.getBounds();
			Rectangle newBounds = (Rectangle) constraint;

			if (oldBounds.width != newBounds.width && newBounds.width != -1)
				return null;
			if (oldBounds.height != newBounds.height && newBounds.height != -1)
				return null;

			return new MoveGraphNodeEditPartCommand(classView, oldBounds, newBounds);
			//return new MoveEditPartCommand(classPart, oldBounds, newBounds);
		}
		return null;
	}

	protected Command getCreateCommand(CreateRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	protected Command getDeleteDependantCommand(Request request) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
