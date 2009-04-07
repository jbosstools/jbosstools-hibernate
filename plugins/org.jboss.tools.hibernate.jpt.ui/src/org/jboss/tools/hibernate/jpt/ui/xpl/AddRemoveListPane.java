/*******************************************************************************
 * Copyright (c) 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *     Dmitry Geraskov, Exadel Inc. - Extracted from Dali 2.0 to protect from changes.
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.xpl;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jpt.ui.internal.listeners.SWTListChangeListenerWrapper;
import org.eclipse.jpt.ui.internal.listeners.SWTPropertyChangeListenerWrapper;
import org.eclipse.jpt.ui.internal.swt.ColumnAdapter;
import org.eclipse.jpt.ui.internal.swt.TableModelAdapter;
import org.eclipse.jpt.ui.internal.swt.TableModelAdapter.SelectionChangeEvent;
import org.eclipse.jpt.ui.internal.swt.TableModelAdapter.SelectionChangeListener;
import org.eclipse.jpt.utility.internal.CollectionTools;
import org.eclipse.jpt.utility.internal.model.value.SimplePropertyValueModel;
import org.eclipse.jpt.utility.internal.model.value.swing.ObjectListSelectionModel;
import org.eclipse.jpt.utility.model.Model;
import org.eclipse.jpt.utility.model.event.ListChangeEvent;
import org.eclipse.jpt.utility.model.event.PropertyChangeEvent;
import org.eclipse.jpt.utility.model.listener.ListChangeListener;
import org.eclipse.jpt.utility.model.listener.PropertyChangeListener;
import org.eclipse.jpt.utility.model.value.ListValueModel;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.utility.model.value.WritablePropertyValueModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This implementation of the <code>AddRemovePane</code> uses a <code>Table</code>
 * as its main widget, a <code>List</code> can't be used because it doesn't
 * support showing images. However, the table is displayed like a list.
 * <p>
 * Here the layot of this pane:
 * <pre>
 * -----------------------------------------------------------------------------
 * | ------------------------------------------------------------- ----------- |
 * | | Item 1                                                    | | Add...  | |
 * | | ...                                                       | ----------- |
 * | | Item n                                                    | ----------- |
 * | |                                                           | | Edit... | |
 * | |                                                           | ----------- |
 * | |                                                           | ----------- |
 * | |                                                           | | Remove  | |
 * | |                                                           | ----------- |
 * | -------------------------------------------------------------             |
 * -----------------------------------------------------------------------------</pre>
 *
 * @version 2.0
 * @since 1.0
 */
@SuppressWarnings("nls")
public class AddRemoveListPane<T extends Model> extends AddRemovePane<T>
{
	/**
	 * Flag used to prevent circular
	 */
	private boolean locked;

	/**
	 * The main widget of this add/remove pane.
	 */
	private Table table;

	/**
	 * Creates a new <code>AddRemoveListPane</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param parent The parent container
	 * @param adapter
	 * @param listHolder The <code>ListValueModel</code> containing the items
	 * @param selectedItemHolder The holder of the selected item, if more than
	 * one item or no items are selected, then <code>null</code> will be passed
	 * @param labelProvider The renderer used to format the table holder's items
	 */
	public AddRemoveListPane(AbstractPane<? extends T> parentPane,
	                         Composite parent,
	                         Adapter adapter,
	                         ListValueModel<?> listHolder,
	                         WritablePropertyValueModel<?> selectedItemHolder,
	                         ILabelProvider labelProvider) {

		super(parentPane,
		      parent,
		      adapter,
		      listHolder,
		      selectedItemHolder,
		      labelProvider);
	}

	/**
	 * Creates a new <code>AddRemoveListPane</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param parent The parent container
	 * @param adapter
	 * @param listHolder The <code>ListValueModel</code> containing the items
	 * @param selectedItemHolder The holder of the selected item, if more than
	 * one item or no items are selected, then <code>null</code> will be passed
	 * @param labelProvider The renderer used to format the table holder's items
	 * @param helpId The topic help ID to be registered with this pane
	 */
	public AddRemoveListPane(AbstractPane<? extends T> parentPane,
	                         Composite parent,
	                         Adapter adapter,
	                         ListValueModel<?> listHolder,
	                         WritablePropertyValueModel<?> selectedItemHolder,
	                         ILabelProvider labelProvider,
	                         String helpId) {

		super(parentPane,
		      parent,
		      adapter,
		      listHolder,
		      selectedItemHolder,
		      labelProvider,
		      helpId);
	}

	/**
	 * Creates a new <code>AddRemoveListPane</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param subjectHolder The holder of the subject
	 * @param adapter
	 * @param parent The parent container
	 * @param listHolder The <code>ListValueModel</code> containing the items
	 * @param selectedItemHolder The holder of the selected item, if more than
	 * one item or no items are selected, then <code>null</code> will be passed
	 * @param labelProvider The renderer used to format the table holder's items
	 */
	public AddRemoveListPane(AbstractPane<?> parentPane,
	                         PropertyValueModel<? extends T> subjectHolder,
	                         Composite parent,
	                         Adapter adapter,
	                         ListValueModel<?> listHolder,
	                         WritablePropertyValueModel<?> selectedItemHolder,
	                         ILabelProvider labelProvider) {

		super(parentPane,
		      subjectHolder,
		      parent,
		      adapter,
		      listHolder,
		      selectedItemHolder,
		      labelProvider);
	}

