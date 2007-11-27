package org.jboss.tools.hibernate.ui.veditor.editors.popup;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.jboss.tools.hibernate.ui.veditor.editors.actions.OpenMappingAction;
import org.jboss.tools.hibernate.ui.veditor.editors.actions.OpenSourceAction;

public class PopupMenuProvider extends ContextMenuProvider {
	private ActionRegistry actionRegistry;

	public PopupMenuProvider(EditPartViewer viewer, ActionRegistry actionRegistry) {
		super(viewer);
		this.actionRegistry = actionRegistry;
	}

	public void buildContextMenu(IMenuManager menu) {
		menu.add(new Separator(GEFActionConstants.MB_ADDITIONS));

		IAction action = getActionRegistry().getAction(OpenSourceAction.ACTION_ID);
		appendToGroup(GEFActionConstants.MB_ADDITIONS, action);
		createMenuItem(getMenu(), action);

		action = getActionRegistry().getAction(OpenMappingAction.ACTION_ID);
		appendToGroup(GEFActionConstants.MB_ADDITIONS, action);
		createMenuItem(getMenu(), action);
	}

	public void createMenuItem(Menu menu, IAction action) {
		boolean enabled = action.isEnabled();
		boolean hidden = false;
		if (hidden)
			return;
		MenuItem item = new MenuItem(menu, SWT.CASCADE);
		String displayName = action.getText();
		item.addSelectionListener(new AL(action));
		item.setText(displayName);
		item.setEnabled(enabled);
	}

	class AL implements SelectionListener {
		IAction action;

		public AL(IAction action) {
			this.action = action;
		}

		public void widgetSelected(SelectionEvent e) {
			action.run();
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	}

	public ActionRegistry getActionRegistry() {
		return actionRegistry;
	}
}
