/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.view.views;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;


public class OrmContentProvider implements ITreeContentProvider/*, IOrmModelListener, IOrmProjectChangedListener*/  {

	protected static final int PACKAGE_CLASS_FIELD_CONTENT_PROVIDER = 1;	
	protected static final int CLASS_FIELD_CONTENT_PROVIDER = 2;
	protected static final int STORAGE_CLASS_FIELD_CONTENT_PROVIDER = 3;
	
	protected static final int SCHEMA_TABLE_COLUMN_CONTENT_PROVIDER = 4;
	protected static final int TABLE_COLUMN_CONTENT_PROVIDER = 5;	
	
	protected TreeViewer viewer;
	
	private static final ContentProviderVisitor contentProviderVisitor = new ContentProviderVisitor();
	private static final Object[] nullChildren = new Object[0];
	private static Object[] children = new Object[0];
	private int tip;
	private boolean sorting;
	
	private Object[] expandedElements = null;	
	private ISelection selectionElements = null;
	
	private String[] beforeChangeElements = null;	
	private String beforeChangeSelectionElementQualifiedName = null;

	public boolean lockMenu = false;

	
	public OrmContentProvider(int tip){
		this.tip = tip;
		sorting = true; // default - sort
	}

	public Object[] getChildren(Object parentElement) {
		if (children == null) 
			children = nullChildren;

		if (!sorting){
			sorting = true; // default - sort
		} else {
//			Arrays.sort(children, comparator);
		}
		return children;
	}
	
	public Object getParent(Object element) {
		
		//if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW )	ExceptionHandler.logInfo("!!! OrmContentProvider.getParent(Object element):" + element + ", tip="+tip);
		
		return null;
	}

	public boolean hasChildren(Object element) {
    	//TODO EXP 3d
		//return getChildren(element).length > 0;
//		if (element instanceof IDatabaseColumn ||
//			element instanceof IDatabaseConstraint ||
//			element instanceof INamedQueryMapping ||
//			element instanceof String) {			
//				return false;
//		} else 
			return true;		
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

//		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW )	ExceptionHandler.logInfo("OrmContentProvider.inputChanged():"+", tip="+tip+", this= " + this);		
//		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW )	ExceptionHandler.logInfo("OrmContentProvider.inputChanged(): OLD_INPUT= " + oldInput + ", tip="+tip);		
//		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW )	ExceptionHandler.logInfo("OrmContentProvider.inputChanged(): NEW_INPUT= " + newInput + ", tip="+tip);		
//		
//		this.viewer = (TreeViewer) viewer;
//		
//		if (oldInput != null) {
//			removeListenerFrom((IOrmModel) oldInput);
//		}
//		
//		if (newInput != null) {
//			addListenerTo((IOrmModel) newInput);
//		}
	}

//	protected void removeListenerFrom(IOrmModel model) {
//		model.removeListener(this);
//		IOrmProject[] projects = model.getOrmProjects();
//		for (int i = 0; i < projects.length; i++) {
//			IOrmProject project = projects[i];
//			project.removeChangedListener(this);
//			// add tau 12.09.2005
//			//project.removeBeforeChangeListener(this);			
//		}
//	}

//	protected void addListenerTo(IOrmModel model) {
//		model.addListener(this);
//		IOrmProject[] projects = model.getOrmProjects();
//		for (int i = 0; i < projects.length; i++) {
//			IOrmProject project = projects[i];
//			project.addChangedListener(this);
//			// add tau 12.09.2005
//			//project.addBeforeChangeListener(this);			
//		}
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.tools.hibernate.core.IOrmModelListener#projectsChanged(org.jboss.tools.hibernate.core.OrmModelEvent)
	 */
//	public void modelChanged(OrmModelEvent event) {
//
//		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW )
//			ExceptionHandler.logInfo("OrmContentProvider.modelChanged(OrmModelEvent)" + ",tip="+tip);
//		
//		// Add tau 21.06.2005
//		int type = event.getType();
//		if (type == OrmModelEvent.AddProject){
//			((IOrmProject) event.getProject()).addChangedListener(this);
//			// add tau 28.11.2005
//			//((IOrmProject) event.getProject()).addBeforeChangeListener(this);			
//		}
//		
//		
//		// Update tau 18.05.2005
//		final IOrmModel model = event.getOrmModel();
//		viewerRefreshUpdate(model, false);
//		
//		/*
//		Control control = viewer.getControl();
//		if (control == null || control.isDisposed())
//			return;
//
//		control.getDisplay().syncExec(new Runnable() {
//			public void run() {
//				if (!viewer.getControl().isDisposed()){
//					viewer.getTree().setRedraw(false);
//					viewer.refresh(model, true);
//					viewer.getTree().setRedraw(true);					
//					// add 04.04.2005
//					//viewer.getControl().setFocus();
//				}
//			}
//		});
//		*/
//
//	}

