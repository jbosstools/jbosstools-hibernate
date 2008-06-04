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
		section.setText(Messages.TABLEDETAILSPAGE_TABLE_DETAILS);
		section.setDescription(Messages.TABLEDETAILSPAGE_SET_PROPERTIES_OF_SELECTED_TABLE);
		
		catalogEntry = new FormTextEntry(client, toolkit, Messages.TABLEDETAILSPAGE_CATALOG, SWT.NULL);
		catalogEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				table.setCatalog(entry.getValue());
			}
		});
		schemaEntry = new FormTextEntry(client, toolkit, Messages.TABLEDETAILSPAGE_SCHEMA, SWT.NULL);
		schemaEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				table.setSchema(entry.getValue());
			}
		});

		nameEntry = new FormTextEntry(client, toolkit, Messages.TABLEDETAILSPAGE_NAME, SWT.NULL);
		nameEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				table.setName(entry.getValue());
			}
		});
		
		classEntry = new FormTextEntry(client, toolkit, Messages.TABLEDETAILSPAGE_CLASS_NAME, SWT.NULL);
		classEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				table.setClassname(entry.getValue());
			}
		});
		
		Button button = toolkit.createButton(client, Messages.TABLEDETAILSPAGE_ADD_PRIMARY_KEY, SWT.NULL);
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
