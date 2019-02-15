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
package org.hibernate.eclipse.graph.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.eclipse.draw2d.geometry.Rectangle;

/**
 * base class for model elements in our views that are node-like and have a movable physical bound.
 *  
 * @author Max Rydahl Andersen
 *
 */
public abstract class GraphNode extends Observable {

	public static final String ASSOCIATONS = "ASSOCIATIONS"; //$NON-NLS-1$
	private Rectangle bounds = new Rectangle( 0, 0, -1, -1 );
	protected List<AssociationViewAdapter> targetAssociations;
	protected List<AssociationViewAdapter> sourceAssociations;

	public GraphNode() {
		targetAssociations = new ArrayList<AssociationViewAdapter>();
		sourceAssociations = null; //lazily created
	}
	
	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		Rectangle oldBounds = this.bounds;
		if ( !bounds.equals( oldBounds ) ) {
			this.bounds = bounds;
			setChanged();
			notifyObservers();
		}
	}
	
	abstract public void createAssociations();

	public List<AssociationViewAdapter> getSourceAssociations() {
		checkAssociations();
		return sourceAssociations;
	}

	private void checkAssociations() {
		if(sourceAssociations==null) {
			sourceAssociations=new ArrayList<AssociationViewAdapter>();
			createAssociations();
		}		
	}

	protected void addTargetAssociation(AssociationViewAdapter iva) {
		targetAssociations.add(iva);
		setChanged();
		notifyObservers(ASSOCIATONS);
	}

	protected void addSourceAssociation(AssociationViewAdapter iva) {
		checkAssociations();
		sourceAssociations.add(iva);
		setChanged();
		notifyObservers(ASSOCIATONS);
	}

	public List<AssociationViewAdapter> getTargetAssociations() {
		return targetAssociations;
	}

}
