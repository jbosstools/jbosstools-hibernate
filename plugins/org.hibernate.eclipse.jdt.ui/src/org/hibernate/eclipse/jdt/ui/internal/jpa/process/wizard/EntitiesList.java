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
package org.hibernate.eclipse.jdt.ui.internal.jpa.process.wizard;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.hibernate.eclipse.jdt.ui.internal.JdtUiMessages;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.process.AnnotStyle;

/**
 * Entities list wizard page
 *
 * @author Vitali
 */
@SuppressWarnings("restriction")
public class EntitiesList extends UserInputWizardPage {

	private enum Columns {
		PROJECT,
		CLASS,
	}
	
	protected TableViewer listViewer;
	
	protected IHibernateJPAWizardData data;

	protected IHibernateJPAWizardParams params;

	public EntitiesList(String name, IHibernateJPAWizardData data, IHibernateJPAWizardParams params) {
		super(name);
		this.data = data;
		this.params = params;
		setDescription(JdtUiMessages.EntitiesList_description);
	}
	
	public IStructuredContentProvider createContentProvider(final IHibernateJPAWizardData data) {
		return new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				return data.getEntities().values().toArray();
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		};
	}
	
	public void setData(final IHibernateJPAWizardData data) {
		this.data = data;
		if (listViewer != null) {
			listViewer.setContentProvider(createContentProvider(data));
			listViewer.setInput(data.getEntities());
			//
			for (int i = 0; i < listViewer.getTable().getColumnCount(); i++) {
				String property = (String)listViewer.getColumnProperties()[i];
				if (Columns.PROJECT.toString().equals(property)) {
					listViewer.getTable().getColumn(i).setWidth(isOneProject() ? 0 : 200);
					listViewer.getTable().getColumn(i).setResizable(!isOneProject());
					listViewer.getTable().getColumn(i).pack();
					break;
				}
			}
		}
	}
	
	public boolean isOneProject() {
		Map<String, EntityInfo> mapEntities = data.getEntities();
		Iterator<EntityInfo> it = mapEntities.values().iterator();
		boolean res = true;
		String javaProjectName = null;
		while (it.hasNext()) {
			EntityInfo ei = it.next();
			if (javaProjectName != null && !javaProjectName.equalsIgnoreCase(ei.getJavaProjectName())) {
				res = false;
				break;
			}
			javaProjectName = ei.getJavaProjectName();
		}
		return res;
	}
	
	public void createControl(Composite parent) {
	    initializeDialogUnits(parent);
		Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 1;
        //Label label = new Label(container, SWT.NULL);
        //label.setText(JdtUiMessages.AllEntitiesProcessor_message);

        listViewer = new TableViewer(container, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);
		//listViewer.setComparator(getViewerComparator());
		Control control = listViewer.getControl();
		GridData gridData = new GridData(GridData.FILL_BOTH
				| GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		gridData.heightHint = convertHeightInCharsToPixels(10);
		control.setLayoutData(gridData);
		String[] columnProperties = new String[] {
			Columns.CLASS.toString(), Columns.PROJECT.toString(),
		};
		listViewer.setColumnProperties(columnProperties); 
		listViewer.setContentProvider(createContentProvider(data));
		listViewer.setLabelProvider(new TableLableProvider(listViewer));
		createTableColumns(listViewer.getTable());
		listViewer.setInput(data.getEntities());
        listViewer.getTable().setHeaderVisible(true);
		listViewer.getTable().setLinesVisible(true);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL)
			.grab(true, true)
			.hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH),
				convertHorizontalDLUsToPixels(2 * IDialogConstants.BUTTON_BAR_HEIGHT)).applyTo(listViewer.getControl());
		//Button generateChoice = new Button(container, SWT.CHECK);
		//generateChoice.setText("fdwsdfv");
		Composite combolabel = new Composite(container, SWT.NULL);
        layout = new GridLayout();
        combolabel.setLayout(layout);
        layout.numColumns = 2;
		Label labelChoice = new Label(combolabel, SWT.NULL);
		labelChoice.setText(JdtUiMessages.AllEntitiesProcessor_preferred_location_annotations);
		Combo generateChoice = new Combo(combolabel, SWT.READ_ONLY);
		generateChoice.add(JdtUiMessages.AllEntitiesProcessor_fields);
		generateChoice.add(JdtUiMessages.AllEntitiesProcessor_getters);
		generateChoice.add(JdtUiMessages.AllEntitiesProcessor_auto_select_from_class_preference);
		int idx = 0;
		if (params.getAnnotationStyle().equals(AnnotStyle.FIELDS)) {
			idx = 0;
		} else if (params.getAnnotationStyle().equals(AnnotStyle.GETTERS)) {
			idx = 1;
		} else if (params.getAnnotationStyle().equals(AnnotStyle.AUTO)) {
			idx = 2;
		}
		generateChoice.select(idx);
		final ModifyListener mlGenerateChoice = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				int idx = ((Combo)e.getSource()).getSelectionIndex();
				if (idx == 0 && !params.getAnnotationStyle().equals(AnnotStyle.FIELDS)) {
					params.setAnnotationStyle(AnnotStyle.FIELDS);
					params.reCollectModification(data.getEntities());
				}
				else if (idx == 1 && !params.getAnnotationStyle().equals(AnnotStyle.GETTERS)) {
					params.setAnnotationStyle(AnnotStyle.GETTERS);
					params.reCollectModification(data.getEntities());
				}
				else if (idx == 2 && !params.getAnnotationStyle().equals(AnnotStyle.AUTO)) {
					params.setAnnotationStyle(params.getAnnotationStylePreference());
					params.reCollectModification(data.getEntities());
					params.setAnnotationStyle(AnnotStyle.AUTO);
				}
			}
			
		};
		generateChoice.addModifyListener(mlGenerateChoice);

		Label labelDefaultStrLength = new Label(combolabel, SWT.NULL);
		labelDefaultStrLength.setText(JdtUiMessages.AllEntitiesProcessor_default_string_length);
		Text textDefaultStrLength = new Text(combolabel, SWT.SINGLE | SWT.BORDER | SWT.TRAIL);
		textDefaultStrLength.setText(String.valueOf(params.getDefaultStrLength()));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL)
			.grab(true, true).applyTo(textDefaultStrLength);
		textDefaultStrLength.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				for (int i = 0; i < e.text.length(); i++) {
					char val = e.text.charAt(i);
					if (!('0' <= val && val <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		});
		final ModifyListener mlDefaultStrLength = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if (e == null || !(e.getSource() instanceof Text)) {
					return;
				}
				String str = ((Text)e.getSource()).getText();
				Integer val = IHibernateJPAWizardParams.columnLength;
				if (str != null && str.length() > 0) {
					val = Integer.valueOf(str);
				}
				params.setDefaultStrLength(val);
				params.reCollectModification(data.getEntities());
			}
			
		};
		textDefaultStrLength.addModifyListener(mlDefaultStrLength);
		
		// enable optimistic locking functionality
		Label labelOptLock = new Label(combolabel, SWT.NULL);
		labelOptLock.setText(JdtUiMessages.AllEntitiesProcessor_enable_optimistic_locking);
		Button checkboxOptLock = new Button(combolabel, SWT.CHECK);
		checkboxOptLock.setSelection(params.getEnableOptLock());
		final Listener mlOptLock = new Listener() {

			public void handleEvent(Event e) {
				params.setEnableOptLock(((Button)e.widget).getSelection());
				params.reCollectModification(data.getEntities());
			}
			
		};
		checkboxOptLock.addListener(SWT.Selection, mlOptLock);
		
		setControl(container);
	}

	protected void createTableColumns(Table table) {
		
		TableColumn column = null;
		int i = 0;
		
		column = new TableColumn(table, SWT.LEFT, i++);
		column.setText(JdtUiMessages.ResolveAmbiguous_column_Class);
		column.setWidth(200);
		
		column = new TableColumn(table, SWT.LEFT, i++);
		column.setText(JdtUiMessages.NewHibernateMappingFilePage_project_name_column);
		column.setWidth(isOneProject() ? 0: 200);
		column.setResizable(!isOneProject());
	}

	protected class TableLableProvider extends LabelProvider implements ITableLabelProvider  {

		protected final TableViewer tv;
		
		protected Image classImage = JavaElementImageProvider.getTypeImageDescriptor(false, false, 0, false).createImage();
		
		public TableLableProvider(TableViewer tv) {
			this.tv = tv;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			String property = (String) tv.getColumnProperties()[columnIndex];
			if (Columns.CLASS.toString().equals(property)) {
				return classImage;
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			String property = (String) tv.getColumnProperties()[columnIndex];
			EntityInfo info = (EntityInfo) element;
			if (Columns.CLASS.toString().equals(property)) {
				return info.getFullyQualifiedName();
			} else if (Columns.PROJECT.toString().equals(property)) {
				return info.getJavaProjectName();
			}
			return "";//$NON-NLS-1$
		}

		@Override
		public void dispose() {
			classImage.dispose();
			super.dispose();
		}
	}
}
