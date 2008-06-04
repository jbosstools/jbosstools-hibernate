package org.hibernate.eclipse.logging;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.hibernate.eclipse.logging.messages"; //$NON-NLS-1$
	public static String PLUGINFILEAPPENDER_MISSING_PLUGIN_STATE_LOCATION;
	public static String PLUGINLOGAPPENDER_MISSING_LAYOUT_FOR_APPENDER;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
