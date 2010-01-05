/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.jdt.ui.internal.jpa.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
import org.hibernate.eclipse.jdt.ui.internal.JdtUiMessages;

/**
 * Context menu action delegate for "Generate Hibernate/JPA annotations..."
 *
 * @author Vitali
 */
public class JPAMapToolActionPulldownDelegate implements
		IWorkbenchWindowPulldownDelegate2, IEditorActionDelegate, IActionDelegate2, IMenuCreator {

	/**
	 * The menu created by this action
	 */
	private Menu fMenu;
	protected boolean fRecreateMenu = false;

	public JPAMapToolActor actor = JPAMapToolActor.getInstance();

	public Menu getMenu(Menu parent) {
		setMenu(new Menu(parent));
		fillMenu(fMenu);
		initMenu();
		return fMenu;
	}

	public Menu getMenu(Control parent) {
		setMenu(new Menu(parent));
		fillMenu(fMenu);
		initMenu();
		return fMenu;
	}

	public void dispose() {
		setMenu(null);
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
		actor.setSelection(selection);
		if (action != null) {
			action.setEnabled(actor.getSelectedSourceSize() > 0);
		}
	}

	protected void addToMenu(Menu menu, IAction action, int accelerator) {
		StringBuffer label = new StringBuffer();
		if (accelerator >= 0 && accelerator < 10) {
			// add the numerical accelerator
			label.append('&');
			label.append(accelerator);
			label.append(' ');
		}
		label.append(action.getText());
		action.setText(label.toString());
		ActionContributionItem item = new ActionContributionItem(action);
		item.fill(menu, -1);
	}

	protected void fillMenu(Menu menu) {
		IAction action = new Action(JdtUiMessages.JPAMapToolActionPulldownDelegate_menu) {
			public void run() {
				//actor.updateOpen();
				actor.updateSelected(Integer.MAX_VALUE);
			}
		};
		addToMenu(menu, action, -1);
	}

	private void initMenu() {
		// Add listener to re-populate the menu each time
		// it is shown because of dynamic list
		fMenu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				Menu m = (Menu) e.widget;
				if (fRecreateMenu) {
					MenuItem[] items = m.getItems();
					for (int i = 0; i < items.length; i++) {
						items[i].dispose();
					}
					fillMenu(m);
					fRecreateMenu = false;
				}
				m.setEnabled(actor.getSelectedSourceSize() > 0);
				MenuItem[] items = m.getItems();
				for (int i = 0; i < items.length; i++) {
					items[i].setEnabled(actor.getSelectedSourceSize() > 0);
				}
			}
			public void menuHidden(MenuEvent e) {
				fRecreateMenu = true;
			}
		});
	}

	private void setMenu(Menu menu) {
		if (fMenu != null) {
			fMenu.dispose();
		}
		fMenu = menu;
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (action != null) {
			action.setEnabled(actor.getSelectedSourceSize() > 0);
		}
	}

	public void init(IAction action) {
		if (action instanceof Action) {
			((Action)action).setMenuCreator(this);
		}
	}

	public void runWithEvent(IAction action, Event event) {
		//actor.updateOpen();
		actor.updateSelected(Integer.MAX_VALUE);
	}

}
