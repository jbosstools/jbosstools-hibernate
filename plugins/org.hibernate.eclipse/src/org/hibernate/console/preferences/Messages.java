package org.hibernate.console.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.hibernate.console.preferences.messages"; //$NON-NLS-1$
	public static String ACCP_COULD_NOT_LOAD_PROP_FILE;
	public static String ACCP_NAME_NOT_NULL_OR_EMPTY;
	public static String ACCP_UNKNOWN;
	public static String STANDALONECONSOLECONFIGURATIONPREFERENCES_COULD_NOT_RESOLVE_CLASSPATHS;
	public static String STANDALONECONSOLECONFIGURATIONPREFERENCES_ERRORS_WHILE_PARSING;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
