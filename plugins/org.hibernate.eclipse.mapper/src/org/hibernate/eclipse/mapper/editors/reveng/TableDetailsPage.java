package org.hibernate.eclipse.mapper.editors.reveng;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.hibernate.eclipse.console.model.IRevEngTable;
import org.hibernate.eclipse.mapper.editors.reveng.xpl.FormTextEntry;

public class TableDetailsPage extends RevEngDetailsPage implements IDetailsPage, PropertyChangeListener {

	
	//private Label label;
	private FormTextEntry nameEntry;
	private FormTextEntry schemaEntry;
	private FormTextEntry catalogEntry;
	private FormTextEntry classEntry;

	private IRevEngTable table;
	
	protected void buildContents(FormToolkit toolkit, Section section, Composite client) {
		section.setText("Table details");
		section.setDescription("Set the properties of the selected table.");
		
		catalogEntry = new FormTextEntry(client, toolkit, "Catalog:", SWT.NULL);
		catalogEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				table.setCatalog(entry.getValue());
			}
		});
		schemaEntry = new FormTextEntry(client, toolkit, "Schema:", SWT.NULL);
		schemaEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				table.setSchema(entry.getValue());
			}
		});

		nameEntry = new FormTextEntry(client, toolkit, "Name:", SWT.NULL);
		nameEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				table.setName(entry.getValue());
			}
		});
		
		classEntry = new FormTextEntry(client, toolkit, "Class name:", SWT.NULL);
		classEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				table.setClassname(entry.getValue());
			}
		});
		
		Button button = toolkit.createButton(client, "Add primary key", SWT.NULL);
		button.addSelectionListener(new SelectionAdapter() {
		
			public void widgetSelected(SelectionEvent e) {
				handleAddPrimaryKey();		
			}
		
		});
	}


	protected void handleAddPrimaryKey() {
		if(table.getPrimaryKey()==null) {
			table.addPrimaryKey();
		}
	}


	public void selectionChanged(IFormPart part, ISelection selection) {
		IRevEngTable newTable = (IRevEngTable) ((IStructuredSelection)selection).getFirstElement();
		if(table!=null) {
			table.removePropertyChangeListener(this);			
		}
		if(newTable!=null) {
			newTable.addPropertyChangeListener(this);
		}
		table = newTable;
		
		update();
	}

	private void update() {
		catalogEntry.setValue(table.getCatalog());
		schemaEntry.setValue(table.getSchema());
		nameEntry.setValue(table.getName());
		classEntry.setValue(table.getClassname());
		//excludeEntry.setValue(table.getExclude());
	}

	public void propertyChange(PropertyChangeEvent evt) {
		update();		
	}

}
