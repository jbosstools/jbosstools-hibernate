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

import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.actions.PrintAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionGroup;
import org.hibernate.eclipse.graph.actions.ToggleLayoutAction;

public class GraphViewActionGroup extends ActionGroup {

	Action toggleLayoutAction;
	private ZoomInAction zoomIn;
	private ZoomOutAction zoomOut;
	private PrintAction printAction;
	
	public GraphViewActionGroup(AbstractGraphViewPart uv, String layoutkey, ScalableFreeformRootEditPart root) {
		toggleLayoutAction = new ToggleLayoutAction(uv, layoutkey);
		
		zoomIn = new ZoomInAction(root.getZoomManager());
	    zoomOut = new ZoomOutAction(root.getZoomManager());
	    
	    printAction = new PrintAction(uv);
	}
	
	public void fillActionBars(IActionBars actionBars) {
		actionBars.getToolBarManager().add(toggleLayoutAction);
		
		actionBars.getToolBarManager().add(zoomIn);
	    actionBars.getToolBarManager().add(zoomOut);
	    
	    actionBars.setGlobalActionHandler(ActionFactory.PRINT.getId(), printAction);
	}

}
