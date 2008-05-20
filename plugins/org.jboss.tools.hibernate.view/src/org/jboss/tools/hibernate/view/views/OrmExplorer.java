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
package org.jboss.tools.hibernate.view.views;

import java.util.ResourceBundle;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IOrmElement;
import org.jboss.tools.hibernate.core.IOrmModel;
import org.jboss.tools.hibernate.core.IOrmModelListener;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.OrmModelEvent;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.view.ViewPlugin;


/**
 * @author Tau from Minsk
 *
 */

public abstract class OrmExplorer extends ViewPart {

    private TreeViewer viewer;
    //akuzmin 17.08.2005
    Action expireAction;

	//ExplorerClass
	static protected Action actionRootPackageClassField;
	//static protected Action actionRootClassField; // del tau 29.07.205
	static protected Action actionRootStorageClassField;
	
	//ExplorerBase
	static protected Action actionRootSchemaTableColumn;
	//static protected Action actionRootTableColumn; // del tau 29.07.205
	
	
	//private Action action2;
	private Action doubleClickAction;
	// $added$ by Konstantin Mishin on 2005/08/12 fixed fo ORMIISTUD-442
	private Action deleteKeyAction;
	// $added$
	protected IOrmModel ormModel;
	
	protected ResourceBundle BUNDLE_IMAGE = ViewPlugin.BUNDLE_IMAGE;
	protected ResourceBundle BUNDLE = ResourceBundle.getBundle(ExplorerClass.class.getPackage().getName() + ".views");
	
	// add 11.03.2005
	protected ITreeContentProvider contentProvider;
	
	// add 11.04.2005
	private ActionExplorerVisitor actionExplorerVisitor;
	
	private static DoubleClickVisitor doubleClickVisitor = new DoubleClickVisitor();
	
	// $added$ by Konstantin Mishin on 2005/08/12 fixed fo ORMIISTUD-442
	private static DeleteKeyVisitor deleteKeyVisitor = new DeleteKeyVisitor();
	// $added$
	// add tau 22.06.2005
	protected static final OrmContentProvider contentProvider_PACKAGE_CLASS_FIELD_CONTENT_PROVIDER = new OrmContentProvider(OrmContentProvider.PACKAGE_CLASS_FIELD_CONTENT_PROVIDER);
	protected static final OrmContentProvider contentProvider_CLASS_FIELD_CONTENT_PROVIDER = new OrmContentProvider(OrmContentProvider.CLASS_FIELD_CONTENT_PROVIDER);
	protected static final OrmContentProvider contentProvider_STORAGE_CLASS_FIELD_CONTENT_PROVIDER = new OrmContentProvider(OrmContentProvider.STORAGE_CLASS_FIELD_CONTENT_PROVIDER);
	
	protected static final OrmContentProvider contentProvider_SCHEMA_TABLE_COLUMN_CONTENT_PROVIDER = new OrmContentProvider(OrmContentProvider.SCHEMA_TABLE_COLUMN_CONTENT_PROVIDER);
	protected static final OrmContentProvider contentProvider_TABLE_COLUMN_CONTENT_PROVIDER = new OrmContentProvider(OrmContentProvider.TABLE_COLUMN_CONTENT_PROVIDER);

	// add tau 26.07.2005
	static private Boolean NO_EXPAND = new Boolean(false);	
	static private Boolean EXPAND = new Boolean(true);
	
	// add tau 23.03.2006
	private IOrmModelListener ormModelListener;
	
	/**
	 * The constructor.
	 */
	public OrmExplorer() {
		super();
	}

	public void createPartControl(Composite parent) {
		
	    //viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
	    
	    makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		// $added$ by Konstantin Mishin on 2005/08/12 fixed fo ORMIISTUD-442
		hookDeleteKeyAction();
		// $added$
		//contributeToActionBars();
		
		OrmModelImageVisitor imageVisitor = new OrmModelImageVisitor();
		OrmModelNameVisitor nameVisitor = new OrmModelNameVisitor(viewer);
		
		OrmLabelProvider label = new OrmLabelProvider(imageVisitor, nameVisitor);
		
		ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		
		viewer.setLabelProvider(new DecoratingLabelProvider(label, decorator));

		setOrmProjects(OrmCore.getDefault().getOrmModel());
		
		// add 11.04.2005
		actionExplorerVisitor = new ActionExplorerVisitor(viewer);
		
		//action....run(); // run in extended classes
		//hookListeners();

	   // 20050617 <yan> 
		viewer.getControl().addKeyListener(
	   		 new org.eclipse.swt.events.KeyAdapter() {
				
	   			 	// edit tau 15.02.2006 - ViewsUtils.getSelectedObject(IMapping.class, viewer)==null)
					public void keyReleased(KeyEvent e) {
						if (e.stateMask==0 && e.keyCode==SWT.F5 && actionExplorerVisitor!=null) {
							if (ViewsUtils.getSelectedObject(IMapping.class, viewer)==null) {
								if (ViewsAction.refreshOrmProjectAction!=null) ViewsAction.refreshOrmProjectAction.run(viewer);
							} else {
								if (ViewsAction.refreshMappingAction!=null) ViewsAction.refreshMappingAction.run(viewer);
							}
						}
					}
				
				}
	    );
		//</yan>

	}

