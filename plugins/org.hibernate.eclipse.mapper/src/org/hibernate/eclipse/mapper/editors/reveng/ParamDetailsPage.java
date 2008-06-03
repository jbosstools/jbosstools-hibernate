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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.hibernate.eclipse.console.model.IRevEngParameter;
import org.hibernate.eclipse.mapper.editors.reveng.xpl.FormTextEntry;

public class ParamDetailsPage extends RevEngDetailsPage implements IDetailsPage, PropertyChangeListener {

	//	private Label label;
	private FormTextEntry nameEntry;
	private FormTextEntry valueEntry;
	
	private IRevEngParameter param;
	
	protected void buildContents(FormToolkit toolkit, Section section, Composite client) {
		section.setText("Generator parameter details");
		section.setDescription("Set the properties of the selected parameter.");
				
		nameEntry = new FormTextEntry(client, toolkit, "Name:", SWT.NULL);
		nameEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				param.setName(entry.getValue());
			}
		});
		
		valueEntry = new FormTextEntry(client, toolkit, "Value:", SWT.NULL);
		valueEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				param.setValue(entry.getValue());
			}
		});
		
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IRevEngParameter newParam = (IRevEngParameter) ((IStructuredSelection)selection).getFirstElement();
		if(param!=null) {
			param.removePropertyChangeListener(this);			
		}
		if(newParam!=null) {
			newParam.addPropertyChangeListener(this);
		}
		
		param = newParam;
		
		update();
	}

	private void update() {
		nameEntry.setValue(param.getName());
		valueEntry.setValue(param.getValue());
	}

	public void propertyChange(PropertyChangeEvent evt) {
		update();		
	}

}
