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
import org.jboss.tools.hibernate.ui.xml.HibernateUIXMLPlugin;
import org.jboss.tools.hibernate.xml.model.FileHibernateFilteredTreeConstraint;


public class HibConfig3CompoundEditor extends DefaultMultipageEditor {
	
	protected TreeFormPage sessionFactory;
	protected TreeFormPage security;
	
	protected void doCreatePages() {
		if(isAppropriateNature()) {
			
			sessionFactory = createTreeFormPage();
			sessionFactory.setLabel("Session Factory");
			sessionFactory.setTitle("Hibernate Configuration 3.0 XML Editor");
			((TreeFormPage)sessionFactory).addFilter(new FileHibernateFilteredTreeConstraint());
			sessionFactory.initialize(object.getChildByPath("Session Factory"));
			addFormPage(sessionFactory, "sessionFactoryEditor");

			security = createTreeFormPage();
			security.setLabel("Security");
			security.setTitle("Hibernate Configuration 3.0 XML Editor");
			((TreeFormPage)security).addFilter(new FileHibernateFilteredTreeConstraint());
			security.initialize(object.getChildByPath("Security"));
			addFormPage(security, "securityEditor");

		}
		createTextPage();
		initEditors();
		if(treeFormPage != null) selectionProvider.addHost("treeEditor", treeFormPage.getSelectionProvider());
		if(sessionFactory != null) selectionProvider.addHost("sessionFactoryEditor", sessionFactory.getSelectionProvider());
		if(textEditor != null) selectionProvider.addHost("textEditor", getTextSelectionProvider());
	}

	protected void addFormPage(TreeFormPage formPage, String name) {
		try {
			int index = addPage(formPage, getEditorInput());
			setPageText(index, formPage.getLabel());
			selectionProvider.addHost(name, formPage.getSelectionProvider());
			//Activate key binding service here
			formPage.getEditorSite().getKeyBindingService();
		} catch (Exception ex) {
			HibernateUIXMLPlugin.log(ex);
		}
			//getSite().setSelectionProvider(formPage.getSelectionProvider());
	  }

	protected void setNormalMode() {
		if (treeFormPage!=null) { // AU added
			sessionFactory.initialize(getModelObject().getChildByPath("Session Factory")); // AU added
			sessionFactory.setErrorMode(isErrorMode());
			security.initialize(getModelObject().getChildByPath("Security")); // AU added
			security.setErrorMode(isErrorMode());
		} // AU added
		if (selectionProvider!=null) {
			updateSelectionProvider();
		}
		if (treeEditor!=null) { 
			treeEditor.setObject(object, isErrorMode());
		}
	}

	protected void setErrorMode() {
		if (treeFormPage!=null) { // AU added
			sessionFactory.initialize(getModelObject().getChildByPath("Session Factory")); // AU added
			sessionFactory.setErrorMode(isErrorMode());
			security.initialize(getModelObject().getChildByPath("Security")); // AU added
			security.setErrorMode(isErrorMode());
		} // AU added
		if (treeEditor!=null) { 
			treeEditor.setObject(object, isErrorMode());
		}
	}

	public Object getAdapter(Class adapter) {
			if (adapter == EditorDescriptor.class)
				return new EditorDescriptor("HibernateConfiguration3.0");

			return super.getAdapter(adapter);
	}

}