	/**
	 * Creates a new <code>AddRemoveListPane</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param subjectHolder The holder of the subject
	 * @param adapter
	 * @param parent The parent container
	 * @param listHolder The <code>ListValueModel</code> containing the items
	 * @param selectedItemHolder The holder of the selected item, if more than
	 * one item or no items are selected, then <code>null</code> will be passed
	 * @param labelProvider The renderer used to format the table holder's items
	 * @param helpId The topic help ID to be registered with this pane
	 */
	public AddRemoveListPane(AbstractPane<?> parentPane,
	                         PropertyValueModel<? extends T> subjectHolder,
	                         Composite parent,
	                         Adapter adapter,
	                         ListValueModel<?> listHolder,
	                         WritablePropertyValueModel<?> selectedItemHolder,
	                         ILabelProvider labelProvider,
	                         String helpId) {

		super(parentPane,
		      subjectHolder,
		      parent,
		      adapter,
		      listHolder,
		      selectedItemHolder,
		      labelProvider,
		      helpId);
	}

	private ColumnAdapter<Object> buildColumnAdapter() {
		return new ColumnAdapter<Object>() {
			public WritablePropertyValueModel<?>[] cellModels(Object subject) {
				WritablePropertyValueModel<?>[] valueHolders = new WritablePropertyValueModel<?>[1];
				valueHolders[0] = new SimplePropertyValueModel<Object>(subject);
				return valueHolders;
			}

			public int columnCount() {
				return 1;
			}

			public String columnName(int columnIndex) {
				return "";
			}
		};
	}

	private ListChangeListener buildListChangeListener() {
		return new SWTListChangeListenerWrapper(buildListChangeListener_());
	}

	private ListChangeListener buildListChangeListener_() {
		return new ListChangeListener() {
			public void itemsAdded(ListChangeEvent e) {
				if (!table.isDisposed()) {
					table.getParent().layout();
				}
			}

			public void itemsMoved(ListChangeEvent e) {
				if (!table.isDisposed()) {
					table.getParent().layout();
				}
			}

			public void itemsRemoved(ListChangeEvent e) {
				if (!table.isDisposed()) {
					table.getParent().layout();
				}
			}

			public void itemsReplaced(ListChangeEvent e) {
				if (!table.isDisposed()) {
					table.getParent().layout();
				}
			}

			public void listChanged(ListChangeEvent e) {
				if (!table.isDisposed()) {
					table.getParent().layout();
				}
			}

			public void listCleared(ListChangeEvent e) {
				if (!table.isDisposed()) {
					table.getParent().layout();
				}
			}
		};
	}

	private SimplePropertyValueModel<Object> buildSelectedItemHolder() {
		return new SimplePropertyValueModel<Object>();
	}

	private PropertyChangeListener buildSelectedItemPropertyChangeListener() {
		return new SWTPropertyChangeListenerWrapper(
			buildSelectedItemPropertyChangeListener_()
		);
	}

	private PropertyChangeListener buildSelectedItemPropertyChangeListener_() {
		return new PropertyChangeListener() {
			public void propertyChanged(PropertyChangeEvent e) {
				if (table.isDisposed()) {
					return;
				}

				if (!locked) {
					locked = true;

					try {
						Object value = e.getNewValue();
						getSelectionModel().setSelectedValue(e.getNewValue());
						int index = -1;

						if (value != null) {
							index = CollectionTools.indexOf(getListHolder().iterator(), value);
						}

						table.select(index);
						updateButtons();
					}
					finally {
						locked = false;
					}
				}
			}
		};
	}

	private SelectionChangeListener<Object> buildSelectionListener() {
		return new SelectionChangeListener<Object>() {
			public void selectionChanged(SelectionChangeEvent<Object> e) {
				AddRemoveListPane.this.selectionChanged();
			}
		};
	}

