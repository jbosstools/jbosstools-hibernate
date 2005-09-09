package org.hibernate.eclipse.graph.parts;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.hibernate.eclipse.graph.model.GraphNode;

public abstract class GraphNodeEditPart extends AbstractGraphicalEditPart implements Observer, NodeEditPart {

	abstract protected IFigure createFigure();

	abstract protected void createEditPolicies();
	
	public void update(Observable o, Object arg) {
		if(arg==GraphNode.ASSOCIATONS) {
			refreshSourceConnections(); 
			refreshTargetConnections();
		} else {
			refreshVisuals();
		}
	}
	
	protected List getModelSourceConnections() {
		return getGraphNode().getSourceAssociations();		
	}
	
	public GraphNode getGraphNode() {
		return (GraphNode)getModel();
	}

	protected List getModelTargetConnections() {
		return getGraphNode().getTargetAssociations();
	}

	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}
	
	public void activate() {
		super.activate();
		Observable o = (Observable) getModel();
		o.addObserver(this);
		refreshSourceConnections(); // to be in sync with the models connections
		refreshTargetConnections(); // cannot just call refresh as that makes the connections available twice
	}
	
	public void deactivate() {
		super.deactivate();
		Observable o = (Observable) getModel();
		o.deleteObserver(this);
	}

	protected void refreshVisuals() {
		GraphicalEditPart parent = (GraphicalEditPart) getParent();
		Rectangle bounds = getGraphNode().getBounds().getCopy();
		parent.setLayoutConstraint( this, getFigure(), bounds );
	}
}
