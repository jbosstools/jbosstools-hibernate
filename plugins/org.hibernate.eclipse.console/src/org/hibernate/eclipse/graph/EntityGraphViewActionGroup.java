package org.hibernate.eclipse.graph;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionGroup;
import org.hibernate.eclipse.graph.actions.ToggleLayoutAction;

public class EntityGraphViewActionGroup extends ActionGroup {

	Action toggleLayoutAction;
	
	public EntityGraphViewActionGroup(EntityGraphView uv) {

		toggleLayoutAction = new ToggleLayoutAction(uv);
		
	}
	
	public void fillActionBars(IActionBars actionBars) {
		actionBars.getToolBarManager().add(toggleLayoutAction);
	}

}
