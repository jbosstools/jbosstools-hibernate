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
package org.hibernate.eclipse.mapper.editors;

import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;
import org.hibernate.eclipse.mapper.MapperPlugin;
import org.hibernate.eclipse.mapper.editors.reveng.HibernateConfigurationForm;

public class HibernateCfgXmlEditor extends XMLMultiPageEditorPart {

	private HibernateConfigurationForm configurationForm;
	private int configurationPageNo;
	private StructuredTextEditor sourcePage;
	
	public HibernateCfgXmlEditor() {
		super();
	}

	protected void createPages() {
		try {
			addFormPage();
			super.createPages();
			initSourcePage();
		} catch (PartInitException pe) {
			MapperPlugin.getDefault().getLogger().logException(
					"Could not create form part for hibernate.cfg.xml editor", pe );
		}
	}

	private void initSourcePage() {
		int pageCount = getPageCount();
		for (int i = 0; i < pageCount; i++) {
			if ( getEditor( i ) instanceof StructuredTextEditor ) {
				sourcePage = (StructuredTextEditor) getEditor( i );							
			}
		}
		
		configurationForm.setModel(getStructuredModel());
	}

	private void addFormPage() throws PartInitException {
		configurationForm = new HibernateConfigurationForm();
        configurationForm.createPartControl(getContainer());
        configurationPageNo = addPage(configurationForm.getControl());
        setPageText(configurationPageNo, "Configuration");
        setActivePage( 0 );
	}
	
	IStructuredModel getStructuredModel() {
		//TODO:how to get it without usage of deprecated methods ?
		return sourcePage.getModel();
	}
	
}
