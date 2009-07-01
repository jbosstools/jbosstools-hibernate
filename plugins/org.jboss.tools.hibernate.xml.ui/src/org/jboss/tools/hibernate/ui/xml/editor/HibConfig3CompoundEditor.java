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
import org.jboss.tools.common.model.ui.texteditors.XMLTextEditorComponent;
import org.jboss.tools.hibernate.ui.xml.HibernateUIXMLPlugin;
import org.jboss.tools.hibernate.ui.xml.Messages;
import org.jboss.tools.hibernate.xml.model.FileHibernateFilteredTreeConstraint;


public class HibConfig3CompoundEditor extends DefaultMultipageEditor {
	
	protected TreeFormPage sessionFactory;
	protected TreeFormPage security;
	
	protected void doCreatePages() {
		if(isAppropriateNature()) {
			
			sessionFactory = createTreeFormPage();
			sessionFactory.setLabel(Messages.HibConfig3CompoundEditor_SessionFactoryLabel);
			sessionFactory.setTitle(Messages.HibConfig3CompoundEditor_SessionFactoryTitle);
			((TreeFormPage)sessionFactory).addFilter(new FileHibernateFilteredTreeConstraint());
			sessionFactory.initialize(object.getChildByPath("Session Factory")); //$NON-NLS-1$
			addFormPage(sessionFactory, "sessionFactoryEditor"); //$NON-NLS-1$

			security = createTreeFormPage();
			security.setLabel(Messages.HibConfig3CompoundEditor_SecurityLabel);
			security.setTitle(Messages.HibConfig3CompoundEditor_SecurityTitle);
			((TreeFormPage)security).addFilter(new FileHibernateFilteredTreeConstraint());
			security.initialize(object.getChildByPath("Security")); //$NON-NLS-1$
			addFormPage(security, "securityEditor"); //$NON-NLS-1$

		}
		createTextPage();
		initEditors();
		if(treeFormPage != null) selectionProvider.addHost("treeEditor", treeFormPage.getSelectionProvider()); //$NON-NLS-1$
		if(sessionFactory != null) selectionProvider.addHost("sessionFactoryEditor", sessionFactory.getSelectionProvider()); //$NON-NLS-1$
		if(textEditor != null) selectionProvider.addHost("textEditor", getTextSelectionProvider()); //$NON-NLS-1$
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
			sessionFactory.initialize(getModelObject().getChildByPath("Session Factory")); // AU added //$NON-NLS-1$
			sessionFactory.setErrorMode(isErrorMode());
			security.initialize(getModelObject().getChildByPath("Security")); // AU added //$NON-NLS-1$
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
			sessionFactory.initialize(getModelObject().getChildByPath("Session Factory")); // AU added //$NON-NLS-1$
			sessionFactory.setErrorMode(isErrorMode());
			security.initialize(getModelObject().getChildByPath("Security")); // AU added //$NON-NLS-1$
			security.setErrorMode(isErrorMode());
		} // AU added
		if (treeEditor!=null) { 
			treeEditor.setObject(object, isErrorMode());
		}
	}

	public Object getAdapter(Class adapter) {
			if (adapter == EditorDescriptor.class)
				return new EditorDescriptor("HibernateConfiguration3.0"); //$NON-NLS-1$

			return super.getAdapter(adapter);
	}

	protected XMLTextEditorComponent createTextEditorComponent() {
		return new XMLTextEditorComponent(false);
	}

}
