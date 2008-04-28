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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MidpointLocator;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.hibernate.eclipse.graph.model.AssociationViewAdapter;

public class AssociationEditPart extends AbstractConnectionEditPart {

	public AssociationEditPart(AssociationViewAdapter adapter) {
		setModel(adapter);
	}

	protected void createEditPolicies() {
		// TODO Auto-generated method stub
		
	}
	
	protected IFigure createFigure() {
		PolylineConnection polylineConnection = new PolylineConnection();
		
		AssociationViewAdapter association = (AssociationViewAdapter) getModel();
		polylineConnection.setTargetDecoration(association.getTargetDecoration());
		ConnectionRouter connectionRouter = association.getConnectionRouter();
		if(connectionRouter!=null) {
			polylineConnection.setConnectionRouter(connectionRouter);	
		}
		
		AssociationViewAdapter ava = (AssociationViewAdapter) getModel();
		if(ava.getAssociationName()!=null) {
			MidpointLocator mpl = new MidpointLocator(polylineConnection, 0);
			polylineConnection.add(new Label(ava.getAssociationName()), mpl);	
		}
		
		polylineConnection.setForegroundColor(ColorConstants.gray);
		
		return polylineConnection;
	}
	
}
