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
package org.hibernate.eclipse.jdt.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jdt.ui.JavaElementImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.jdt.ui.internal.JdtUiMessages;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo;

/**
 * @author Dmitry Geraskov
 *
 */
@SuppressWarnings("restriction")
public class NewHibernateMappingFilePage extends WizardPage {

	private TableViewer viewer;
	
	private boolean hideFilename;

	/**
	 * @param pageName
	 */
	protected NewHibernateMappingFilePage(boolean hideFilename) {
		super("");	//$NON-NLS-1$
		this.hideFilename = hideFilename;
	}

	public void createControl(Composite parent) {

		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.pack(false);

		Composite container = new Composite(sc, SWT.NULL);
		sc.setContent(container);

		Layout layout = new FillLayout();
		container.setLayout(layout);

		Table table =  new Table(container, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION );
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.pack(false);
		createTableColumns(table);
		viewer = createTableViewer(table);
		viewer.setInput(null);

		sc.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(container);
	}
	
	public void setInput(Map<IJavaProject, Collection<EntityInfo>> project_infos){
		viewer.setInput(project_infos);
		//Hide "project" column if only 1 project's CUs selected
		if (project_infos.size() == 1){
			viewer.getTable().getColumn(1).setWidth(0);
		} else {
			viewer.getTable().getColumn(1).setWidth(120);
			viewer.getTable().getColumn(1).pack();
		}
		viewer.getTable().getColumn(2).pack();
		if (!hideFilename){
			viewer.getTable().getColumn(3).pack();
		}
	}

	private void createTableColumns(Table table){
		int coulmnIndex = 0;
		TableColumn column = new TableColumn(table, SWT.CENTER, coulmnIndex++);
		column.setText("!"); //$NON-NLS-1$
		/* Hide the column while JBIDE-4269 is not implemented.
		 * (Filtering generated elements)
		 */
		//if (hideFilename) 
		column.setWidth(0);
		column.setResizable(false);

		column = new TableColumn(table, SWT.LEFT, coulmnIndex++);
		column.setText(JdtUiMessages.NewHibernateMappingFilePage_project_name_column);
		column.setWidth(120);

		column = new TableColumn(table, SWT.LEFT, coulmnIndex++);
		column.setText(JdtUiMessages.NewHibernateMappingFilePage_class_name_column);
		column.setWidth(200);

		if (!hideFilename) {
			column = new TableColumn(table, SWT.LEFT, coulmnIndex++);
			column.setText(JdtUiMessages.NewHibernateMappingFilePage_file_name_column);
			column.setWidth(150);
		}
	}

	private TableViewer createTableViewer(Table table) {
		TableViewer result = new TableViewer( table );
		result.setUseHashlookup( true );

		result.setColumnProperties( new String[] {Columns.CREATE.toString(), 
				Columns.PROJECT.toString(),	Columns.CLASS.toString(), Columns.FILE.toString()} ); 

		CellEditor[] editors = new CellEditor[result.getColumnProperties().length];
		int coulmnIndex = 0;
		editors[coulmnIndex++] = new CheckboxCellEditor( result.getTable() );
		editors[coulmnIndex++] = new TextCellEditor( result.getTable() );
		editors[coulmnIndex++] = new TextCellEditor( result.getTable() );
		if (!hideFilename) {
			editors[coulmnIndex++] = new TextCellEditor( result.getTable() );
		}

		result.setCellEditors( editors );
		result.setCellModifier( new TableCellModifier(result) );
		result.setLabelProvider(new TableLableProvider(result));
		result.setContentProvider( new TableContentProvider() );
		return result;
	}

	private class TableLine {

		public String projectName;

		public String className;

		public String fileName;
		
		public IProject  project;

		public Boolean isCreate = true;

		public TableLine(IProject iProject, String className){
			this(iProject, className, className + ".hbm.xml",true); //$NON-NLS-1$
		}

		public TableLine(IProject iProject, String className, String fileName, boolean isCreate){
			this.projectName = iProject.getName();
			this.className = className;
			this.fileName = fileName;
			this.isCreate = isCreate;
			this.project = iProject;
		}

	}
	
	private enum Columns {
		PROJECT,
		CLASS,
		FILE,
		CREATE
	}