	public void viewerSetInput(Object input, ITreeContentProvider contentProvider ) {

		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW )	ExceptionHandler.logInfo("OrmExplorer.viewerSetInput(),input= "+input );
		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW )	ExceptionHandler.logInfo("OrmExplorer.viewerSetInput(),contentProvider= "+contentProvider );		
		
		saveExpandedElements(); // add tau 29.06.2005
		//Map mapElements = new HashMap();
		//saveElements(mapElements);
		
		if (input instanceof IOrmModel) {		
			viewer.setContentProvider(contentProvider);
		} else {
			if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW )	ExceptionHandler.logInfo("STOP, -> viewerSetInput(...),input= "+input );
		}
		// add 28.06.2005 tau
		if (this.contentProvider == null) {
			viewer.setInput(input);			
		}
		this.contentProvider = contentProvider;
		
		loadExpandedElements(); // add tau 29.06.2005
		//restoreElements(mapElements);
	}	

    private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				//Explorer.this.fillContextMenu(manager);
				//TreeItem[] items = viewer.getTree().getSelection();
				//if ((items.length != 0) && (items[0].getData() instanceof IOrmElement)) {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				if (obj instanceof IOrmElement) {
					//akuzmin 17.08.2005
						// add OrmCore.lock.acquire(); 05.12.2005
						try {
							
							if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW ) ExceptionHandler.logInfo("!TRY OrmExplorer.hookContextMenu(...) lock(=" + OrmCore.lock + ").acquire(), Depth=" + OrmCore.lock.getDepth());							
							OrmCore.lock.acquire(); // add tau 05.12.2005
							
							// add tau 06.12.2005
							if (((OrmContentProvider)viewer.getContentProvider()).lockMenu) {
								manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS)); // add 09.03.2006								
								return;
							}
									
							if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW ) ExceptionHandler.logInfo("!RUN OrmExplorer.hookContextMenu(...) lock(=" + OrmCore.lock + ").acquire(), Depth=" + OrmCore.lock.getDepth());
							
							((IOrmElement) obj).accept(actionExplorerVisitor, manager);
							manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS)); // add 19.04.2005
						} finally {
					    	 OrmCore.lock.release();
						}

				} else {
					manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS)); // added 2005.06.08 by Yan
				}
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	protected void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		//fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	abstract protected void fillContextMenu(IMenuManager manager);
	
	abstract protected void fillLocalToolBar(IToolBarManager manager);
	
	static public String getExplorerId(){
		return null;
	}
	
	private void makeActions() {
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object element = ((IStructuredSelection)selection).getFirstElement();
				if (element instanceof IOrmElement) {
					//((IOrmElement) element).accept(doubleClickVisitor, viewer); // del tau 09.06.2005
					((IOrmElement) element).accept(doubleClickVisitor, actionExplorerVisitor); // add tau 09.06.2005

				}
				
				//showMessage("Double-click detected");
			}
		};
		// $added$ by Konstantin Mishin on 2005/08/12 fixed fo ORMIISTUD-442
		deleteKeyAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object element = ((IStructuredSelection)selection).getFirstElement();
				if (element instanceof IOrmElement) {
					((IOrmElement) element).accept(deleteKeyVisitor, actionExplorerVisitor);

				}
			}
		};
		// $added$
