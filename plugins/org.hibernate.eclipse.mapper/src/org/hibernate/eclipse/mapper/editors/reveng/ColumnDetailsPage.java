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
		section.setText(Messages.COLUMNDETAILSPAGE_COLUMN_DETAILS);
		section.setDescription(Messages.COLUMNDETAILSPAGE_SET_PROPERTIES_OF_SELECTED_COLUMN);
		
		nameEntry = new FormTextEntry(client, toolkit, Messages.COLUMNDETAILSPAGE_NAME, SWT.NULL);
		nameEntry.setDescription(Messages.COLUMNDETAILSPAGE_THE_NAME_OF_THE_COLUMN);
		nameEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				column.setName(entry.getValue());
			}
		});
		
		excluded = toolkit.createButton(client, Messages.COLUMNDETAILSPAGE_EXCLUDE_COLUMNS_FROM_REVERSE_ENGINEERING, SWT.CHECK);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true,false);
		gridData.horizontalSpan = 3;
		excluded.setLayoutData(gridData);
		excluded.addSelectionListener(new SelectionAdapter() {
		
			public void widgetSelected(SelectionEvent e) {
				column.setExcluded(excluded.getSelection());
			}
		
		});
		
		jdbcTypeEntry = new FormTextEntry(client, toolkit, Messages.COLUMNDETAILSPAGE_JDBC_TYPE, SWT.NULL);
		jdbcTypeEntry.setDescription(Messages.COLUMNDETAILSPAGE_WHICH_JDBC_TYPE_THIS_COLUMN_SHOULD_HAVE);
		jdbcTypeEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				column.setJDBCType(entry.getValue());
			}
		});
		propertyEntry = new FormTextEntry(client, toolkit, Messages.COLUMNDETAILSPAGE_PROPERTY_NAME, SWT.NULL);
		propertyEntry.setDescription(Messages.COLUMNDETAILSPAGE_THE_PROPERTY_NAME_WHICH_MUST_BE_USED_FOR);
		propertyEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				column.setPropertyName(entry.getValue());
			}
		});
		
		typeEntry = new FormTextEntry(client, toolkit, Messages.COLUMNDETAILSPAGE_HIBERNATE_TYPE, SWT.NULL);
		typeEntry.setDescription(Messages.COLUMNDETAILSPAGE_THE_HIBERNATE_TYPE);
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
