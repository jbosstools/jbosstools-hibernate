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
package org.hibernate.eclipse.console.views;


import java.io.FileNotFoundException;

import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.node.BaseNode;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.actions.EditConsoleConfiguration;
import org.hibernate.eclipse.console.actions.OpenMappingAction;
import org.hibernate.eclipse.console.viewers.xpl.MTreeViewer;
import org.hibernate.eclipse.console.workbench.xpl.AnyAdaptableLabelProvider;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;


/**
 * @author max
 *
 */
@SuppressWarnings("restriction")
public class KnownConfigurationsView extends ViewPart {

	public static final String ID = "org.hibernate.eclipse.console.views.KnownConfigurationsView";	//$NON-NLS-1$

	TreeViewer viewer;
	
	private ActionGroup actionGroup;
	private Action doubleAction;
	
	public KnownConfigurationsView() {
		super();
	}

	public void createPartControl(Composite parent) {
		//viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer = new MTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		/*viewer.setContentProvider(new ConfigurationsContentProvider() );*/
		
		/*viewer.setLabelProvider(new ConfigurationsLabelProvider() );*/
		
		
		viewer.setLabelProvider(new AnyAdaptableLabelProvider());
		
		final KnownConfigurationsProvider cp = new KnownConfigurationsProvider();
		viewer.setContentProvider(cp);
		DebugUIPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(cp);
		
		viewer.getTree().addDisposeListener(new DisposeListener() {
			
			public void widgetDisposed(DisposeEvent e) {
				DebugUIPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(cp);
			}
		});
		
		//viewer.setInput(KnownConfigurations.getInstance().getRootNode() );
		viewer.setInput( KnownConfigurations.getInstance() );
		
		makeActions();
		createContextMenu();		
		hookDoubleClick();
		provideSelection();
	}

    private void hookDoubleClick() {
    	viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleAction.run();
			}
		});
	}

	private void provideSelection() {
		getSite().setSelectionProvider(viewer);
	}

	private void createContextMenu ()
    {
		MenuManager menuMgr = new MenuManager("#PopupMenu");	//$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});

		Menu menu = menuMgr.createContextMenu(viewer.getControl() );
		
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
		
		IActionBars actionBars = getViewSite().getActionBars();
		
		IMenuManager dropDownMenu = actionBars.getMenuManager();
		
		actionGroup.fillContextMenu(dropDownMenu);
		actionGroup.fillActionBars(actionBars);
    }
    
	protected void fillContextMenu(IMenuManager menuMgr) {
		actionGroup.setContext(new ActionContext(viewer.getSelection() ) );
		actionGroup.fillContextMenu(menuMgr);
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS) );		
	}

	/**
	 * 
	 */
	private void makeActions() {
		
		this.actionGroup = new ConfigurationsViewActionGroup(this, viewer);
		
		this.doubleAction = new Action() {
			public void run() {
				// TODO: make action dependent on having a connected console configuration!
				ISelection selection = viewer.getSelection();
				if (selection == null || selection.isEmpty()) {
					return;
				}
				Object firstElement = ( (IStructuredSelection)selection).getFirstElement();
				if(firstElement instanceof ConsoleConfiguration) {
					new EditConsoleConfiguration((ConsoleConfiguration)firstElement).run();
				} else if (firstElement instanceof BaseNode){
					BaseNode node = (BaseNode) firstElement;
					ConsoleConfiguration consoleConfiguration = node.getConsoleConfiguration();
					if(consoleConfiguration.isSessionFactoryCreated() ) {
						String hql = node.getHQL();
						// open HQL Editor
						HibernateConsolePlugin.getDefault().openScratchHQLEditor(consoleConfiguration.getName(), hql);
						/** /
						// execute query and show results in 
						// Hibernate Query result view - commented cause old version
						if(StringHelper.isNotEmpty( hql )) {
							try {
								if (getSite() != null && getSite().getPage() != null) {
									getSite().getPage().showView(QueryPageTabView.ID);
								}
							} catch (PartInitException e) {
								HibernateConsolePlugin.getDefault().logErrorMessage("Can't show QueryPageTabView.", e);	//$NON-NLS-1$
							}
							consoleConfiguration.executeHQLQuery( hql );
						}
						/**/
					}
				} else if (selection instanceof TreeSelection){
					TreePath[] paths = ((TreeSelection)selection).getPaths();
					TreePath path = paths[0];
					Object last = path.getLastSegment();
					ConsoleConfiguration consoleConfig = (ConsoleConfiguration)(path.getSegment(0));
					if (last instanceof PersistentClass || last.getClass() == Property.class){
						try {
							OpenMappingAction.run(consoleConfig, path);
						} catch (PartInitException e) {
							HibernateConsolePlugin.getDefault().logErrorMessage("Can't find mapping file.", e);	//$NON-NLS-1$
						} catch (JavaModelException e) {
							HibernateConsolePlugin.getDefault().logErrorMessage("Can't find mapping file.", e);	//$NON-NLS-1$
						} catch (FileNotFoundException e) {
							HibernateConsolePlugin.getDefault().logErrorMessage("Can't find mapping file.", e);	//$NON-NLS-1$
						}
					}
					else {
						for (int i = 0; i < paths.length; i++) {
							if (viewer.getExpandedState(paths[i])) {
								viewer.collapseToLevel(paths[i], 1);
							}
							else {
								viewer.expandToLevel(paths[i], 1);
							}
						}
					}
				}
			}
		};
	}
	
	public void dispose() {
		super.dispose();
		actionGroup.dispose();
		
	}

	public void setFocus() {
		viewer.getTree().setFocus();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter.equals(IPropertySheetPage.class) )
		{
			PropertySheetPage page = new PropertySheetPage();
			page.setPropertySourceProvider(new ConsoleConfigurationPropertySourceProvider() );
			return page;
		}
		
		return super.getAdapter( adapter );
	}
}
