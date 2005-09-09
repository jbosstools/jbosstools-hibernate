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
import org.hibernate.eclipse.graph.model.PersistentClassViewAdapter;
import org.hibernate.eclipse.graph.parts.GraphNodeEditPart;
import org.hibernate.eclipse.graph.parts.PersistentClassEditPart;

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
