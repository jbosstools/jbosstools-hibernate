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
