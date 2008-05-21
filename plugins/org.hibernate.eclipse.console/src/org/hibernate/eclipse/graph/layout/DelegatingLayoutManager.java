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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.hibernate.eclipse.graph.parts.ConfigurationEditPart;
import org.hibernate.eclipse.graph.policy.ConfigurationLayoutEditPolicy;

/**
 * Used to delegate between the GraphyLayoutManager and the GraphXYLayout
 * classes
 * 
 * @author Phil Zoio
 */
public class DelegatingLayoutManager implements LayoutManager {

	private ConfigurationEditPart diagram;

	private LayoutManager activeLayoutManager;

	private GraphLayoutManager graphLayoutManager;

	private GraphXYLayout xyLayoutManager;
	
	public DelegatingLayoutManager(ConfigurationEditPart diagram) {
		this.diagram = diagram;
		this.graphLayoutManager = new GraphLayoutManager( diagram );
		this.xyLayoutManager = new GraphXYLayout( diagram );

		if(diagram.isManualLayoutActive()) {
			this.activeLayoutManager = xyLayoutManager;
		} else {
			this.activeLayoutManager = this.graphLayoutManager;			
		}

	}

	// ********************* layout manager methods methods
	// ****************************/

	public void layout(IFigure container) {

		if ( diagram.isManualLayoutActive() ) {
			// TODO: copy constraints on to xylayoutmanager constraints.
			
				setLayoutManager( container, xyLayoutManager );
				activeLayoutManager.layout( container );
		}
		else {
			setLayoutManager( container, graphLayoutManager );
			activeLayoutManager.layout( container );
		}

	}

	public Object getConstraint(IFigure child) {
		return activeLayoutManager.getConstraint( child );
	}

	public Dimension getMinimumSize(IFigure container, int wHint, int hHint) {
		return activeLayoutManager.getMinimumSize( container, wHint, hHint );
	}

	public Dimension getPreferredSize(IFigure container, int wHint, int hHint) {
		return activeLayoutManager.getPreferredSize( container, wHint, hHint );
	}

	public void invalidate() {
		activeLayoutManager.invalidate();
	}

	public void remove(IFigure child) {
		activeLayoutManager.remove( child );
	}

	public void setConstraint(IFigure child, Object constraint) {
		activeLayoutManager.setConstraint( child, constraint );
	}

	public void setXYLayoutConstraint(IFigure child, Rectangle constraint) {
		xyLayoutManager.setConstraint( child, constraint );
	}

	// ********************* protected and private methods
	// ****************************/

	/**
	 * Sets the current active layout manager
	 */
	private void setLayoutManager(IFigure container, LayoutManager layoutManager) {
		container.setLayoutManager( layoutManager );
		this.activeLayoutManager = layoutManager;
		if ( layoutManager == xyLayoutManager ) {
			diagram.installEditPolicy( EditPolicy.LAYOUT_ROLE,
					new ConfigurationLayoutEditPolicy() );
		}
		else {
			diagram.installEditPolicy( EditPolicy.LAYOUT_ROLE, null );
		}
	}

	public LayoutManager getActiveLayoutManager() {
		return activeLayoutManager;
	}
	
}