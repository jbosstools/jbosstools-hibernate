/*
 * Created on 18-Oct-2004
 */
package org.hibernate.eclipse.console.wizards;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author max
 *
 */
public abstract class UpDownList {

	private SelectionListener buttonListener= new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			handleButtonPressed((Button) e.widget);
		}
	};
	
	private Button[] addButton;
	private Button removeButton;
	private Button upButton;
	private Button downButton;
	private TableViewer tableView;
	private Shell shell;

	private final Composite parent;
	private final String title;
	
	public UpDownList(Composite parent, Shell shell2, String title) {
		this.parent = parent;
		this.shell = shell2;
		this.title = title;
		build();
	}

	void build() {
		
		Font font = parent.getFont();
		
		Group topLevel = new Group(parent, SWT.NONE);
		topLevel.setText(title);
		GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		gd.verticalSpan = 3;
		gd.horizontalSpan = 3;
		
		topLevel.setLayoutData(gd);
		GridLayout layout = new GridLayout();
		
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 3;
		topLevel.setLayout(layout);
		
		// table of builders and tools
		tableView = new TableViewer(topLevel, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		//TODO: viewer.setLabelProvider(labelProvider);
		
		Table builderTable= tableView.getTable();
		builderTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		builderTable.setFont(font);
		builderTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleTableSelectionChanged();
			}
		});
		
		//button area
		Composite buttonArea = new Composite(topLevel, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttonArea.setLayout(layout);
		buttonArea.setFont(font);
		buttonArea.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		
		String[] addButtonLabels = getAddButtonLabels();
		addButton = new Button[addButtonLabels.length];
		for (int i = 0; i < addButtonLabels.length; i++) {
			String label = addButtonLabels[i];
			addButton[i] = createButton(buttonArea, label); //$NON-NLS-1$
			addButton[i].setEnabled(true);
		}
		removeButton = createButton(buttonArea, "Remove"); //$NON-NLS-1$
		new Label(buttonArea, SWT.LEFT);
		upButton = createButton(buttonArea, "Up"); //$NON-NLS-1$
		downButton = createButton(buttonArea, "Down");
		
		
		
		//populate widget contents	
		//addBuildersToTable();
		
	}

	protected String[] getAddButtonLabels() {
		return new String[] { "Add..." };		
	}

	/**
	 * One of the buttons has been pressed, act accordingly.
	 */
	private void handleButtonPressed(Button button) {
		if (button == removeButton) {
			handleRemoveButtonPressed(tableView);
		} else if (button == upButton) {
			moveSelectionUp(tableView);
		} else if (button == downButton) {
			moveSelectionDown(tableView);
		} else {
			for (int i = 0; i < addButton.length; i++) {
				Button but = addButton[i];
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
			while (iterator.hasNext()) {
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

	abstract protected void listChanged();

	abstract protected Object[] handleAdd(int i);

	protected Shell getShell() {
		return shell;
	}

	/**
	 * The user has selected a different item in table.
	 * Update button enablement.
	 */
	private void handleTableSelectionChanged() {
		for (int i = 0; i < addButton.length; i++) {
			addButton[i].setEnabled(true);
		}
		Table builderTable= tableView.getTable();
		TableItem[] items = builderTable.getSelection();
		boolean validSelection= items != null && items.length > 0;
		boolean enableEdit= validSelection;
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

	/**
	 * Creates and returns a button with the given label, id, and enablement.
	 */
	private Button createButton(Composite parent, String label) {
		Button button = new Button(parent, SWT.PUSH);
		GridData data = new GridData();
		//data.grabExcessHorizontalSpace = true;
		//data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		//data.verticalAlignment = GridData.FILL;
		
		button.setLayoutData(data);
		button.setFont(parent.getFont());
		button.setText(label);
		button.setEnabled(false);
		button.addSelectionListener(buttonListener);
		return button;
	}

	public Table getTable() {
		return tableView.getTable();
	}


}
