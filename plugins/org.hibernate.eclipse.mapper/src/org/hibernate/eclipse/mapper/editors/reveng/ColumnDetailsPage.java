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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.hibernate.eclipse.console.model.IRevEngColumn;
import org.hibernate.eclipse.mapper.editors.reveng.xpl.FormTextEntry;

public class ColumnDetailsPage extends RevEngDetailsPage implements IDetailsPage, PropertyChangeListener {

	private Button excluded;
	private FormTextEntry nameEntry;
	private FormTextEntry jdbcTypeEntry;
	private FormTextEntry propertyEntry;
	private FormTextEntry typeEntry;
	private IRevEngColumn column;

	public void buildContents(FormToolkit toolkit, Section section, Composite client) {
		section.setText("Column Details");
		section.setDescription("Set the properties of the selected column.");
		
		nameEntry = new FormTextEntry(client, toolkit, "Name:", SWT.NULL);
		nameEntry.setDescription("The name of the column");
		nameEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				column.setName(entry.getValue());
			}
		});
		
		excluded = toolkit.createButton(client, "Exclude column from reverse engineering", SWT.CHECK);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true,false);
		gridData.horizontalSpan = 3;
		excluded.setLayoutData(gridData);
		excluded.addSelectionListener(new SelectionAdapter() {
		
			public void widgetSelected(SelectionEvent e) {
				column.setExcluded(excluded.getSelection());
			}
		
		});
		
		jdbcTypeEntry = new FormTextEntry(client, toolkit, "JDBC Type:", SWT.NULL);
		jdbcTypeEntry.setDescription("Which JDBC Type this column should have (overriding the type read from the database)");
		jdbcTypeEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				column.setJDBCType(entry.getValue());
			}
		});
		propertyEntry = new FormTextEntry(client, toolkit, "Property name:", SWT.NULL);
		propertyEntry.setDescription("The property name which must be used for this column when used as a property");
		propertyEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				column.setPropertyName(entry.getValue());
			}
		});
		
		typeEntry = new FormTextEntry(client, toolkit, "Hibernate Type:", SWT.NULL);
		typeEntry.setDescription("The hibernate type which should be used for this colum when used as a property");
		typeEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				column.setType(entry.getValue());
			}
		});		
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IRevEngColumn newColumn = (IRevEngColumn) ((IStructuredSelection)selection).getFirstElement();
		if(column!=null) {
			column.removePropertyChangeListener(this);			
		}
		if(newColumn!=null) {
			newColumn.addPropertyChangeListener(this);
		}
		column = newColumn;
		
		update();
	}

	private void update() {
		nameEntry.setValue(column.getName());
		jdbcTypeEntry.setValue(column.getJDBCType());
		propertyEntry.setValue(column.getPropertyName());
		typeEntry.setValue(column.getType());
		excluded.setSelection(column.getExclude());
		
		nameEntry.setEditable(!column.getExclude());
	}

	public void propertyChange(PropertyChangeEvent evt) {
		update();		
	}

}
