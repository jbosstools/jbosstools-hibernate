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

import org.jboss.tools.common.editor.TreeFormPage;
import org.jboss.tools.common.model.ui.editor.EditorDescriptor;
import org.jboss.tools.common.model.ui.editors.multipage.DefaultMultipageEditor;
import org.jboss.tools.hibernate.xml.model.FileHibernateFilteredTreeConstraint;


public class Hibernate3CompoundEditor extends DefaultMultipageEditor {
	
	protected void doCreatePages() {
		if(isAppropriateNature()) {
			treeFormPage = createTreeFormPage();
			treeFormPage.setTitle("Hibernate 3.0 XML Editor");
			((TreeFormPage)treeFormPage).addFilter(new FileHibernateFilteredTreeConstraint());
			treeFormPage.initialize(object);
			addFormPage(treeFormPage);
		}
		createTextPage();
		initEditors();
		if(treeFormPage != null) selectionProvider.addHost("treeEditor", treeFormPage.getSelectionProvider());
		if(textEditor != null) selectionProvider.addHost("textEditor", getTextSelectionProvider());
	}

	public Object getAdapter(Class adapter) {
			if (adapter == EditorDescriptor.class)
				return new EditorDescriptor("Hibernate3.0");
			return super.getAdapter(adapter);
	}
}