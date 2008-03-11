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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.jboss.tools.hibernate.core.IDatabaseSchema;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IOrmProject;

/**
 * @author Tau from Minsk
 *
 */
public class ExplorerBase extends OrmExplorer {

	/**
	 * The constructor.
	 */
	public ExplorerBase() {
		super();
	}
	
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		// add 22.06.2005
		this.makeActions();
		contributeToActionBars();
		actionRootSchemaTableColumn.run();
		
	}	

	protected void fillContextMenu(IMenuManager manager) {
		//		 del tau 29.07.205		
		//manager.add(actionRootSchemaTableColumn);
		//manager.add(actionRootTableColumn);
		
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));		
	}

	protected void fillLocalToolBar(IToolBarManager manager) {
		//	del tau 29.07.205		
		//manager.add(actionRootSchemaTableColumn);
		// manager.add(actionRootTableColumn);
	}

	static public String getExplorerId() {
		return "org.jboss.tools.hibernate.view.views.ExplorerBase";
	}
	
	private void makeActions() {
		// ExplorerBase actions
		// *********
		actionRootSchemaTableColumn = new Action() {
			public void run() {
				viewerSetInput(ormModel, contentProvider_SCHEMA_TABLE_COLUMN_CONTENT_PROVIDER);
				//actionRootSchemaTableColumn.setChecked(true);
				//	del tau 29.07.205
				//actionRootTableColumn.setChecked(false);
				// setContentDescription(BUNDLE.getString("ExplorerBase.ActionRootSchemaTableColumnContentDescription"));
			}
		};
		//actionRootSchemaTableColumn.setToolTipText(BUNDLE.getString("ExplorerBase.ActionRootSchemaTableColumnToolTipText"));
		// actionRootPackageClassField.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		//actionRootSchemaTableColumn.setImageDescriptor(ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("ExplorerBase.RootSchemaTableColumn")));

		/*	del tau 29.07.205
		actionRootTableColumn = new Action(BUNDLE
				.getString("ExplorerBase.ActionRootTableColumnName"),
				Action.AS_CHECK_BOX) {
			public void run() {
				viewerSetInput(ormProjects, contentProvider_TABLE_COLUMN_CONTENT_PROVIDER);
				actionRootSchemaTableColumn.setChecked(false);
				actionRootTableColumn.setChecked(true);
				// setContentDescription(BUNDLE.getString("ExplorerBase.ActionRootTableColumnContentDescription"));
			}
		};
		actionRootTableColumn.setToolTipText(BUNDLE
				.getString("ExplorerBase.ActionRootTableColumnToolTipText"));
		actionRootTableColumn.setImageDescriptor(ViewPlugin
				.getImageDescriptor(BUNDLE_IMAGE
						.getString("ExplorerBase.RootTableColumn")));
		*/
	}	
	
	// edit tau 24.03.2006
	public void showOrmElement(IOrmProject project, IMapping mapping, IDatabaseSchema databaseSchema, IDatabaseTable table, boolean expand) {
		// edit tau 29.07.2005
		actionRootSchemaTableColumn.run();
		
		TreeViewer viewer = this.getTreeViewer();
		Tree tree = viewer.getTree();
				
		// find project
		TreeItem itemProject = getTreeItemElement(tree.getItems(), IOrmProject.class, project, viewer );
		if (itemProject == null) return;
		
		// find mapping		
		TreeItem itemMapping = getTreeItemElement(itemProject.getItems(), IMapping.class, mapping, viewer );
		if (itemMapping == null) return;		

		// find Schema
		TreeItem itemSchema = getTreeItemElement(itemMapping.getItems(), IDatabaseSchema.class, databaseSchema, viewer );
		if (itemSchema == null) return;
		
		// find table
		TreeItem itemDatabaseTable = getTreeItemElement(itemSchema.getItems(), IDatabaseTable.class, table, viewer );
		if (itemDatabaseTable == null) return;
		
		tree.setSelection(new TreeItem[]{itemDatabaseTable});

	}	

