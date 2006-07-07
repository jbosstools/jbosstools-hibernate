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

import java.util.List;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.hibernate.eclipse.graph.parts.ConfigurationEditPart;


/**
 * Uses the DirectedGraphLayoutVisitor to automatically lay out figures on diagram
 * Adapted from jdbc article. 
 */
public class GraphLayoutManager extends AbstractLayout
{

	private ConfigurationEditPart diagram;
	
	public GraphLayoutManager(ConfigurationEditPart diagram)
	{
		this.diagram = diagram;
	}

	
	protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint)
	{		
		container.validate();
		List children = container.getChildren();
		Rectangle result = new Rectangle().setLocation(container.getClientArea().getLocation());
		for (int i = 0; i < children.size(); i++)
			result.union(((IFigure) children.get(i)).getBounds());
		result.resize(container.getInsets().getWidth(), container.getInsets().getHeight());
		return result.getSize();		
	}

	
	public void layout(IFigure container)
	{

		GraphAnimation.recordInitialState(container);
		if (GraphAnimation.playbackState(container))
			return;
	
		new DirectedGraphLayoutVisitor().layoutDiagram(diagram);
		diagram.resetModelBounds(null);

	}
	
}