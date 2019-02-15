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
import org.hibernate.eclipse.graph.model.AssociationViewAdapter;
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
	
	protected List<AssociationViewAdapter> getModelSourceConnections() {
		return getGraphNode().getSourceAssociations();		
	}
	
	public GraphNode getGraphNode() {
		return (GraphNode)getModel();
	}

	protected List<AssociationViewAdapter> getModelTargetConnections() {
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
