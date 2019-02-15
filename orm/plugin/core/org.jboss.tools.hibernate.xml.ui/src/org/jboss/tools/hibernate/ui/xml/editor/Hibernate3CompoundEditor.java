/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.ui.xml.editor;

import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;
import org.jboss.tools.common.editor.TreeFormPage;
import org.jboss.tools.common.model.ui.editor.EditorDescriptor;
import org.jboss.tools.common.model.ui.editors.multipage.DefaultMultipageEditor;
import org.jboss.tools.common.model.ui.texteditors.XMLTextEditorComponent;
import org.jboss.tools.hibernate.ui.xml.Messages;
import org.jboss.tools.hibernate.xml.model.FileHibernateFilteredTreeConstraint;


public class Hibernate3CompoundEditor extends DefaultMultipageEditor {
	
	protected void doCreatePages() {
		if(isAppropriateNature()) {
			treeFormPage = createTreeFormPage();
			treeFormPage.setTitle(Messages.Hibernate3CompoundEditor_HibernateXMLEditor);
			((TreeFormPage)treeFormPage).addFilter(new FileHibernateFilteredTreeConstraint());
			treeFormPage.initialize(object);
			addFormPage(treeFormPage);
		}
		createTextPage();
		initEditors();
		if(treeFormPage != null) selectionProvider.addHost("treeEditor", treeFormPage.getSelectionProvider()); //$NON-NLS-1$
		if(textEditor != null) selectionProvider.addHost("textEditor", getTextSelectionProvider()); //$NON-NLS-1$
	}

	public Object getAdapter(Class adapter) {
			if (adapter == EditorDescriptor.class)
				return new EditorDescriptor("Hibernate3.0"); //$NON-NLS-1$
			return super.getAdapter(adapter);
	}
	
	protected XMLTextEditorComponent createTextEditorComponent() {
		return new XMLTextEditorComponent(false);
	}

}