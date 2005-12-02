package org.hibernate.eclipse.launch;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.debug.internal.ui.SWTUtil;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.model.WorkbenchContentProvider;

public class PropertiesPart {
	
	private Object container;
	
	private Button editButton;
	private Button removeButton;
	private Button addButton;
	
	private TableViewer propertyTableViewer;
	
	private final ILabelProvider labelProvider = new LabelProvider();

	private boolean tablesEnabled= true;
    
	private final String[] fTableColumnHeaders= {
	        "Name", "Value"
	};
	
	private final ColumnLayoutData[] fTableColumnLayouts= {
	        new ColumnWeightData(30),
	        new ColumnWeightData(40)        
	};
	
	/**
	 * Button listener that delegates for widget selection events.
	 */
	private SelectionAdapter buttonListener= new SelectionAdapter() {
		public void widgetSelected(SelectionEvent event) {
			if (event.widget == addButton) {
				addProperty();
			} else if (event.widget == editButton) {
				edit();
			} else if (event.widget == removeButton) {
				remove(propertyTableViewer);
			} 
		}
	};
	
	/**
	 * Key listener that delegates for key pressed events.
	 */
	private KeyAdapter keyListener= new KeyAdapter() {
		public void keyPressed(KeyEvent event) {
			if (event.getSource() == propertyTableViewer) {
				if (removeButton.isEnabled() && event.character == SWT.DEL && event.stateMask == 0) {
					remove(propertyTableViewer);
				}
			} 
		}	
	};
	
