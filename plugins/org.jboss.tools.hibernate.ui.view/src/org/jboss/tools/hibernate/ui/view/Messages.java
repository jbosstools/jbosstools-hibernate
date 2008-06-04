package org.jboss.tools.hibernate.ui.view;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.hibernate.ui.view.messages"; //$NON-NLS-1$
	public static String BASEUIPLUGIN_HIBERNATE_CONSOLE;
	public static String VIEWPLUGIN_CANOT_LOAD_PREFERENCE_STORE_PROPERTIES;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
