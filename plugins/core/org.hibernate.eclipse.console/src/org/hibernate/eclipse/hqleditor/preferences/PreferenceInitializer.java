package org.hibernate.eclipse.hqleditor.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import org.hibernate.eclipse.console.HibernateConsolePlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = HibernateConsolePlugin.getDefault()
				.getPreferenceStore();
		PreferenceConverter.setDefault( store, HQLPreferenceConstants.HQL_DEFAULT_COLOR, new RGB( 0, 0, 0 ) );
		PreferenceConverter.setDefault( store, HQLPreferenceConstants.HQL_IDENTIFIER_COLOR, new RGB( 0, 128, 128 ) );
		PreferenceConverter.setDefault( store, HQLPreferenceConstants.HQL_KEYWORD_COLOR, new RGB( 128, 0, 0 ) );
		PreferenceConverter.setDefault( store, HQLPreferenceConstants.HQL_QUOTED_LITERAL_COLOR, new RGB( 0, 0, 255 ) );
		PreferenceConverter.setDefault( store, HQLPreferenceConstants.HQL_COMMENT_COLOR, new RGB( 0, 0, 128 ) );
		
	}

}