	/*
	 * edit 05.12.2005
	 * 
	 * @see org.jboss.tools.hibernate.core.IOrmProjectListener#modelChanged(org.jboss.tools.hibernate.core.OrmProjectEvent)
	 */
//	public void projectChanged(OrmProjectEvent event, boolean flagUpdate) {
//	
//		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW ) ExceptionHandler.logInfo("OrmContentProvider.projectChanged((eventSource=," + event.getSource()+ "), tip="+tip);
//		
//		final IOrmProject project = event.getProject();
//		
//		viewerRefreshUpdate(project, flagUpdate);
//		
//		/* del tau 05.2005 
//		//!!! event from OrmBuilder == IOrmProject - edit tau 05.12.2005
//		if (event.getSource() instanceof IOrmProject || !(isBuilder(project.getProject()))){ 
//			viewerRefresh(project);			
//		}
//		*/
//	}
	
	/*
	public void projectBeforeChange(OrmProjectEvent event) {
		
		Control control = viewer.getControl();
		if (control == null || control.isDisposed()) {
			if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW ) ExceptionHandler.logInfo("->NO REFRESH, control="+ control + ",tip="+tip);
			return;			
		}
		
		// add tau 17.11.2005
		if (beforeChangeElements != null){
			if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW ) ExceptionHandler.logInfo("->projectBeforeChange("+event.getSource()+")beforeChangeElements != null"+",tip="+tip);			
			return;
		}

		// TODO (!tau->tau) asyncExec???
		//control.getDisplay().asyncExec(new Runnable() {
		control.getDisplay().syncExec(new Runnable() {
			public void run() {
				// edit tau 24.11.2005 +OrmCore.lock.acquire();
				try {
					if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW ) ExceptionHandler.logInfo("-> TRY projectBeforeChange(...) lock(=" + OrmCore.lock + ").acquire(), Depth=" + OrmCore.lock.getDepth() + ", tip="+tip);					
					OrmCore.lock.acquire();
					lockMenu = true; // add tau 05.12.2005
					if (!viewer.getControl().isDisposed()) {
						beforeChangeElements = saveElements();	
						beforeChangeSelectionElementQualifiedName = saveSelection();
					}					
			     } finally {
			    	 OrmCore.lock.release();
			    	 lockMenu = false; // add tau 05.12.2005
					 if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW ) ExceptionHandler.logInfo("-> END projectBeforeChange(...)=lock(=" + OrmCore.lock.toString() + ").release(), Depth=" + OrmCore.lock.getDepth() + ", tip="+tip);					    	 
			     }
			}
		});
	}	
	*/
	

	public int getTip() {
		return tip;
	}

	public void setSorting(boolean sorting) {
		this.sorting = sorting;
	}

	/*
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	*/

	public Object[] getExpandedElements() {
		return expandedElements;
	}

	public void setExpandedElements(Object[] expandedElements) {
		this.expandedElements = expandedElements;
	}

	public ISelection getSelectionElements() {
		return selectionElements;
	}

	public void setSelectionElements(ISelection selectionElements) {
		this.selectionElements = selectionElements;
	}
	
