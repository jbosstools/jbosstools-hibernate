/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.QueryEditor;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.criteriaeditor.CriteriaEditor;
import org.hibernate.eclipse.hqleditor.HQLEditor;

/**
 * @author max
 *
 */
public class ExecuteHQLAction extends Action implements IMenuCreator, IWorkbenchWindowPulldownDelegate2 {


	Menu dropDown = null;

	private IWorkbenchWindow window;

	private QueryEditor editor;
	
	public ExecuteHQLAction() {
		
		setMenuCreator(this);
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.EXECUTE) );
		initTextAndToolTip();
	}


	public ExecuteHQLAction(QueryEditor editor) {
		this();
		setHibernateQueryEditor( editor );
	}


	public void dispose() {
		if(dropDown!=null) dropDown.dispose();
	}

	public Menu getMenu(Control parent) {
		if (dropDown != null) dropDown.dispose();
		dropDown = new Menu(parent);
		/**
		 * Add listener to repopulate the menu each time do keep uptodate with configurationis
		 */
		dropDown.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				Menu currentMenu = (Menu)e.widget;
				MenuItem[] items = currentMenu.getItems();
				for (int i=0; i < items.length; i++) {
					items[i].dispose();
				}
				fillMenu(currentMenu);
			}
		});
		return dropDown;
	}

	protected void fillMenu(Menu menu) {
		ConsoleConfiguration lastUsed = HibernateConsolePlugin.getDefault().getLastUsedConfiguration();
		
		if(editor!=null) {
			lastUsed = editor.getConsoleConfiguration();
		}
		
		if (lastUsed != null) {
			createSubAction(menu, lastUsed);
			Separator separator = new Separator();
			separator.fill(menu, -1);
		}
		
		ConsoleConfiguration[] configs = KnownConfigurations.getInstance().getConfigurations();
		//Arrays.sort(bookmarks, new DisplayableComparator() );
		for (int i = 0, length = configs == null ? 0 : configs.length; i < length; i++) {
			final ConsoleConfiguration bookmark = configs[i];
			createSubAction(menu, bookmark);
		}
	}

	private void createSubAction(Menu menu, final ConsoleConfiguration lastUsed) {
		Action action = new Action() {
			public void run() {
				IEditorPart activeEditor = window.getActivePage().getActiveEditor();
				if(activeEditor!=null && activeEditor instanceof QueryEditor) {
					ExecuteHQLAction.this.execute(lastUsed, (QueryEditor) activeEditor);
				}
			}
		};
		//action.setImageDescriptor(ImageStore.getImageDescriptor(ImageStore.BOOKMARK) );
		
		action.setText(lastUsed.getName());
		ActionContributionItem item = new ActionContributionItem(action);
		item.fill(menu, -1);
	}

	public void run() {
		execute(editor.getConsoleConfiguration(), editor );
	}
	
	public void runWithEvent(Event event) {
		super.runWithEvent( event );
	}
	protected void execute(ConsoleConfiguration lastUsed, QueryEditor queryEditor) {
		try {	
			if(lastUsed!=null) {
				if (!lastUsed.isSessionFactoryCreated()) {
					if (queryEditor.askUserForConfiguration(lastUsed.getName())) {
						if(lastUsed.getConfiguration()==null) {
							lastUsed.build();
						}
						lastUsed.buildSessionFactory();
						queryEditor.executeQuery(lastUsed);						
					}
				} else {
					queryEditor.executeQuery(lastUsed);					
				}
			} 
		}
		finally {
			HibernateConsolePlugin.getDefault().setLastUsedConfiguration(lastUsed);
			initTextAndToolTip();
		}
		
	}

	private void initTextAndToolTip() {
		setText("Run HQL");
		setToolTipText("Run HQL");
	}

	public Menu getMenu(Menu parent) {
		throw new IllegalStateException("should not be called!");
	}

	public void setText(String text) {
			super.setText( text );
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}


	public void setHibernateQueryEditor(QueryEditor editor) {
		this.editor = editor;		
	}


	
	
	/*private IDocument getDocument() {
		
		ITextEditor editor= getTextEditor();
		if (editor != null) {
			
			IDocumentProvider provider= editor.getDocumentProvider();
			IEditorInput input= editor.getEditorInput();
			if (provider != null && input != null)
				return provider.getDocument(input);
			
		}
		return null;
	}*/
}
