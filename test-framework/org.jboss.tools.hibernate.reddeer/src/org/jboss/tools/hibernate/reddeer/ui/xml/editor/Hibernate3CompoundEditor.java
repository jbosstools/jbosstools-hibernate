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
package org.jboss.tools.hibernate.reddeer.ui.xml.editor;

import org.jboss.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.jboss.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.jboss.reddeer.workbench.impl.editor.AbstractEditor;

/**
 * Hibernate mapping file editor (Hibernate 3. XML Editor)
 * @author Jiri Peterka
 *
 */
public class Hibernate3CompoundEditor extends AbstractEditor {

	/**
	 * Initiates mapping file editor for given file
	 */
	public Hibernate3CompoundEditor(String file) {
		super(file);
	}	

	/**
	 * Returns editor's source text
	 * @return editor source text
	 */
	public String getSourceText() {
		activateSourceTab();
		DefaultStyledText dst = new DefaultStyledText();		
		String source = dst.getText();
		return source;
	}

	/**
	 * Activates editor's Source tab
	 */
	public void activateSourceTab() {
		new DefaultCTabItem("Source").activate();
	}

	/**
	 * Activates editor's Tree tab
	 */
	public void activateTreeTab() {
		new DefaultCTabItem("Tree").activate();
	}	
}
