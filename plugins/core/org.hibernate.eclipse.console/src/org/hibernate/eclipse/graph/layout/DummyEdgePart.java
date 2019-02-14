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
package org.hibernate.eclipse.graph.layout;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;


/**
 * Used to represent a dummy edge node with NodeJoiningDirectedGraphLayout
 * @author Phil Zoio
 */
public class DummyEdgePart
{
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies()
	{
	}

	
	/**
	 * @see org.eclipse.gef.editparts.AbstractConnectionEditPart#createFigure()
	 */
	protected IFigure createFigure()
	{
		PolylineConnection conn = new PolylineConnection();
		conn.setConnectionRouter(new BendpointConnectionRouter());conn.setVisible(false);
		return conn;
	}
	
}
