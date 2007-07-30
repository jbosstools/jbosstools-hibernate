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
package org.hibernate.eclipse.console.wizards;

import java.util.Iterator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class UpDownListComposite extends Composite {

	private SelectionListener buttonListener= new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			handleButtonPressed( (Button) e.widget);
		}
	};
	
	private Group group = null;
	private Table table = null;
	private Composite buttons = null;
	private Button removeButton = null;
	private Button upButton = null;
	private Button downButton = null;
	private Label fillLabel = null;
	private Button[] addButtons;

	private TableViewer tableView;
	private ILabelProvider provider = null;
	private final String title;
	

	public UpDownListComposite(Composite parent, int style) {
		this( parent, style, "");		
	}
	
	public UpDownListComposite(Composite parent, int style, String title) {
		this( parent, style, title, null);
	}

	public UpDownListComposite(Composite parent, int style, String title, ILabelProvider provider) {
		super( parent, style );
		this.title = title;
		this.provider = provider;
		initialize();
	}

	private void initialize() {
		createGroup();
		GridLayout gridLayout = new GridLayout();		
		this.setLayout(gridLayout);
	}

	/**
	 * This method initializes group	
	 *
	 */
	private void createGroup() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		group = new Group( this, SWT.NONE );
		group.setText(title);
		createTable();
		group.setLayoutData(gridData);
		group.setLayout(gridLayout);
		createButtons();
	}

	/**
	 * This method initializes table	
	 *
	 */
	private void createTable() {
		GridData gridData1 = new org.eclipse.swt.layout.GridData();
		gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData1.heightHint = 20;
		gridData1.widthHint = 20;
		
		table = new Table(group, SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI );
		table.setHeaderVisible(false);
		table.setLayoutData(gridData1);
		table.setLinesVisible(false);
		createColumns(table);
		
		table.addSelectionListener(new SelectionListener() {
		
			public void widgetDefaultSelected(SelectionEvent e) {
				handleTableSelectionChanged();		
			}
		
			public void widgetSelected(SelectionEvent e) {
				handleTableSelectionChanged();		
			}
		
		});
		
		tableView = new TableViewer(table);
		if(provider!=null) tableView.setLabelProvider(provider);
		
	}

	protected void createColumns(Table table) {
		/*TableColumn column = new TableColumn(table, SWT.NULL);
		column.setText("NXame");
		column.setWidth(10);*/
	}

	/**
	 * This method initializes buttons	
	 *
	 */
	private void createButtons() {
		GridData gridData6 = new org.eclipse.swt.layout.GridData();
		gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData6.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		GridData gridData5 = new org.eclipse.swt.layout.GridData();
		gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData5.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		GridData gridData4 = new org.eclipse.swt.layout.GridData();
		gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData4.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		GridData gridData3 = new org.eclipse.swt.layout.GridData();
		gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData3.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		GridData gridData2 = new org.eclipse.swt.layout.GridData();
		gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData2.grabExcessHorizontalSpace = false;
		gridData2.grabExcessVerticalSpace = false;
		gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		buttons = new Composite( group, SWT.NONE );
		buttons.setLayout(new GridLayout());
		buttons.setLayoutData(gridData2);
		
		String[] addButtonLabels = getAddButtonLabels();
		addButtons = new Button[addButtonLabels.length];
		for (int i = 0; i < addButtonLabels.length; i++) {
			String label = addButtonLabels[i];
			addButtons[i] = createButton(buttons, label); 
			addButtons[i].setEnabled(true);
		}
		removeButton = new Button(buttons, SWT.NONE);
		removeButton.setText("Remove");
		removeButton.setLayoutData(gridData3);
		removeButton.addSelectionListener(buttonListener);
		fillLabel = new Label(buttons, SWT.NONE);
		fillLabel.setText("");
		fillLabel.setLayoutData(gridData6);
		upButton = new Button(buttons, SWT.NONE);
		upButton.setText("Up");
		upButton.setLayoutData(gridData4);
		upButton.addSelectionListener(buttonListener);
		downButton = new Button(buttons, SWT.NONE);
		downButton.setText("Down");
		downButton.setLayoutData(gridData5);
		downButton.addSelectionListener(buttonListener);
	}

	protected String[] getAddButtonLabels() {		
		return new String[] { "Add..." };
	}

	private Button createButton(Composite parent, String label) {
		Button button = new Button(parent, SWT.PUSH);
		GridData data = new GridData();
		//data.grabExcessHorizontalSpace = true;
		//data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		//data.verticalAlignment = GridData.FILL;
		
		button.setLayoutData(data);
		button.setFont(parent.getFont() );
		button.setText(label);
		button.setEnabled(false);
		button.addSelectionListener(buttonListener);
		return button;
	}

	private void handleButtonPressed(Button button) {
		if (button == removeButton) {
			handleRemoveButtonPressed(tableView);
		} else if (button == upButton) {
			moveSelectionUp(tableView);
		} else if (button == downButton) {
			moveSelectionDown(tableView);
		} else {
			for (int i = 0; i < addButtons.length; i++) {
				Button but = addButtons[i];
				if(button == but) {
				 handleAddButtonPressed(i);
				}				
			}						 
		}
		handleTableSelectionChanged();
		tableView.getTable().setFocus();
		
	}

	private void moveSelectionDown(TableViewer viewer) {
		Table table = viewer.getTable();
		int indices[]= table.getSelectionIndices();
		if (indices.length < 1) {
			return;
		}
		int newSelection[]= new int[indices.length];
		int max= table.getItemCount() - 1;
		for (int i = indices.length - 1; i >= 0; i--) {
			int index= indices[i];
			if (index < max) {
				move (viewer, table.getItem(index), index + 1);
				newSelection[i]= index + 1;
			}
		}
		table.setSelection(newSelection);
	}

	private void moveSelectionUp(TableViewer viewer) {
		Table table = viewer.getTable();
		int indices[]= table.getSelectionIndices();
		int newSelection[]= new int[indices.length];
		for (int i = 0; i < indices.length; i++) {
			int index= indices[i];
			if (index > 0) {
				move (viewer, table.getItem(index), index - 1);
				newSelection[i]= index - 1;
			}
		}
		table.setSelection(newSelection);
	}

	/**
	 * Moves an entry in the builder table to the given index.
	 */
	private void move(TableViewer viewer, TableItem item, int index) {
		Object data = item.getData();
		item.dispose();
		viewer.insert(data, index);
	}
	
	private void handleRemoveButtonPressed(TableViewer viewer) {
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		if (selection != null) {
			int numSelected= selection.size();
			
			Iterator iterator= selection.iterator();
			while (iterator.hasNext() ) {
				Object item= iterator.next();
				viewer.remove(item);
			}
			listChanged();
		}		
	}
	
	private void handleAddButtonPressed(int i) {
		Object[] o = handleAdd(i);
		if(o!=null) {
			add(o,true);
		}
	}

	public void add(Object[] o, boolean notify) {
		tableView.add(o);
		if (notify) listChanged();
	}

	protected void listChanged() {
	}

	protected Object[] handleAdd(int i) {
		return new Object[] { " test " };
	}

	/**
	 * The user has selected a different item in table.
	 * Update button enablement.
	 */
	private void handleTableSelectionChanged() {
		for (int i = 0; i < addButtons.length; i++) {
			addButtons[i].setEnabled(true);
		}
		Table builderTable= tableView.getTable();
		TableItem[] items = builderTable.getSelection();
		boolean validSelection= items != null && items.length > 0;
		boolean enableRemove= validSelection;
		boolean enableUp= validSelection;
		boolean enableDown= validSelection;
		if (validSelection) {
			int indices[]= builderTable.getSelectionIndices();
			int max = builderTable.getItemCount();
			enableUp= indices[0] != 0;
			enableDown= indices[indices.length - 1] < max - 1;
			enableRemove = true;
		}
		removeButton.setEnabled(enableRemove);
		upButton.setEnabled(enableUp);
		downButton.setEnabled(enableDown);
	}

	public Table getTable() {
		return tableView.getTable();
	}

	public void clear() {
		tableView.getTable().removeAll();
	}
}
