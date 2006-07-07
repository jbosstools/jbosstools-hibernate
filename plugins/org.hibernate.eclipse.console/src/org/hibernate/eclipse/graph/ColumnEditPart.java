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
package org.hibernate.eclipse.graph;

import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.hibernate.eclipse.graph.anchor.LeftOrRightParentAnchor;
import org.hibernate.eclipse.graph.figures.EditableLabel;
import org.hibernate.eclipse.graph.model.ColumnViewAdapter;
import org.hibernate.eclipse.graph.model.GraphNode;
import org.hibernate.mapping.Column;

public class ColumnEditPart extends AbstractGraphicalEditPart implements NodeEditPart, Observer {
	
	public ColumnEditPart(ColumnViewAdapter column) {
		setModel(column);
	}

	public void activate() {
		((Observable)getModel()).addObserver(this);
		super.activate();
	}
	
	public void deactivate() {		
		super.deactivate();
		((Observable)getModel()).deleteObserver(this);
	}
	
	protected IFigure createFigure() {
		Column column = ((ColumnViewAdapter) getModel()).getcolumn();
		String label = column.getName();
		Label columnLabel = new EditableLabel(label);
		//columnLabel.setIcon(((ColumnViewAdapter)getModel()).getImage());
		return columnLabel;
	}

	protected void createEditPolicies() {
		
	}
	
	
	public Command getCommand(Request request) {
		// TODO Auto-generated method stub
		return super.getCommand( request );
	}
	
	public List getModelSourceConnections() {
	//	List sc = ((ColumnViewAdapter) getModel()).getSourceConnections();
		return Collections.EMPTY_LIST;
	}
	
	public List getModelTargetConnections() {
		//List tc = ((ColumnViewAdapter) getModel()).getTargetConnections();
		return Collections.EMPTY_LIST;
	}

	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return new LeftOrRightParentAnchor(getFigure());
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new LeftOrRightParentAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return new LeftOrRightParentAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new LeftOrRightParentAnchor(getFigure());
	}

	public void update(Observable o, Object arg) {
	 if(arg==GraphNode.ASSOCIATONS) {
		 refreshSourceConnections();
		 refreshTargetConnections();
	 }
		
	}

	public void setSelected(int value) {
		super.setSelected( value );
	}

}
