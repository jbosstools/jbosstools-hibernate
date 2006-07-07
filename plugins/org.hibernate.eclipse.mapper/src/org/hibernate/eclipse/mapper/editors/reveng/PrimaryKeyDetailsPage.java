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
import org.hibernate.eclipse.console.model.IRevEngPrimaryKey;

public class PrimaryKeyDetailsPage extends RevEngDetailsPage implements
		IDetailsPage, PropertyChangeListener {


	//	private Label label;
	
	private IRevEngPrimaryKey primaryKey;
	
	protected void buildContents(FormToolkit toolkit, Section section, Composite client) {
		section.setText("Primary key details");
		section.setDescription("A primary key can define a generator strategy and alternative columns");
				
		Button createGenerator = toolkit.createButton(client, "Add generator", SWT.NONE);
		createGenerator.addSelectionListener(new SelectionAdapter() {
		
			public void widgetSelected(SelectionEvent e) {
				handleAddGenerator();
			}		
		});
		
		Button createColumn = toolkit.createButton(client, "Add column", SWT.NONE);
		createColumn.addSelectionListener(new SelectionAdapter() {
		
			public void widgetSelected(SelectionEvent e) {
				handleAddColumn();
			}		
		});
	}

	protected void handleAddColumn() {
		primaryKey.addColumn();		
	}

	protected void handleAddGenerator() {
		if(primaryKey.getGenerator()==null) {
			primaryKey.addGenerator();
		}
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IRevEngPrimaryKey newParam = (IRevEngPrimaryKey) ((IStructuredSelection)selection).getFirstElement();
		if(primaryKey!=null) {
			primaryKey.removePropertyChangeListener(this);			
		}
		if(newParam!=null) {
			newParam.addPropertyChangeListener(this);
		}
		
		primaryKey = newParam;
		
		update();
	}

	private void update() {
		
	}

	public void propertyChange(PropertyChangeEvent evt) {
		update();		
	}

}