	/**
	 * Selection changed listener that delegates selection events.
	 */
	private ISelectionChangedListener tableListener= new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			if (tablesEnabled) {
				if (event.getSource() == propertyTableViewer) {
					propertyTableSelectionChanged((IStructuredSelection) event.getSelection());
				} 
			}
		}
	};

	
	
	public PropertiesPart(Object container) {
		this.container= container; 
	}

	public void createControl(Composite top, String propertyLabel) {
		Font font= top.getFont();
		
		Label label = new Label(top, SWT.NONE);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalSpan =2;
		label.setLayoutData(gd);
		label.setFont(font);
		label.setText(propertyLabel);

		propertyTableViewer= createTableViewer(top, true);
		propertyTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (!event.getSelection().isEmpty() && editButton.isEnabled()) {
					edit();
				}
			}
		});
		
		propertyTableViewer.getTable().addKeyListener(keyListener);	
		
		createButtonGroup(top);

		createButtonGroup(top);
	}
	
	/**
	 * Creates the group which will contain the buttons.
	 */
	private void createButtonGroup(Composite top) {
		Composite buttonGroup = new Composite(top, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttonGroup.setLayout(layout);
		buttonGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL));
		buttonGroup.setFont(top.getFont());

		addButtonsToButtonGroup(buttonGroup);
	}
	
	/**
	 * Creates and returns a configured table viewer in the given parent
	 */
	private TableViewer createTableViewer(Composite parent, boolean setColumns) {
		Table table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		GridData data= new GridData(GridData.FILL_BOTH);
		data.widthHint = IDialogConstants.ENTRY_FIELD_WIDTH;
		table.setLayoutData(data);
		table.setFont(parent.getFont());
		
		TableViewer tableViewer= new TableViewer(table);
		tableViewer.setContentProvider(new WorkbenchContentProvider());
		tableViewer.setLabelProvider(labelProvider);
		tableViewer.addSelectionChangedListener(tableListener);
        
        if (setColumns) {
            TableLayout tableLayout = new TableLayout();
            table.setLayout(tableLayout);
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            
            for (int i = 0; i < fTableColumnHeaders.length; i++) {
                tableLayout.addColumnData(fTableColumnLayouts[i]);
                TableColumn column = new TableColumn(table, SWT.NONE, i);
                column.setResizable(fTableColumnLayouts[i].resizable);
                column.setText(fTableColumnHeaders[i]);
            }
        }
            
		return tableViewer;
	}
	
	protected void addButtonsToButtonGroup(Composite parent) {
		addButton= createPushButton(parent, "Add..."); 
		editButton= createPushButton(parent, "Edit..");
		removeButton= createPushButton(parent, "Remove"); 
	}
	
	private Button createPushButton(Composite parent, String label) {
		Button button= SWTUtil.createPushButton(parent, label, null);
		button.addSelectionListener(buttonListener);
		GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
		button.setLayoutData(gridData);
		return button;
	}
	
	private void remove(TableViewer viewer) {
	/*	AntContentProvider antContentProvider= (AntContentProvider)viewer.getContentProvider();
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		antContentProvider.remove(sel);
		container.update();*/
	}
	
	/**
	 * Allows the user to enter a user property
	 */
	private void addProperty() {
		String title = "Add Property";
		AddPropertyDialog dialog = new AddPropertyDialog(propertyTableViewer.getControl().getShell(), title, new String[]{"", ""}); //$NON-NLS-1$ //$NON-NLS-2$
		if (dialog.open() == Window.CANCEL) {
			return;
		}
		
		String[] pair= dialog.getNameValuePair();
		String name= pair[0];
		if (!overwrite(name)) {
			return;
		}
		Property prop = new Property();
		prop.setName(name);
		prop.setValue(pair[1]);
		//((ContentProvider)propertyTableViewer.getContentProvider()).add(prop);
		//container.update();
	}

	static class Property {
		String name;
		String value;
		
		public void setName(String name) {
			this.name = name;
		}
		
		public void setValue(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public String getName() {
			return name;
		}
	}
	
	private void edit() {
		IStructuredSelection selection= (IStructuredSelection) propertyTableViewer.getSelection();
		Property prop = (Property) selection.getFirstElement();
		
		String originalName= prop.getName();
		String title = "Edit property";
		AddPropertyDialog dialog = new AddPropertyDialog(propertyTableViewer.getControl().getShell(), title, new String[]{prop.getName(), prop.getValue()});
	
		if (dialog.open() == Window.CANCEL) {
			return;
		}

		String[] pair= dialog.getNameValuePair();
		String name= pair[0];
		if (!name.equals(originalName)) {
			if (!overwrite(name)){
				return;
			}
		}
		prop.setName(name);
		prop.setValue(pair[1]);
		//trigger a resort
		propertyTableViewer.refresh();
	//	container.update();
	}
	
	private boolean overwrite(String name) {
		Object[] properties= getProperties();
		for (int i = 0; i < properties.length; i++) {
			Property property = (Property)properties[i];
			String propertyName = property.getName();
			if (propertyName.equals(name)) {
				boolean overWrite= MessageDialog.openQuestion(propertyTableViewer.getControl().getShell(), "Overwrite property ?", "Property " + name + " already exists, overwrite ?");  
				if (!overWrite) {
					return false;
				}
				//((AntContentProvider)propertyTableViewer.getContentProvider()).remove(property);
				break;
			}					
		}
		return true;
	}
	

	/**
	 * Handles selection changes in the Property table viewer.
	 */
	private void propertyTableSelectionChanged(IStructuredSelection newSelection) {
		int size = newSelection.size();
		boolean enabled= true;

		Iterator itr= newSelection.iterator();
		while (itr.hasNext()) {
			Object element = itr.next();
			if (element instanceof Property) {
				Property property= (Property)element;
				/*if (property.isDefault()) {
					enabled= false;
					break;
				}*/
			}
		}
		editButton.setEnabled(enabled && size == 1);
		removeButton.setEnabled(enabled && size > 0);
		
	}
	
	public void populatePropertyViewer(Map properties) {
		if (properties == null) {
			propertyTableViewer.setInput(new Property[0]);
			return;
		} 
		Property[] result = new Property[properties.size()];
		Iterator entries= properties.entrySet().iterator();
		int i= 0;
		while (entries.hasNext()) {
			Map.Entry element = (Map.Entry) entries.next();
			Property property = new Property();
			property.setName((String)element.getKey());
			property.setValue((String)element.getValue());
			result[i]= property;
			i++;
		}
		propertyTableViewer.setInput(result);
	}
	
	public void setPropertiesInput(Property[] properties) {
		propertyTableViewer.setInput(properties);
	}
	
	public void update() {
		propertyTableSelectionChanged((IStructuredSelection) propertyTableViewer.getSelection());
	}
	
	public Object[] getProperties() {
		//return ((AntContentProvider)propertyTableViewer.getContentProvider()).getElements(null);
		return new Object[0];
	}
	
	public void setEnabled(boolean enable) {
		setTablesEnabled(enable);
		addButton.setEnabled(enable);
		editButton.setEnabled(enable);
		removeButton.setEnabled(enable);
		
		if (enable) {
			propertyTableViewer.setSelection(propertyTableViewer.getSelection());
		}
	}
	
	public void setTablesEnabled(boolean tablesEnabled) {
		this.tablesEnabled= tablesEnabled;
	}
}
