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
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.model.IModelStateListenerProposed;
import org.eclipse.wst.sse.core.internal.provisional.model.IStructuredModelEvent;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;

public class HibernateConfigurationForm {

	private IStructuredModel model;

	private IModelStateListener modelListener;

	private Composite container;

	private ManagedForm managedForm;

	private Document inputDocument;

	public void setModel(IStructuredModel structuredModel) {
		if ( this.model != null )
			model.removeModelStateListener( this.modelListener );
		this.model = structuredModel;

		if ( this.model != null && (this.model instanceof IDOMModel ) ) {
			model.addModelStateListener( modelListener );
			Document document = ((IDOMModel) model ).getDocument();
			setInput( document );
		}
	}

	private void setInput(Document document) {
		this.inputDocument = document;
	}

	public Control getControl() {
		return getForm();
	}

	public void refresh() {
	}

	public void createPartControl(Composite cotainer) {
		this.container = cotainer;
		managedForm = new ManagedForm( container );
		getForm().setText( "Hibernate Configuration" );

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		getForm().getBody().setLayout( layout );

		createFormParts();
		managedForm.initialize();
	}

	private void createFormParts() {
		//managedForm.addPart(part);
	}

	private ScrolledForm getForm() {
		return managedForm.getForm();
	}

	static class ModelStateListener implements IModelStateListener,
			IModelStateListenerProposed {

		public void modelAboutToBeChanged(IStructuredModel model) {
			// TODO: here we can set flag for ignoring edit events
		}

		public void modelChanged(IStructuredModel model) {
			// TODO: here we can actually perform the refresh if needed..
		}

		public void modelDirtyStateChanged(IStructuredModel model,
				boolean isDirty) {
		}

		public void modelResourceDeleted(IStructuredModel model) {
		}

		public void modelResourceMoved(IStructuredModel oldModel,
				IStructuredModel newModel) {
		}

		public void modelAboutToBeReinitialized(IStructuredModel structuredModel) {
		}

		public void modelReinitialized(IStructuredModel structuredModel) {
		}

		public void modelAboutToBeChanged(IStructuredModelEvent event) {
		}

		public void modelChanged(IStructuredModelEvent event) {
		}

	}
}