	private Composite buildTableContainer(Composite container) {

		container = buildPane(container, buildTableContainerLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		return container;
	}

	private Layout buildTableContainerLayout() {
		return new Layout() {
			@Override
			protected Point computeSize(Composite composite,
			                            int widthHint,
			                            int heightHint,
			                            boolean flushCache) {

				Table table = (Table) composite.getChildren()[0];
				packColumn(table);

				// Calculate the table size and adjust it with the hints
				Point size = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);

				if (widthHint != SWT.DEFAULT) {
					size.x = widthHint;
				}

				if (heightHint != SWT.DEFAULT) {
					size.y = heightHint;
				}

				return size;
			}

			private boolean isVerticalScrollbarBarVisible(Table table,
			                                              Rectangle clientArea) {

				// Get the height of all the rows
				int height = table.getItemCount() * table.getItemHeight();

				// Remove the border from the height
				height += (table.getBorderWidth() * 2);

				return (clientArea.height < height);
			}

			@Override
			protected void layout(Composite composite, boolean flushCache) {

				Rectangle bounds = composite.getClientArea();

				if (bounds.width > 0) {

					Table table = (Table) composite.getChildren()[0];
					table.setBounds(0, 0, bounds.width, bounds.height);

					updateTableColumnWidth(
						table,
						bounds.width,
						isVerticalScrollbarBarVisible(table, bounds)
					);
				}
			}

			private void packColumn(Table table) {

				TableColumn tableColumn = table.getColumn(0);

				table.setRedraw(false);
				table.setLayoutDeferred(true);
				tableColumn.pack();
				table.setLayoutDeferred(false);
				table.setRedraw(true);

				// Cache the column width so it can be used in
				// updateTableColumnWidth() when determine which width to use
				table.setData(
					"column.width",
					Integer.valueOf(tableColumn.getWidth())
				);
			}

			private void updateTableColumnWidth(Table table,
			                                    int width,
			                                    boolean verticalScrollbarBarVisible) {

				// Remove the border from the width
				width -= (table.getBorderWidth() * 2);

				// Remove the scrollbar from the width if it is shown
				if (verticalScrollbarBarVisible) {
					width -= table.getVerticalBar().getSize().x;
				}

				TableColumn tableColumn = table.getColumn(0);

				// Retrieve the cached column width, which is required for
				// determining which width to use (the column width or the
				// calculated width)
				Integer columnWitdh = (Integer) table.getData("column.width");

				// Use the calculated width if the column is smaller, otherwise
				// use the column width and a horizontal scroll bar will show up
				width = Math.max(width, columnWitdh);

				// Adjust the column width
				tableColumn.setWidth(width);
			}
		};
	}

	private ITableLabelProvider buiTableLabelProvider(IBaseLabelProvider labelProvider) {
		return new TableLabelProvider((ILabelProvider) labelProvider);
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public Table getMainControl() {
		return table;
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void initializeMainComposite(Composite container,
	                                       Adapter adapter,
	                                       ListValueModel<?> listHolder,
	                                       WritablePropertyValueModel<?> selectedItemHolder,
	                                       IBaseLabelProvider labelProvider,
	                                       String helpId) {

		table = buildTable(
			buildTableContainer(container),
			SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI,
			helpId
		);

		removeFromEnablementControl(table);

		TableModelAdapter model = TableModelAdapter.adapt(
			(ListValueModel<Object>) listHolder,
			buildSelectedItemHolder(),
			table,
			buildColumnAdapter(),
			buiTableLabelProvider(labelProvider)
		);

		model.addSelectionChangeListener(buildSelectionListener());

		selectedItemHolder.addPropertyChangeListener(
			PropertyValueModel.VALUE,
			buildSelectedItemPropertyChangeListener()
		);

		listHolder.addListChangeListener(
			ListValueModel.LIST_VALUES,
			buildListChangeListener()
		);

		initializeTable(table);
	}

	/**
	 * Initializes the given table, which acts like a list in our case.
	 *
	 * @param table The main widget of this pane
	 */
	protected void initializeTable(Table table) {

		table.setData("column.width", Integer.valueOf(0));
		table.setHeaderVisible(false);
		table.setLinesVisible(false);
	}

	/**
	 * The selection has changed, update (1) the selected item holder, (2) the
	 * selection model and (3) the buttons.
	 */
	private void selectionChanged() {

		if (locked) {
			return;
		}

		locked = true;

		try {
			WritablePropertyValueModel<Object> selectedItemHolder = getSelectedItemHolder();
			ObjectListSelectionModel selectionModel = getSelectionModel();
			int selectionCount = table.getSelectionCount();

			if (selectionCount == 0) {
				selectedItemHolder.setValue(null);
				selectionModel.clearSelection();
			}
			else if (selectionCount != 1) {
				selectedItemHolder.setValue(null);
				selectionModel.clearSelection();

				for (int index : table.getSelectionIndices()) {
					selectionModel.addSelectionInterval(index, index);
				}
			}
			else {
				int selectedIndex = table.getSelectionIndex();
				Object selectedItem = getListHolder().get(selectedIndex);

				selectedItemHolder.setValue(selectedItem);
				selectionModel.setSelectedValue(selectedItem);
			}

			updateButtons();
		}
		finally {
			locked = false;
		}
	}

	/**
	 * This label provider simply delegates the rendering to the provided
	 * <code>ILabelProvider</code>.
	 */
	private class TableLabelProvider extends LabelProvider
	                                 implements ITableLabelProvider {

		private ILabelProvider labelProvider;

		TableLabelProvider(ILabelProvider labelProvider) {
			super();
			this.labelProvider = labelProvider;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return labelProvider.getImage(element);
		}

		public String getColumnText(Object element, int columnIndex) {
			return labelProvider.getText(element);
		}
	}
}