/*
 * Created on 2004-11-01 by max
 * 
 */
package org.hibernate.eclipse.console.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.node.BaseNode;

/**
 * @author max
 *
 */
public class DeleteConfigurationAction extends SelectionListenerAction {
	
	public DeleteConfigurationAction() {
		super("Delete");
		setEnabled(false);
	}

	public void run() {
		List selectedNonResources = getSelectedNonResources();
		
		Iterator iter = selectedNonResources.iterator();
		while (iter.hasNext()) {
			BaseNode element = (BaseNode) iter.next();
			KnownConfigurations.getInstance().removeConfiguration(element.getConsoleConfiguration());
		}
	}	
	
	protected boolean updateSelection(IStructuredSelection selection) {
		if(!selection.isEmpty()) {
			Iterator iter = getSelectedNonResources().iterator();
			while (iter.hasNext()) {
				Object element = iter.next();
				if(element instanceof BaseNode) {
					return true;
				}
			}
		}
		return false;
	}
}
