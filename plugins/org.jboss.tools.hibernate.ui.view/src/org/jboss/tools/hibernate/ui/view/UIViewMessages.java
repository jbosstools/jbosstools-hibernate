package org.jboss.tools.hibernate.ui.view;

import org.eclipse.osgi.util.NLS;

public class UIViewMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.hibernate.ui.view.UIViewMessages"; //$NON-NLS-1$
	public static String BaseUIPlugin_hibernate_console;
	public static String ViewPlugin_canot_load_preference_store_properties;
	public static String OrmLabelProvider_element;
	public static String OrmLabelProvider_orm_element;
	public static String OrmLabelProvider_unknown_type_of_element_in_tree_of_type;
	public static String ViewPlugin_no_message_1;
	public static String ViewPlugin_no_message_2;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, UIViewMessages.class);
	}

	private UIViewMessages() {
	}
}