//	public void showOrmElement(IOrmProject project, IMapping mapping, IDatabaseSchema databaseSchema, IDatabaseTable table, boolean expand) {
//		// edit tau 29.07.2005
//		actionRootSchemaTableColumn.run();
//		
//		TreeViewer viewer = this.getTreeViewer();
//		Tree tree = viewer.getTree();
//		TreeItem[] treeItemOrmProjectArray = tree.getItems();
//		
//		// find project
//		IOrmProject explorerBaseProject = null;
//		TreeItem itemProject = null;		
//		for (int i = 0; i < treeItemOrmProjectArray.length; i++) {
//			itemProject = treeItemOrmProjectArray[i];
//			if (itemProject.getData() instanceof IOrmProject){
//				explorerBaseProject = (IOrmProject) itemProject.getData();
//				if (explorerBaseProject == project) break;
//			}
//		}
//		if (explorerBaseProject == null) return;
//		
//		// find mapping
//		if (!itemProject.getExpanded()) {
//			viewer.expandToLevel(explorerBaseProject,1);
//		}
//		TreeItem[] itemMappingArray = itemProject.getItems();
//		IMapping explorerBaseMapping = null; 		
//		TreeItem itemMapping = null;		
//		for (int i = 0; i < itemMappingArray.length; i++) {
//			itemMapping = itemMappingArray[i];
//			if (itemMapping.getData() instanceof IMapping){
//				explorerBaseMapping = (IMapping) itemMapping.getData();
//				if (explorerBaseMapping == mapping) break;
//			}
//		}
//		if (explorerBaseMapping == null) return;
//		
//		// find Schema add tau 29.07.2005
//		if (!itemMapping.getExpanded()) {
//			viewer.expandToLevel(explorerBaseMapping,1);
//		}
//		TreeItem[] itemSchemaArray = itemMapping.getItems();
//		IDatabaseSchema explorerBaseSchema = null; 		
//		TreeItem itemSchema = null;		
//		for (int i = 0; i < itemSchemaArray.length; i++) {
//			itemSchema = itemSchemaArray[i];
//			if (itemSchema.getData() instanceof IDatabaseSchema){
//				explorerBaseSchema = (IDatabaseSchema) itemSchema.getData();
//				if (explorerBaseSchema == databaseSchema) break;
//			}
//		}
//		if (explorerBaseSchema == null) return;		
//		
//		
//		// find table
//		if (!itemSchema.getExpanded()) {
//			viewer.expandToLevel(explorerBaseSchema,1);
//		}
//		TreeItem[] itemDatabaseTableArray = itemSchema.getItems();
//		IDatabaseTable explorerBaseDatabaseTable = null; 		
//		TreeItem itemDatabaseTable = null;		
//		for (int i = 0; i < itemDatabaseTableArray.length; i++) {
//			itemDatabaseTable = itemDatabaseTableArray[i];
//			if (itemDatabaseTable.getData() instanceof IDatabaseTable){
//				explorerBaseDatabaseTable = (IDatabaseTable) itemDatabaseTable.getData();
//				if (explorerBaseDatabaseTable == table) break;
//			}
//		}
//		if (explorerBaseDatabaseTable == null) return;
//		
//		// expand?
//		if (expand){
//			viewer.expandToLevel(explorerBaseDatabaseTable,1);
//			tree.setSelection( new TreeItem[]{itemDatabaseTable});
//		}
//	}
	
	//edit tau 02.04.2006
	private TreeItem getTreeItemElement(TreeItem[] items, Class clazz, Object element, TreeViewer viewer) {
		Object ormElement = null;
		TreeItem ormItemReturn = null;		
		for (int i = 0; i < items.length; i++) {
			TreeItem ormItem = items[i];
			if (clazz.isInstance(ormItem.getData())) {
				ormElement = ormItem.getData();
				if (ormElement == element){
					if (!ormItem.getExpanded()) {
						viewer.expandToLevel(ormElement,1);
					}
					ormItemReturn = ormItem; 					
					break;
				}
			}
		}
		return ormItemReturn;
	}
}