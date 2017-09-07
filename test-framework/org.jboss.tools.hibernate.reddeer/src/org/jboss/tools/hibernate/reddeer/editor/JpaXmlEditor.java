/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.reddeer.editor;

import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import org.eclipse.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.eclipse.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.workbench.impl.editor.AbstractEditor;

/**
 * Multipage Persistence XML Editor with Hibernate Tab
 * @author Jiri Peterka
 * TODO move to reddeer
 *
 */
public class JpaXmlEditor extends AbstractEditor {

	/**
	 * Initiates Persistence.xml editor
	 */
	public JpaXmlEditor() {
		super("persistence.xml");
	}	

	/**
	 * Sets hibernate username on hibernate tab
	 * @param username hibernate username
	 */
	public void setHibernateUsername(String username) {
		activateHibernateTab();
		new LabeledText(this, "Username:").setText(username);
	}
		
	/**
	 * Sets hibernate dialect on hibernate tab
	 * @param dialect hibernate dialect
	 */
	public void setHibernateDialect(String dialect) {
		activateHibernateTab();
		new LabeledCombo(this, "Database dialect:").setSelection(dialect);
	}

	/**
	 * Returns editor's source text
	 * @return editor source text
	 */
	public String getSourceText() {
		activateSourceTab();
		DefaultStyledText dst = new DefaultStyledText(this);		
		String source = dst.getText();
		return source;
	}
	
	/**
	 * Activates editor's Overview tab
	 */
	public void activateOverviewTab() {
		new DefaultCTabItem(this, "Overview").activate();
	}

	/**
	 * Activates editor's Type Mappings tab
	 */
	public void activateGeneralTab() {
		new DefaultCTabItem(this, "General").activate();
	}

	/**
	 * Activates editor's Type Filters tab
	 */
	public void activateConnectionsTab() {
		new DefaultCTabItem(this, "Connections").activate();
	}

	/**
	 * Activates editor's Table and Columns tab
	 */	
	public void  activateOptionsTab() {
		new DefaultCTabItem(this, "Options").activate();
	}
	
	/**
	 * Activates editor's Properties tab
	 */
	public void activatePropertiesTab() {
		new DefaultCTabItem(this, "Properties").activate();
	}
	
	/**
	 * Activates editor's Hibernate tab
	 */
	public void activateHibernateTab() {
		new DefaultCTabItem(this, "Hibernate").activate();
	}

	/**
	 * Activates editor's Source tab
	 */
	public void activateSourceTab() {
		new DefaultCTabItem(this, "Source").activate();
	}	
}
