/*
 * Created on 2004-11-01 by max
 * 
 */
package org.hibernate.eclipse.console.actions;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.QueryPage;
import org.hibernate.eclipse.console.utils.EclipseImages;

/**
 * @author max
 *
 */
public class CloseQueryPageAction extends SelectionListenerAction {

	private final ISelectionProvider selectionProvider;
	
	/**
	 * @param text
	 */
	public CloseQueryPageAction(ISelectionProvider selectionProvider) {
		super("");
		this.selectionProvider = selectionProvider;
		this.selectionProvider.addSelectionChangedListener(this);
		setEnabled(!this.selectionProvider.getSelection().isEmpty());
		
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.CLOSE));
		setDisabledImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.CLOSE_DISABLED));
		
		setToolTipText("Close query page");
	}

	public void run() {
		IStructuredSelection selection = 
			(IStructuredSelection) this.selectionProvider.getSelection();
		if (!selection.isEmpty()) {
			for (Iterator i = selection.iterator(); i.hasNext(); ) {
				KnownConfigurations.getInstance().getQueryPageModel().remove((QueryPage) i.next());
			}
		}
	}

	public boolean updateSelection(IStructuredSelection selection) {
        return !selection.isEmpty();
	}

}
