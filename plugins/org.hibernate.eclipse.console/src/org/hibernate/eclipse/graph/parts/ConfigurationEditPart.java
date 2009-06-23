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

import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.hibernate.eclipse.graph.layout.DelegatingLayoutManager;
import org.hibernate.eclipse.graph.layout.GraphXYLayout;
import org.hibernate.eclipse.graph.model.ConfigurationViewAdapter;
import org.hibernate.eclipse.graph.model.GraphNode;
import org.hibernate.eclipse.graph.policy.ConfigurationLayoutEditPolicy;

public class ConfigurationEditPart extends AbstractGraphicalEditPart implements Observer {

	private DelegatingLayoutManager delegatingLayoutManager;
	private boolean manualLayoutActive = true;

	public void activate() {
		super.activate();
		getConfigurationViewAdapter().addObserver(this);
	}
	
	public void deactivate() {
		super.deactivate();
		getConfigurationViewAdapter().deleteObserver(this);
	}
	
	public ConfigurationEditPart(ConfigurationViewAdapter configuration) {
		setModel( configuration );
	}

	protected List<?> getModelChildren() {
		if ( getConfigurationViewAdapter() == null )
			return super.getModelChildren();
		return getConfigurationViewAdapter().getPersistentClasses();
	}

	protected IFigure createFigure() {
		FreeformLayer layer = new FreeformLayer();
		delegatingLayoutManager = new DelegatingLayoutManager(this);
		layer.setLayoutManager( delegatingLayoutManager );
		layer.setBorder( new LineBorder( 1 ) );
		
		//		 Create the static router for the connection layer
		//ConnectionLayer connLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
		//connLayer.setConnectionRouter(new ManhattanConnectionRouter());
		return layer;
	}

	public ConfigurationViewAdapter getConfigurationViewAdapter() {
		return (ConfigurationViewAdapter) getModel();
	}

	protected void createEditPolicies() {
		installEditPolicy( EditPolicy.LAYOUT_ROLE,	new ConfigurationLayoutEditPolicy() );
	}

	/**
	 * Updates the bounds of the table figure (without invoking any event
	 * handling), and sets layout constraint data
	 * 
	 * @return whether the procedure execute successfully without any
	 *         omissions. The latter occurs if any Table objects have no
	 *         bounds set or if no figure is available for the TablePart
	 */
	public boolean resetFigureBounds(boolean updateConstraint)
	{
		List tableParts = getChildren();
		
		for (Iterator iter = tableParts.iterator(); iter.hasNext();)
		{
			GraphicalEditPart nodePart = (GraphicalEditPart) iter.next();
			
			// now check whether we can find an entry in the tableToNodesMap
			Rectangle bounds = nodePart.getFigure().getBounds();
			if (bounds == null)
			{
				// TODO handle this better
				return false;
			}
			else
			{
				Figure tableFigure = (Figure) nodePart.getFigure();
				if (tableFigure == null)
				{
					return false;
				}
				else
				{
					if (updateConstraint)
					{
						// pass the constraint information to the xy layout
						// setting the width and height so that the
						// preferred size will be applied
						delegatingLayoutManager.setXYLayoutConstraint(tableFigure, new Rectangle(bounds.x, bounds.y,
								-1, -1));
					}
				}
			}			
		}
		return true;
		
	}

	public boolean resetModelBounds(GraphXYLayout layout) {

			List tableParts = getChildren();
			
			for (Iterator iter = tableParts.iterator(); iter.hasNext();)
			{
				GraphNodeEditPart classPart = (GraphNodeEditPart) iter.next();
				IFigure persistentClassFigure = classPart.getFigure();

				//if we don't find a node for one of the children then we should
				// continue
				if (persistentClassFigure == null)
					continue;

				Rectangle bounds = persistentClassFigure.getBounds().getCopy();
				GraphNode node = classPart.getGraphNode();
				node.setBounds(bounds);
				if(layout!=null) {
					layout.setConstraint(persistentClassFigure, bounds);
				}
			}

			return true;
	}

	public void update(Observable o, Object arg) {
		getFigure().setLayoutManager(delegatingLayoutManager);
		refresh();
	}

	public boolean isManualLayoutActive() {
		return manualLayoutActive;
	}
	
	public void setManualLayoutActive(boolean manualLayoutActive) {
		this.manualLayoutActive = manualLayoutActive;
		getFigure().setLayoutManager(delegatingLayoutManager);
		refresh();
	}
	
}