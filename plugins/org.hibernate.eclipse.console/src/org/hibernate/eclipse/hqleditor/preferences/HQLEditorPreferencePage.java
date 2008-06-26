package org.hibernate.eclipse.hqleditor.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class HQLEditorPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public HQLEditorPreferencePage() {
		super(GRID);
		setPreferenceStore(HibernateConsolePlugin.getDefault().getPreferenceStore());
		setDescription("Colors for HQL editor syntax highlighting");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new ColorFieldEditor(HQLPreferenceConstants.HQL_DEFAULT_COLOR, 
				"Default:", getFieldEditorParent())); 
		addField(new ColorFieldEditor(HQLPreferenceConstants.HQL_IDENTIFIER_COLOR, 
				"Identifier:", getFieldEditorParent()));
		addField(new ColorFieldEditor(HQLPreferenceConstants.HQL_KEYWORD_COLOR, 
				"Keyword:", getFieldEditorParent()));
		addField(new ColorFieldEditor(HQLPreferenceConstants.HQL_QUOTED_LITERAL_COLOR, 
				"String literal:", getFieldEditorParent()));
		}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}