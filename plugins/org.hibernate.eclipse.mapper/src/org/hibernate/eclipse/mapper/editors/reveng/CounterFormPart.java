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

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;

public class CounterFormPart extends RevEngSectionPart {

	private Text text;
	private IReverseEngineeringDefinition def;
	private PropertyChangeListener listener;

	public CounterFormPart(Composite parent, IManagedForm form) {
		super(parent,form);
	}

	public boolean setFormInput(IReverseEngineeringDefinition def) {
		this.def = def;
		listener = new PropertyChangeListener() {
			int cnt = 0;
			public void propertyChange(PropertyChangeEvent evt) {
				text.setText("" + cnt++);		
			}
		
		};
		def.addPropertyChangeListener(listener);
		return true;
	}
	
	public void dispose() {
		def.removePropertyChangeListener(listener);
	}
	
	Control createClient(IManagedForm form) {
		FormToolkit toolkit = form.getToolkit();
		Composite composite = toolkit.createComposite(getSection());
		composite.setLayout(new FillLayout());
		text = toolkit.createText(composite, "Zero");
		return composite;
	}
	
	protected String getSectionDescription() {
		return "debug counter for property changes";
	}
	
	protected String getSectionTitle() {
		return "Debug counter";
	}
}
