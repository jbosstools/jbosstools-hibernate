/*
 * Created on 2004-10-29 by max
 * 
 */
package org.hibernate.eclipse.console.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.actions.SelectionListenerAction;

/**
 * @author max
 *
 */
public class RefreshAction extends SelectionListenerAction {

	private final StructuredViewer viewer;

	public RefreshAction(StructuredViewer viewer) {
		super("Refresh");
		this.viewer = viewer;
	}
	
	public void run() {
		List selectedNonResources = getSelectedNonResources();
		
		Iterator iter = selectedNonResources.iterator();
		while (iter.hasNext() ) {
			Object element = iter.next();
			viewer.refresh(element);	
		}
		
		
	}

}
