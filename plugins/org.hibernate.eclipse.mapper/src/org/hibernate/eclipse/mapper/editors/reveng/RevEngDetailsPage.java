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

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public abstract class RevEngDetailsPage implements IDetailsPage {

	private IManagedForm mform;
	
	final public void createContents(Composite parent) {
		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 5;
		layout.leftMargin = 5;
		layout.rightMargin = 2;
		layout.bottomMargin = 2;
		parent.setLayout(layout);
	
		FormToolkit toolkit = mform.getToolkit();
		Section section = toolkit.createSection(parent, Section.DESCRIPTION);
		section.marginWidth = 10;
		TableWrapData td = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
		td.grabHorizontal = true;
		section.setLayoutData(td);
		toolkit.createCompositeSeparator(section);
		Composite client = toolkit.createComposite(section);
		GridLayout glayout = new GridLayout();
		glayout.marginWidth = glayout.marginHeight = 2;
		glayout.numColumns = 2;
		client.setLayout(glayout);
		//client.setBackground(client.getDisplay().getSystemColor(SWT.COLOR_CYAN));
		
		toolkit.paintBordersFor(section);
		toolkit.paintBordersFor(client);
		
		buildContents( toolkit, section, client );
		section.setClient(client);
	}

	abstract protected void buildContents(FormToolkit toolkit, Section section, Composite client);

	public void initialize(IManagedForm form) {
		this.mform = form;
	}

	public void dispose() {

	}

	public boolean isDirty() {
		return false;
	}

	public void commit(boolean onSave) {

	}

	public boolean setFormInput(Object input) {
		return false;
	}

	public void setFocus() {

	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {

	}

}
