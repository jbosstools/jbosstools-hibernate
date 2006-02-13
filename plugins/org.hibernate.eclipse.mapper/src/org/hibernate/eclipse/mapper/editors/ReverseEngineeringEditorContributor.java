package org.hibernate.eclipse.mapper.editors;

import org.hibernate.eclipse.mapper.editors.reveng.xpl.MultiPageEditorContributor;

/**
 * Manages the installation/deinstallation of global actions for multi-page editors.
 * Responsible for the redirection of global actions to the active editor.
 * Multi-page contributor replaces the contributors for the individual editors in the multi-page editor.
 */
public class ReverseEngineeringEditorContributor extends MultiPageEditorContributor {
	
	private static final String EDITOR_ID = "org.hibernate.eclipse.mapper.editors.ReverseEngineeringEditor"; //$NON-NLS-1$
	
	public ReverseEngineeringEditorContributor() {
		super(EDITOR_ID);
	}
}
