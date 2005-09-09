package org.hibernate.eclipse.graph.policy;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.hibernate.eclipse.graph.figures.EditableLabel;
import org.hibernate.eclipse.graph.parts.PropertyEditPart;

public class PropertySelectionPolicy extends NonResizableEditPolicy {

	private EditableLabel getLabel() {
		PropertyEditPart part = (PropertyEditPart)getHost();
		return ((EditableLabel)part.getFigure());
	}	

	/**
	 * @see org.eclipse.gef.editpolicies.NonResizableEditPolicy#hideFocus()
	 */
	protected void hideFocus() {
		super.hideFocus();
	}

	/**
	 * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#hideSelection()
	 */
	protected void hideSelection() {
		getLabel().setSelected(false);
		//getLabel().setFocus(false);		
	}

	/**
	 * @see org.eclipse.gef.editpolicies.NonResizableEditPolicy#showFocus()
	 */
	protected void showFocus() {
	//	getLabel().setFocus(true);
		super.showFocus();
	}

	/**
	 * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#showSelection()
	 */
	protected void showPrimarySelection() {
		getLabel().setSelected(true);
		//getLabel().setFocus(true);
	}

	/**
	 * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#showSelection()
	 */
	protected void showSelection() {
		getLabel().setSelected(true);
//		getLabel().setFocus(false);
	}
}
