/*
 * Created on 2004-11-01 by max
 * 
 */
package org.hibernate.eclipse.console.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionGroup;
import org.hibernate.eclipse.console.actions.CloseQueryPageAction;

/**
 * @author max
 *
 */
public class QueryPageTabViewActionGroup extends ActionGroup {

	Action closeAction;
	
	public QueryPageTabViewActionGroup(IViewPart part, ISelectionProvider provider) {

		closeAction = new CloseQueryPageAction(provider);
		
	}
	
	public void fillActionBars(IActionBars actionBars) {
		actionBars.getToolBarManager().add(closeAction);
	}
}