//		akuzmin 17.08.2005
		expireAction = new Action() {
			public void run() {			
			MessageDialog.openInformation(ViewPlugin.getActiveWorkbenchShell(),
					 							BUNDLE.getString("OrmExplorer.licenceTitle"),BUNDLE.getString("OrmExplorer.licencemessage"));
		}
		};

		
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	// $added$ by Konstantin Mishin on 2005/08/12 fixed fo ORMIISTUD-442
	private void hookDeleteKeyAction() {
		viewer.getControl().addKeyListener( new org.eclipse.swt.events.KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.stateMask==0 && e.keyCode==SWT.DEL) {
					deleteKeyAction.run();
				}

			}
		});
	}
	// $added$
	
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
    // add 04.07.2005 tau
	public void setFocus(Object element, int expandToLevel) {
		ISelection selection =  new StructuredSelection(element);
		viewer.setSelection(selection, true);
		if (expandToLevel > 0){
			viewer.expandToLevel(element, expandToLevel);			
		}
		viewer.getControl().setFocus();
	}	
	
	// 20050611 <yan>
	private void setupContentDescription(IOrmModel ormModel) {
		
		final String desc=BUNDLE.getString(
				ormModel!=null && ormModel.getOrmProjects().length>0?
						"Explorer.ormProjectsWasFound":"Explorer.ormProjectsNotFound"
		);
		
		if (!desc.equals(getContentDescription())) {
			// Update tau 14.06.2005
			Control control = viewer.getControl();
			if (control == null || control.isDisposed())
				return;

			control.getDisplay().syncExec(new Runnable() {
				public void run() {
					if (!viewer.getControl().isDisposed())
						setContentDescription(desc);
						firePropertyChange(IWorkbenchPartConstants.PROP_CONTENT_DESCRIPTION);
				}
			});


		}
		
	}
	// </yan>

	/**
	 * @param ormModel
	 */
	public void setOrmProjects(IOrmModel ormModel) {
		
		this.ormModel = ormModel;
		
		// 20050611 <yan>
		setupContentDescription(ormModel); 
		
		//edit tau 23.03.2006
		ormModelListener = new IOrmModelListener() {
			public void modelChanged(OrmModelEvent event) {
				setupContentDescription(event.getOrmModel());
			}
		};
		
		if (ormModel!=null) {
			ormModel.addListener(ormModelListener);
		}
		// </yan>
		
	}
	
	/*
				protected void hookListeners() {
					viewer.addSelectionChangedListener(new ISelectionChangedListener() {
						public void selectionChanged(SelectionChangedEvent event) {
							System.out.println(event.getSelection());
							if(event.getSelection().isEmpty()) return;
							if(event.getSelection() instanceof IStructuredSelection) {
								IStructuredSelection selection = (IStructuredSelection)event.getSelection();
			
								for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
									//Object domain = (Model) iterator.next();
								}
							}
						}
					});
					
				}
		*/	
	
	/**
	 * @return Returns the contentProvider.
	 */
	public IContentProvider getContentProvider() {
		return contentProvider;
	}
	/**
	 * @param contentProvider The contentProvider to set.
	 */
	public void setContentProvider(IContentProvider contentProvider) {
		this.contentProvider = (ITreeContentProvider) contentProvider;
	}
	
	public TreeViewer getTreeViewer(){
		return viewer;
	}
	
	// add tau 29.06.2005
	private void saveExpandedElements() {
		viewer.getTree().setRedraw(false);		
		OrmContentProvider ormContentProvider = ((OrmContentProvider) viewer.getContentProvider());
		if (ormContentProvider != null) {
			ormContentProvider.setExpandedElements(viewer.getExpandedElements());
			ormContentProvider.setSelectionElements(viewer.getSelection());			
		}
	}

	// add tau 29.06.2005
	private void loadExpandedElements() {
		OrmContentProvider ormContentProvider = ((OrmContentProvider) viewer.getContentProvider());
		if (ormContentProvider != null) {
			Object[] expandedElements = ormContentProvider.getExpandedElements();
			ISelection selectionElements = ormContentProvider.getSelectionElements(); 			
			//viewer.getTree().setRedraw(false);			
			if (expandedElements != null){
				viewer.setExpandedElements(expandedElements);
			} else {
				viewer.collapseAll();
			}
			
			if (selectionElements != null){
				viewer.setSelection(selectionElements,true);
				TreeItem[] arraySelection = viewer.getTree().getSelection();
				if (arraySelection != null && arraySelection.length != 0) {
					viewer.reveal(arraySelection[0].getData());
				}
			}
			//viewer.getTree().setRedraw(true);			
		}
		viewer.getTree().setRedraw(true);		
	}
	
	// add tau 26.07.2005
/*
	private void saveElements(Map map) {
		OrmContentProvider ormContentProvider = ((OrmContentProvider) viewer.getContentProvider());
		if (ormContentProvider != null) {
			Tree tree = viewer.getTree();
			TreeItem[] items = tree.getItems();
			for (int i = 0; i < items.length; i++) {
				TreeItem item = items[i];
				if (item.getExpanded()){
					map.put(((IOrmElement)item.getData()).getName(),EXPAND);
				} else {
					map.put(((IOrmElement)item.getData()).getName(),NO_EXPAND);					
				}
			}
		}
	}
*/	
	// add tau 26.07.2005
/*
	private void restoreElements(Map map) {
		viewer.getTree().setRedraw(false);		
		OrmContentProvider ormContentProvider = ((OrmContentProvider) viewer.getContentProvider());
		if (ormContentProvider != null) {
			Tree tree = viewer.getTree();
			TreeItem[] items = tree.getItems();
			for (int i = 0; i < items.length; i++) {
				TreeItem item = items[i];
				Boolean data = (Boolean) map.get(((IOrmElement)item.getData()).getName());
				if ( data != null && data.equals(EXPAND) ) {
					item.setExpanded(true);
				} else {
					item.setExpanded(false);					
				}
			}
			viewer.getTree().setRedraw(true);			
		}
	}
*/
	// add tau 23.03.2006
	public void dispose() {
		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW ) ExceptionHandler.logInfo("OrmExplorer.dispose()" );
		if (ormModel != null && ormModelListener != null) {
			ormModel.removeListener(ormModelListener);
		}		
		//ormModel.removeListener(this);
		super.dispose();
	}	
		
}