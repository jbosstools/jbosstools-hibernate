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

import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.hibernate.eclipse.graph.parts.ConfigurationEditPart;

/**
 * Subclass of XYLayout which can use the child figures actual bounds as a
 * constraint when doing manual layout (XYLayout)
 * 
 * @author Phil Zoio
 */
public class GraphXYLayout extends FreeformLayout {

	private ConfigurationEditPart diagram;

	public GraphXYLayout(ConfigurationEditPart diagram) {
		this.diagram = diagram;
	}

	public void layout(IFigure container) {		
		diagram.resetModelBounds(this);
		super.layout( container );				
	}

	public Object getConstraint(IFigure child) {
		Object constraint = constraints.get( child );
		if (constraint instanceof Rectangle) {
			return (Rectangle) constraint;
		}
		else {
			Rectangle currentBounds = child.getBounds();
			return new Rectangle( currentBounds.x, currentBounds.y, -1, -1 );
		}
	}

	public void setConstraint(IFigure figure, Object newConstraint) {
		if(newConstraint!=null && newConstraint instanceof Rectangle) {
			figure.setBounds((Rectangle) newConstraint);
		}
		super.setConstraint( figure, newConstraint );
	}
}
