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
package org.hibernate.eclipse.console.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
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


public class HibernateConsolePreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	public static final String P_PATH = "pathPreference"; //$NON-NLS-1$
	public static final String P_BOOLEAN = "booleanPreference"; //$NON-NLS-1$
	public static final String P_CHOICE = "choicePreference"; //$NON-NLS-1$
	public static final String P_STRING = "stringPreference"; //$NON-NLS-1$

	public HibernateConsolePreferencePage() {
		super(GRID);
		setPreferenceStore(HibernateConsolePlugin.getDefault().getPreferenceStore() );
		setDescription(HibernateConsoleMessages.HibernateConsolePreferencePage_demo_of_pref_page_impl);
		initializeDefaults();
	}
/**
 * Sets the default values of the preferences.
 */
	private void initializeDefaults() {
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(P_BOOLEAN, true);
		store.setDefault(P_CHOICE, HibernateConsoleMessages.HibernateConsolePreferencePage_choice2);
		store.setDefault(P_STRING, HibernateConsoleMessages.HibernateConsolePreferencePage_def_value);
	}

/**
 * Creates the field editors. Field editors are abstractions of
 * the common GUI blocks needed to manipulate various types
 * of preferences. Each field editor knows how to save and
 * restore itself.
 */

	public void createFieldEditors() {
		addField(new DirectoryFieldEditor(P_PATH,
				HibernateConsoleMessages.HibernateConsolePreferencePage_dir_preference, getFieldEditorParent() ) );
		addField(
			new BooleanFieldEditor(
				P_BOOLEAN,
				HibernateConsoleMessages.HibernateConsolePreferencePage_example_of_bool_pref,
				getFieldEditorParent() ) );

		addField(new RadioGroupFieldEditor(
			P_CHOICE,
			HibernateConsoleMessages.HibernateConsolePreferencePage_example_of_multichoise_pref,
			1,
			new String[][] { { HibernateConsoleMessages.HibernateConsolePreferencePage_choice_1, HibernateConsoleMessages.HibernateConsolePreferencePage_choice1 }, {
				HibernateConsoleMessages.HibernateConsolePreferencePage_choice_2, HibernateConsoleMessages.HibernateConsolePreferencePage_choice2 }
		}, getFieldEditorParent() ) );
		addField(
			new StringFieldEditor(P_STRING, HibernateConsoleMessages.HibernateConsolePreferencePage_text_pref, getFieldEditorParent() ) );
	}

	public void init(IWorkbench workbench) {
	}
}