	public void viewerRefreshUpdate(final Object element, final boolean flagUpdate) {
		
//		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW ) ExceptionHandler.logInfo("OrmContentProvider.viewerRefresh(...)1= " + element + " ,flagUpdate = "+ flagUpdate);		
		
		Control control = viewer.getControl();
			
		if (control == null || control.isDisposed()) {
//			if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW ) ExceptionHandler.logInfo("-> NO REFRESH, control="+ control + ",tip="+tip);
			return;			
		}
			
		Display display = control.getDisplay();
		
		/*
		Thread tc = display.getSyncThread();
		boolean dd = display.isDisposed();
		boolean dd2 = display.isDisposed();
		Thread tt = display.getThread();
		
		// add tau 24.01.2006
		display.wake();
		
		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW ) ExceptionHandler.logInfo("OrmContentProvider.viewerRefresh(...)4= " + tc + tt + dd + dd2);
		*/		
		
			// edit tau 30.01.2006
			//display.syncExec(new Runnable() {
		display.asyncExec(new Runnable() {
		
				public void run() {
					// add try and OrmCore.lock.acquire(); - tau 01.12.2005					
					try {					
//						if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW ) ExceptionHandler.logInfo("-> TRY REFRESH lock(=" + OrmCore.lock + ").acquire(), Depth=" + OrmCore.lock.getDepth() + ", viewerRefresh("+element+"),tip="+tip);			
//						OrmCore.lock.acquire();
//						if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW ) ExceptionHandler.logInfo("-> RUN REFRESH, viewerRefresh("+element+"), isDisposed()="+ viewer.getControl().isDisposed() + ",tip="+tip);						
					
						if (!viewer.getControl().isDisposed()) {
							
//							if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW ) ExceptionHandler.logInfo("-> REFRESHing, viewerRefresh("+element+"),tip="+tip);

							viewer.getTree().setRedraw(false);

							// add tau 02.12.2005 SAVE
							if (beforeChangeElements == null){
								beforeChangeElements = saveElements();	
								beforeChangeSelectionElementQualifiedName = saveSelection();
							}
							
							if (flagUpdate) {
								// Update
//								if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW ) ExceptionHandler.logInfo("viewerRefresh("+element+") -> viewer.update(element, null), tip = "+tip);								
								viewer.update(element, null);								
							} else {
								// REFRESH
//								if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW ) ExceptionHandler.logInfo("viewerRefresh("+element+") -> viewer.refresh(element, true), tip = "+tip);								
								viewer.refresh(element, true);								
							}
							
							// LOAD
							if (beforeChangeElements != null){
								restoreElements(beforeChangeElements, beforeChangeSelectionElementQualifiedName);
								beforeChangeElements = null;						
							}
							
							viewer.getTree().setRedraw(true);					
						}
				     } finally {
//					    	 OrmCore.lock.release();
//							 if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW ) ExceptionHandler.logInfo("-> END REFRESH=lock(=" + OrmCore.lock.toString() + ").release(), Depth=" + OrmCore.lock.getDepth() + ", viewerRefresh("+element+"),tip="+tip);					    	 
					 }						
				}
		});	        
	}
	
	
	// tau 08.11.2005
	private String[] saveElements() {
		// add tau 15.11.2005
		String [] expandedElements = null;
		int length = viewer.getExpandedElements().length;		
		if (length != 0) {
			expandedElements = new String [length];
		}
		//
		
		int j = 0;
		TreeItem[] items = viewer.getTree().getItems();
		for (int i = 0; i < items.length; i++) {
			TreeItem item = items[i];
			j = saveItem(item, expandedElements, j);
		}
//		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW)	ExceptionHandler.logInfo(".saveElements()= " + length + ",tip="+tip);		
		return expandedElements;
	}

