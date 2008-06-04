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
import org.jboss.tools.hibernate.ui.view.ViewPlugin;


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
		sorting = true;
	}

	public Object[] getChildren(Object parentElement) {
		if (children == null) 
			children = nullChildren;

		if (!sorting){
			sorting = true;
		}
		return children;
	}
	
	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
			return true;		
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public int getTip() {
		return tip;
	}

	public void setSorting(boolean sorting) {
		this.sorting = sorting;
	}

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
		Control control = viewer.getControl();
			
		if (control == null || control.isDisposed()) {
			return;			
		}
			
		Display display = control.getDisplay();
		display.asyncExec(new Runnable() {
		
				public void run() {
					try {					
						if (!viewer.getControl().isDisposed()) {
							viewer.getTree().setRedraw(false);
							if (beforeChangeElements == null){
								beforeChangeElements = saveElements();	
								beforeChangeSelectionElementQualifiedName = saveSelection();
							}
							
							if (flagUpdate) {
								viewer.update(element, null);								
							} else {
								viewer.refresh(element, true);								
							}
							
							if (beforeChangeElements != null){
								restoreElements(beforeChangeElements, beforeChangeSelectionElementQualifiedName);
								beforeChangeElements = null;						
							}
							
							viewer.getTree().setRedraw(true);					
						}
				     } catch (Exception e) {
				    	 ViewPlugin.getDefault().logError(e);
					}						
				}
		});	        
	}
	
	
	private String[] saveElements() {
		String [] expandedElements = null;
		int length = viewer.getExpandedElements().length;		
		if (length != 0) {
			expandedElements = new String [length];
		}
		
		int j = 0;
		TreeItem[] items = viewer.getTree().getItems();
		for (int i = 0; i < items.length; i++) {
			TreeItem item = items[i];
			j = saveItem(item, expandedElements, j);
		}
		return expandedElements;
	}

	private int saveItem(TreeItem treeItem, String [] expandedElements, int j) {
		return j;
	}
	
	private String saveSelection() {
		String selectionElementQualifiedName = "";	 //$NON-NLS-1$
		ISelection selection = viewer.getSelection();
		if (!selection.isEmpty()) {
			TreeItem[] selectionTreeItems =  viewer.getTree().getSelection();
			if (selectionTreeItems.length != 0) {
				TreeItem selectionItem = selectionTreeItems[0]; 
				if (selection instanceof StructuredSelection) {
					Object selectionElement = ((StructuredSelection) selection).getFirstElement();
				}
			}
		}
		return selectionElementQualifiedName;
	}
	
	private void restoreElements(String[] expandedElements, String selectionElementQualifiedName) {
		checkItem(viewer.getTree().getItems(), expandedElements, selectionElementQualifiedName);				
	}
	
	private void checkItem(TreeItem[] items, String[] expandedElements,	String selectionElementQualifiedName) {
		for (int j = 0; j < items.length; j++) {
			TreeItem item = items[j];
		}
	}
	
	private boolean isBuilder(IProject project){
		boolean flag = false;
		if (project.getWorkspace().isAutoBuilding()) {
			IProjectDescription description;
		}
		return flag;
	}

}