	private class TableContentProvider implements IStructuredContentProvider {

		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Map) {
				List<TableLine> result = new ArrayList<TableLine>();
				Map<IJavaProject, Collection<EntityInfo>> configs = (Map<IJavaProject, Collection<EntityInfo>>)inputElement;
				for (Entry<IJavaProject, Collection<EntityInfo>> entry : configs.entrySet()) {
					Iterator<EntityInfo> iter = entry.getValue().iterator();
					while (iter.hasNext()) {
						EntityInfo ei = iter.next();
						result.add(new TableLine(entry.getKey().getProject(), ei.getName()));
					}
				}
				return result.toArray();
			}
			return new Object[0];
		}

		public void dispose() {}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {	}

	}

	private class TableLableProvider extends LabelProvider implements ITableLabelProvider  {

		private final TableViewer tv;
		
		private final Point IMAGE_SIZE = new Point(16, 16);
		
		private final ImageDescriptorRegistry registry = JavaPlugin.getImageDescriptorRegistry();

		private ImageDescriptor DESC_OBJ_PROJECT = null;
		
		public TableLableProvider(TableViewer tv) {
			this.tv = tv;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			String property = (String) tv.getColumnProperties()[columnIndex];
			TableLine tl = (TableLine) element;
			if(Columns.CREATE.toString().equals(property)) {				
				String key = tl.isCreate ? null : ImageConstants.CLOSE ; // TODO: find a better image
				return EclipseImages.getImage(key);
			} else if (Columns.CLASS.toString().equals(property)){
				return registry.get(
						new JavaElementImageDescriptor(JavaPluginImages.DESC_OBJS_CLASS, 0, IMAGE_SIZE));
			} else if (Columns.FILE.toString().equals(property)){
				IFile file = tl.project.getFile(tl.fileName);
				IWorkbenchAdapter wbAdapter= (IWorkbenchAdapter) file.getAdapter(IWorkbenchAdapter.class);
				if (wbAdapter != null) {
					ImageDescriptor descriptor= wbAdapter.getImageDescriptor(file);
					if (descriptor != null) {
						return registry.get(
								new JavaElementImageDescriptor(descriptor, 0, IMAGE_SIZE));
					}					
				}				
			} else if (Columns.PROJECT.toString().equals(property)){
				if (DESC_OBJ_PROJECT == null) {
					ISharedImages images= JavaPlugin.getDefault().getWorkbench().getSharedImages();
					DESC_OBJ_PROJECT= 		 images.getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT);
				}
				return registry.get(new JavaElementImageDescriptor(DESC_OBJ_PROJECT, 0, IMAGE_SIZE));
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			String property = (String) tv.getColumnProperties()[columnIndex];
			TableLine tl = (TableLine) element;

			if (Columns.CLASS.toString().equals(property)){
				return tl.className;
			} else if (Columns.PROJECT.toString().equals(property)){
				return tl.projectName;
			} else if (Columns.FILE.toString().equals(property)){
				return tl.fileName;
			} else {
				return "";//$NON-NLS-1$
			}
		}
	}

	private class TableCellModifier implements ICellModifier {

		private final TableViewer tv;

		public TableCellModifier(TableViewer tv) {
			this.tv = tv;
		}

		public boolean canModify(Object element, String property) {
			return false/*TODO "file".equals(property) || "create".equals(property)*/;
		}

		public Object getValue(Object element, String property) {
			if (Columns.CLASS.toString().equals(property)){
				return ((TableLine)element).className;
			} else if (Columns.PROJECT.toString().equals(property)){
				return ((TableLine)element).projectName;
			} else if (Columns.FILE.toString().equals(property)){
				return ((TableLine)element).fileName;
			} else if (Columns.CREATE.toString().equals(property)){
				return ((TableLine)element).isCreate;
			}
			return null;
		}

		public void modify(Object element, String property, Object value) {
			TableLine tl = (TableLine)((TableItem)element).getData();
			if (Columns.CLASS.toString().equals(property)){
				tl.className = (String)value;
			} else if (Columns.PROJECT.toString().equals(property)){
				tl.projectName = (String)value;
			} else if (Columns.FILE.toString().equals(property)){
				tl.fileName = (String)value;
			} else if (Columns.CREATE.toString().equals(property)){
				tl.isCreate = (Boolean)value;
			}

			tv.update(new Object[] { tl }, new String[] { property });
		}
	}
}