	// tau 08.11.2005
	private int saveItem(TreeItem treeItem, String [] expandedElements, int j) {
//		if (treeItem.getExpanded()) {
//			if (treeItem.getData() instanceof IOrmElement ){
//				expandedElements[j++] = ((IOrmElement)treeItem.getData()).getQualifiedName(treeItem);
//				TreeItem[] items = treeItem.getItems();
//				for (int i = 0; i < items.length; i++) {
//					j = saveItem(items[i], expandedElements, j);				
//				}
//			} else {
//				return j;
//			}
//				
//		}
		return j;
	}
	
	private String saveSelection() {
		String selectionElementQualifiedName = "";	// default	
		ISelection selection = viewer.getSelection();
		if (!selection.isEmpty()) {
			TreeItem[] selectionTreeItems =  viewer.getTree().getSelection();
			if (selectionTreeItems.length != 0) {
				TreeItem selectionItem = selectionTreeItems[0]; 
				if (selection instanceof StructuredSelection) {
					// edit tau 05.04.2006 for /ESORM-562
					//IOrmElement selectionElement = (IOrmElement) ((StructuredSelection) selection).getFirstElement();
					Object selectionElement = ((StructuredSelection) selection).getFirstElement();
//					if (selectionElement instanceof IOrmElement) { 
//						IOrmElement selectionOrmElement = (IOrmElement) selectionElement;					
//						selectionElementQualifiedName = selectionOrmElement.getQualifiedName(selectionItem);
//					}
				}
			}
		}
//		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW)	ExceptionHandler.logInfo(".saveSelection()= " + selectionElementQualifiedName + ",tip="+tip);		
		return selectionElementQualifiedName;
	}
	
	private void restoreElements(String[] expandedElements, String selectionElementQualifiedName) {
		//viewer.getTree().setRedraw(false);		
		checkItem(viewer.getTree().getItems(), expandedElements, selectionElementQualifiedName);				
		//viewer.getTree().setRedraw(true);
		//if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW)	ExceptionHandler.logInfo(".restoreElements()="	+ expandedElements.length  + ", tip="+tip);		
	}
	
	// #changed# by Konstantin Mishin on 03.09.2005 fixed for ORMIISTUD-699
	private void checkItem(TreeItem[] items, String[] expandedElements,	String selectionElementQualifiedName) {
		for (int j = 0; j < items.length; j++) {
			TreeItem item = items[j];
			
			// add tau 07.03.2006
//			if (!(item.getData() instanceof IOrmElement)){
//				continue;
//			}
			
//			IOrmElement itemElement = (IOrmElement) item.getData();
//			String itemElementQualifiedName = itemElement.getQualifiedName(item);
//			if (itemElementQualifiedName.equals("")) continue;
//
//			if (selectionElementQualifiedName != null && selectionElementQualifiedName.equals(itemElementQualifiedName)) {
//					viewer.setSelection(new StructuredSelection(itemElement), true);
//					// add tau 28.11.2005
//					viewer.reveal(itemElement);
//			}
//
//			for (int i = 0; i < expandedElements.length; i++) {
//				if (itemElementQualifiedName.equals(expandedElements[i])) {
//					viewer.setExpandedState(itemElement, true);
//					TreeItem[] childrenItems = item.getItems();
//					checkItem(childrenItems, expandedElements, selectionElementQualifiedName);
//				}
//			}
		}
	}
	
	private boolean isBuilder(IProject project){
		boolean flag = false;
		if (project.getWorkspace().isAutoBuilding()) {
			IProjectDescription description;
//			try {
//				description = project.getProject().getDescription();
//				ICommand[] builderCommands = description.getBuildSpec();
//				flag = false;
//				for (int i = 0; i < builderCommands.length; i++){
//					if (builderCommands[i].getBuilderName().equals(OrmHibernateNature.ORM_HIBERNATE_BUILDER_ID)) {
//						flag = true;
//						if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW ) ExceptionHandler.logInfo("OrmContentProvider.isBuilder(...), tip="+tip + ",flagRefresh->"+flag);						
//						break;
//					}
//				}
//			} catch (CoreException e) {
//				ExceptionHandler.logThrowableError(e, "projectChanged");
//			}
		}
		return flag;
	}

}
