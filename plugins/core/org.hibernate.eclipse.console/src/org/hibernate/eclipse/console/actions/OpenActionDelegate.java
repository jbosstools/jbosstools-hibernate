package org.hibernate.eclipse.console.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public abstract class OpenActionDelegate implements IObjectActionDelegate {
	private IWorkbenchPart part;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.part = targetPart;
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public IWorkbenchPart getPart() {
		return part;
	}
}