/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.diagram.editors.popup;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.actions.ActionFactory;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.AutoLayoutAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ExportImageAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.OpenMappingAction;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.OpenSourceAction;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Shape;
import org.jboss.tools.hibernate.ui.diagram.editors.parts.OrmEditPart;

public class PopupMenuProvider extends ContextMenuProvider {
	private ActionRegistry actionRegistry;

	public PopupMenuProvider(EditPartViewer viewer, ActionRegistry actionRegistry) {
		super(viewer);
		this.actionRegistry = actionRegistry;
	}

	public void buildContextMenu(IMenuManager menu) {
		// Add standard action groups to the menu
		GEFActionConstants.addStandardActionGroups(menu);

		menu.add(new Separator(GEFActionConstants.MB_ADDITIONS));

		IAction action = null;

		if (getViewer().getSelection() instanceof StructuredSelection){
			IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
			if (selection != null &&  selection.getFirstElement() instanceof OrmEditPart) {
				Object obj = ((OrmEditPart)selection.getFirstElement()).getModel();
				if (null != obj && obj instanceof Shape) {
					Shape shape = (Shape)obj;
					Object first = shape.getOrmElement();
					if (first instanceof PersistentClass
							|| first.getClass() == Property.class
							|| first instanceof Table
							|| first instanceof Column){		
						action = getActionRegistry().getAction(OpenSourceAction.ACTION_ID);
						appendToGroup(GEFActionConstants.MB_ADDITIONS, action);
						createMenuItem(getMenu(), action);
						
						action = getActionRegistry().getAction(OpenMappingAction.ACTION_ID);
						appendToGroup(GEFActionConstants.MB_ADDITIONS, action);
						createMenuItem(getMenu(), action);					
					} 
				} 
			}			
		}
		
		action = getActionRegistry().getAction(AutoLayoutAction.ACTION_ID);
		appendToGroup(GEFActionConstants.GROUP_VIEW, action);
		createMenuItem(getMenu(), action);
		
		action = getActionRegistry().getAction(ExportImageAction.ACTION_ID);
		appendToGroup(GEFActionConstants.MB_ADDITIONS, action);
		createMenuItem(getMenu(), action);

		// Add actions to the menu
		menu.appendToGroup(
				GEFActionConstants.GROUP_UNDO, // target group id
				getAction(ActionFactory.UNDO.getId())); // action to add
		menu.appendToGroup(
				GEFActionConstants.GROUP_UNDO, 
				getAction(ActionFactory.REDO.getId()));
	}

	private IAction getAction(String actionId) {
		return actionRegistry.getAction(actionId);
	}

	public void createMenuItem(Menu menu, IAction action) {
		boolean enabled = action.isEnabled();
		boolean hidden = false;
		if (hidden) {
			return;
		}
		MenuItem item = new MenuItem(menu, SWT.CASCADE);
		String displayName = action.getText();
		item.addSelectionListener(new AL(action));
		item.setText(displayName);
		item.setEnabled(enabled);
	}

	static class AL implements SelectionListener {
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
