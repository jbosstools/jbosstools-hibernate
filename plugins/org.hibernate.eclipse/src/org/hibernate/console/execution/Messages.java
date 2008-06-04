package org.hibernate.console.execution;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.hibernate.console.execution.messages"; //$NON-NLS-1$
	public static String DEFAULTEXECUTIONCONTEXT_EXISTING_CLASSLOADER;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
