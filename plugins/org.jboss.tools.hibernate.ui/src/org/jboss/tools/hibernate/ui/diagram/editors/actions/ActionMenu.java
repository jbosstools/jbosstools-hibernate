/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.diagram.editors.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * Menu with list of actions.
 * 
 * @author Vitali Yemialyanchyk
 */
public class ActionMenu extends Action implements IMenuCreator {

	private Action[] actions;
	private Menu menu;

	public ActionMenu(Action[] actions) {
		this.actions = actions;
		if (this.actions.length > 0) {
			setToolTipText(actions[0].getToolTipText());
			setImageDescriptor(actions[0].getImageDescriptor());
			if (actions.length > 1) {
				setMenuCreator(this);
			}
		}
	}

	public void run() {
		if (actions.length > 0) {
			actions[0].run();
		}
	}

	public void dispose() {
		if (menu != null) {
			menu.dispose();
			menu = null;
		}
	}

	public Menu getMenu(Control parent) {
		if (menu != null) {
			menu.dispose();
		}
		menu = new Menu(parent);
		for (int i = 0; i < actions.length; i++) {
			addActionToMenu(menu, actions[i]);
		}
		return menu;
	}

	public Menu getMenu(Menu parent) {
		if (menu != null) {
			menu.dispose();
		}
		menu = new Menu(parent);
		for (int i = 0; i < actions.length; i++) {
			addActionToMenu(menu, actions[i]);
		}
		return menu;
	}

	protected void addActionToMenu(Menu parent, Action action) {
		ActionContributionItem item = new ActionContributionItem(action);
		item.fill(parent, -1);
	}
}
